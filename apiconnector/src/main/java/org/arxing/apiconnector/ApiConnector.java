package org.arxing.apiconnector;

import android.os.Handler;
import android.os.Looper;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.arxing.apiconnector.error.ResponseBodyInfoParseError;
import org.arxing.apiconnector.error.ResponseFailedError;
import org.arxing.apiconnector.interceptor.ChainInterceptor;
import org.arxing.apiconnector.interceptor.RequestInterceptor;
import org.arxing.apiconnector.protocol.ConnectCallback;
import org.arxing.apiconnector.protocol.ConnectException;
import org.arxing.apiconnector.protocol.ConnectResponse;
import org.arxing.apiconnector.protocol.ConnectTimeout;
import org.arxing.apiconnector.requestBody.DeleteRequestBuilder;
import org.arxing.apiconnector.requestBody.GetRequestBuilder;
import org.arxing.apiconnector.requestBody.PatchRequestBuilder;
import org.arxing.apiconnector.requestBody.PostRequestBuilder;
import org.arxing.apiconnector.requestBody.PutRequestBuilder;
import org.arxing.apiconnector.requestBody.RequestBuilder;
import org.arxing.axutils_android.FileUtils;
import org.arxing.axutils_android.Logger;
import org.arxing.axutils_android.ThreadUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


@SuppressWarnings({"EmptyCatchBlock", "Convert2MethodRef", "LambdaParameterTypeCanBeSpecified", "unused", "WeakerAccess"})
public class ApiConnector {
    private final static String tag = "ApiConnector";
    private final static long TIMEOUT_CONNECT = 10;
    private final static long TIMEOUT_READ = 10;
    private final static long TIMEOUT_WRITE = 10;
    private OkHttpClient mOkHttpClient;
    private ExecutorService pool;
    private Handler handler = new Handler(Looper.getMainLooper());

    private RequestInterceptor defaultRequestInterceptor;
    private Map<String, String> requestTagMap;
    private Map<String, ProgressResponseBody.OnResponseProgressListener> progressListenerMap;
    private boolean showLog;
    private Logger logger;
    private String debugLogOutputPath;
    private boolean enableDebugLog;

    public static ApiConnector defaultInstance() {
        return new ApiConnector();
    }

    ApiConnector(OkHttpClient.Builder builder) {
        init(builder);
    }

    ApiConnector() {
        init(getDefaultHttpClientBuilder());
    }

    private void init(OkHttpClient.Builder builder) {
        showLog = true;
        logger = new Logger("ApiConnector");
        logger.setEnable(showLog);
        extendClientBuilder(builder);
        mOkHttpClient = builder.build();
        requestTagMap = new ConcurrentHashMap<>();
        progressListenerMap = new ConcurrentHashMap<>();
        pool = Executors.newFixedThreadPool(3);
        registerResponseParsers();
    }

    private void registerResponseParsers() {
        ResponseBodyParserFactory factory = ResponseBodyParserFactory.getInstance();
        factory.registerParser(ResponseType.BINARY, new BinaryResponseBodyParser());
        factory.registerParser(ResponseType.HTML, new HtmlResponseBodyParser());
        factory.registerParser(ResponseType.JSON, new JsonResponseBodyParser());
        factory.registerParser(ResponseType.XML, new XmlResponseBodyParser());
        factory.registerParser(ResponseType.TEXT, new TextResponseBodyParser());
    }

    private void extendClientBuilder(OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(networkProgressInterceptor);
    }

    private OkHttpClient.Builder getDefaultHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS);
        builder.readTimeout(TIMEOUT_READ, TimeUnit.SECONDS);
        builder.writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS);
        return builder;
    }

    private Interceptor networkProgressInterceptor = new Interceptor() {
        @Override public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            String url = request.url().toString();
            String requestTag = requestTagMap.get(url);
            ResponseBody progressBody = new ProgressResponseBody(requestTag, response.body(), responseProgressListener);
            return response.newBuilder().body(progressBody).build();
        }
    };

    private ProgressResponseBody.OnResponseProgressListener responseProgressListener = (requestTag, readCount, contentSize, done) -> {
        ProgressResponseBody.OnResponseProgressListener listener = progressListenerMap.get(requestTag);
        if (listener != null)
            listener.onResponseProgress(requestTag, readCount, contentSize, done);
    };

    /**
     * Build a request from request info.
     */
    private Request distributeRequest(RequestInfo requestInfo) {
        RequestBuilder builder;
        switch (requestInfo.getMethod()) {
            case GET:
                builder = new GetRequestBuilder();
                break;
            case POST:
                builder = new PostRequestBuilder();
                break;
            case PUT:
                builder = new PutRequestBuilder();
                break;
            case PATCH:
                builder = new PatchRequestBuilder();
                break;
            case DELETE:
                builder = new DeleteRequestBuilder();
                break;
            default:
                throw new Error("Unknown method: " + requestInfo.getMethod());
        }
        Request.Builder realBuilder = builder.createBuilder(requestInfo);
        extendRequestBuilder(realBuilder, requestInfo);
        return realBuilder.build();
    }

    private void extendRequestBuilder(final Request.Builder builder, RequestInfo info) {
        info.visitAllHeader(builder::addHeader);
    }

    private Response startConnectSync(Request request, RequestInfo requestInfo) throws Exception {
        String requestTag = requestInfo.getTag();
        String url = request.url().toString();
        requestTagMap.put(url, requestTag);
        Response response = mOkHttpClient.newCall(request).execute();
        requestTagMap.remove(requestTag);
        return response;
    }

    private void startConnectAsync(final Request request,
                                   final RequestInfo requestInfo,
                                   final ResponseCallback callback,
                                   ConnectCallback connectCallback,
                                   boolean callbackOnUiThread) {
        final String requestTag = requestInfo.getTag();
        String url = request.url().toString();
        requestTagMap.put(url, requestTag);
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                try {
                    if (e instanceof SocketTimeoutException) {
                        callback.onSocketTimeout(requestTag, requestInfo, (SocketTimeoutException) e);
                    } else {
                        callback.onResponse(null, false, e);
                    }
                } catch (Exception ex) {
                    callback.onRequestException(requestTag, requestInfo, ex);
                }
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                requestTagMap.remove(requestTag);
                try {
                    int code = response.code();
                    if (code == 200) {
                        callback.onResponse(response, true, null);
                    } else {
                        callback.onResponse(response, false, null);
                    }
                } catch (Exception ex) {
                    callback.onRequestException(tag, requestInfo, ex);
                }
            }
        });
    }

    private void postUserResponse(final RequestChain requestChain,
                                  final ConnectCallback callback,
                                  final boolean success,
                                  final ResponseMap responseMap,
                                  boolean callbackOnUiThread) {
        // Callback on main thread or current thread.
        if (callbackOnUiThread) {
            ThreadUtil.post(() -> callback.onResponse(success, requestChain, responseMap));
        } else {
            callback.onResponse(success, requestChain, responseMap);
        }
    }

    private void postUserTimeout(final RequestChain requestChain,
                                 RequestInfo request,
                                 String requestTag,
                                 ConnectCallback callback,
                                 SocketTimeoutException ex,
                                 boolean callbackOnUiThread) {
        // Callback on main thread or current thread.
        if (callbackOnUiThread) {
            ThreadUtil.post(() -> callback.onSocketTimeout(requestTag, request, ex, requestChain));
        } else {
            callback.onSocketTimeout(requestTag, request, ex, requestChain);
        }
    }

    private void postUserException(final RequestChain requestChain,
                                   RequestInfo request,
                                   String requestTag,
                                   ConnectCallback callback,
                                   Exception ex,
                                   boolean callbackOnUiThread) {
        // Callback on main thread or current thread.
        if (callbackOnUiThread) {
            ThreadUtil.post(() -> callback.onRequestException(requestTag, request, ex, requestChain));
        } else {
            callback.onRequestException(requestTag, request, ex, requestChain);
        }
    }

    private void asyncHandleChain(RequestChain chain,
                                  ConnectCallback callback,
                                  RequestInterceptor interceptor,
                                  boolean callbackOnUiThread) {
        chain.startTiming();
        // Triggered beginning of request chain.
        if (interceptor != null && interceptor instanceof ChainInterceptor)
            ((ChainInterceptor) interceptor).beforeChain(chain);
        iterateChainAsync(chain, callback, interceptor, 0, new ResponseMap(), callbackOnUiThread);
    }

    private boolean breakSet(RequestSet set) {
        if (set.isAllRequestCompleted())
            // Break while-loop if all request is completed.
            return true;
        if (set.isAnyRequestFailed())
            // Break while-loop if any request is failed.
            return true;
        else
            // Or keeping while-loop.
            return false;
    }

    private void iterateChainAsync(RequestChain chain,
                                   ConnectCallback callback,
                                   RequestInterceptor interceptor,
                                   int level,
                                   ResponseMap responseMap,
                                   boolean callbackOnUiThread) {
        RequestSet targetSet = chain.getSet(level);
        targetSet.startTiming();
        targetSet.resetCompletion();
        logger.d("開始遍歷請求鏈, 當前層級=%d/%d, 當前請求數=%d", level, chain.size() - 1, targetSet.size());
        chain.printTree(logger);
        Stream.of(targetSet.getRequests()).forEach(requestInfo -> {
            // Triggered beginning of request.
            if (interceptor != null)
                interceptor.beforeRequest(chain, requestInfo, responseMap);
            requestInfo.startTiming();
            Request request = distributeRequest(requestInfo);
            String strHeaders = Stream.of(requestInfo.getHeaders())
                                      .map(entry -> entry.getKey().concat("=").concat(entry.getValue()))
                                      .collect(Collectors.joining(";"));
            logger.d("[%s] 開始請求", requestInfo.toString());
            logger.d("[%s] Headers: %s", requestInfo.toString(), strHeaders);
            IterateResponseCallback iterateCallback = new IterateResponseCallback(this);
            iterateCallback.setDebugLog(enableDebugLog, debugLogOutputPath);
            iterateCallback.setData(requestInfo, chain, targetSet, interceptor, responseMap, callback, callbackOnUiThread, logger);
            // Iterate all request in current request set and run each request with async.
            startConnectAsync(request, requestInfo, iterateCallback, callback, callbackOnUiThread);
        });

        logger.d("全部請求開始執行, 等待結果...");
        // Waiting all request completed or any one been failed.
        while (!breakSet(targetSet)) {
            ThreadUtil.sleep(10);
        }
        logger.d("執行結束");

        // Stop timing of current request set.
        targetSet.endTiming();

        //在這裡判斷請求集是怎麼結束的
        //可能其中有請求失敗
        //可能是所有請求都完成了
        if (targetSet.isAnyRequestFailed()) {
            //請求集中有請求失敗
            //則判斷為整個請求鏈失敗 回調使用者
            chain.endTiming();
            logger.d("回調, 有任一請求失敗");
            postUserResponse(chain, callback, false, responseMap, callbackOnUiThread);
            //攔截器通知請求鏈結束
            if (interceptor != null && interceptor instanceof ChainInterceptor)
                ((ChainInterceptor) interceptor).afterChain(chain, false, chain.getCostTimeMills());
        } else {
            if ((level + 1) < chain.depth()) {
                logger.d("請求鏈準備執行下一個層級");
                //如果請求鏈中還有下一個請求集 則遞迴執行下一個請求集
                iterateChainAsync(chain, callback, interceptor, level + 1, responseMap, callbackOnUiThread);
            } else {
                //請求鍊中已經執行完所有請求集了 回調使用者
                chain.endTiming();
                //攔截器通知請求鏈結束
                if (interceptor != null && interceptor instanceof ChainInterceptor)
                    ((ChainInterceptor) interceptor).afterChain(chain, true, chain.getCostTimeMills());
                logger.d("回調, 所有請求執行成功");
                postUserResponse(chain, callback, true, responseMap, callbackOnUiThread);
            }
        }
    }

    // lambda語法糖 for requestInfo

    public void asyncResponse(final RequestInfo request, ConnectResponse response, ConnectTimeout timeout, ConnectException exception) {
        asyncResponse(request, new ConnectCallback() {
            @Override public void onResponse(boolean success, RequestChain chain, ResponseMap responseMap) {
                if (response != null)
                    response.onResponse(success, chain, responseMap);
            }

            @Override public void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex, RequestChain chain) {
                if (timeout != null)
                    timeout.onSocketTimeout(requestTag, request, ex, chain);
            }

            @Override public void onRequestException(String requestTag, RequestInfo request, Exception ex, RequestChain chain) {
                if (exception != null)
                    exception.onRequestException(requestTag, request, ex, chain);
            }
        });
    }

    public void asyncResponse(RequestInfo request, ConnectResponse response, ConnectTimeout timeout) {
        asyncResponse(request, response, timeout, null);
    }

    public void asyncResponse(RequestInfo request, ConnectResponse response, ConnectException exception) {
        asyncResponse(request, response, null, exception);
    }

    public void asyncResponse(RequestInfo request, ConnectResponse response) {
        asyncResponse(request, response, null, null);
    }

    // lambda語法糖 for requestChain

    public void asyncResponse(final RequestChain chain, ConnectResponse response, ConnectTimeout timeout, ConnectException exception) {
        asyncResponse(chain, new ConnectCallback() {
            @Override public void onResponse(boolean success, RequestChain chain, ResponseMap responseMap) {
                if (response != null)
                    response.onResponse(success, chain, responseMap);
            }

            @Override public void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex, RequestChain chain) {
                if (timeout != null)
                    timeout.onSocketTimeout(requestTag, request, ex, chain);
            }

            @Override public void onRequestException(String requestTag, RequestInfo request, Exception ex, RequestChain chain) {
                if (exception != null)
                    exception.onRequestException(requestTag, request, ex, chain);
            }
        });
    }

    public void asyncResponse(RequestChain chain, ConnectResponse response, ConnectTimeout timeout) {
        asyncResponse(chain, response, timeout, null);
    }

    public void asyncResponse(RequestChain chain, ConnectResponse response, ConnectException exception) {
        asyncResponse(chain, response, null, exception);
    }

    public void asyncResponse(RequestChain chain, ConnectResponse response) {
        asyncResponse(chain, response, null, null);
    }

    // callback寫法

    public void asyncResponse(final RequestInfo request, final ConnectCallback callback) {
        asyncResponse(RequestChain.build(request), callback, defaultRequestInterceptor, true);
    }

    public void asyncResponse(final RequestChain chain, final ConnectCallback callback) {
        asyncResponse(chain, callback, defaultRequestInterceptor, true);
    }

    public void asyncResponse(final RequestInfo request, final ConnectCallback callback, boolean callbackOnUiThread) {
        asyncResponse(RequestChain.build(request), callback, defaultRequestInterceptor, callbackOnUiThread);
    }

    public void asyncResponse(final RequestChain chain, final ConnectCallback callback, boolean callbackOnUiThread) {
        asyncResponse(chain, callback, defaultRequestInterceptor, callbackOnUiThread);
    }

    public void asyncResponse(RequestInfo request, ConnectCallback callback, RequestInterceptor interceptor) {
        asyncResponse(RequestChain.build(request), callback, interceptor, true);
    }

    public void asyncResponse(RequestInfo request, ConnectCallback callback, RequestInterceptor interceptor, boolean callbackOnUiThread) {
        asyncResponse(RequestChain.build(request), callback, interceptor, callbackOnUiThread);
    }

    public void asyncResponse(final RequestChain chain,
                              final ConnectCallback callback,
                              final RequestInterceptor interceptor,
                              boolean callbackOnUiThread) {
        pool.submit(() -> asyncHandleChain(chain, callback, interceptor, callbackOnUiThread));
    }

    // RX寫法

    @Deprecated public Observable<ResponseBodyInfo> rxResponse(RequestInfo requestInfo) {
        return rxResponse(requestInfo, defaultRequestInterceptor);
    }

    @Deprecated public Observable<ResponseBodyInfo> rxResponse(RequestInfo requestInfo, RequestInterceptor interceptor) {
        return Observable.create((ObservableOnSubscribe<ResponseBodyInfo>) emitter -> {
            Request request = distributeRequest(requestInfo);
            Response response = startConnectSync(request, requestInfo);
            if (response.isSuccessful() || requestInfo.isIgnoreRequestFailed()) {
                try {
                    byte[] byteContent = response.body() == null ? new byte[0] : response.body().bytes();
                    ResponseBodyParser parser = ResponseBodyParserFactory.getInstance().findParser(requestInfo.getResponseType());
                    ResponseBodyInfo bodyInfo = parser.parse(requestInfo, byteContent, interceptor);
                    bodyInfo.requestInfo = requestInfo;
                    emitter.onNext(bodyInfo);
                    emitter.onComplete();
                } catch (Exception e) {
                    if (requestInfo.isIgnoreParseFailed()) {
                        logger.w("ResponseBodyInfo parsed failed, but was ignored at '%s'", requestInfo.getTag());
                        emitter.onComplete();
                    } else {
                        emitter.onError(new ResponseBodyInfoParseError(e));
                    }
                }
            } else {
                emitter.onError(new ResponseFailedError(response.code()));
            }
        }).subscribeOn(Schedulers.from(pool)).doOnError(throwable -> {
            logger.e("rxResponse catch '%s', message='%s'", throwable.getClass().getSimpleName(), throwable.getMessage());
        });
    }

    public Single<ResponseBodyInfo> rxResponse2(final RequestInfo requestInfo) {
        return rxResponse2(requestInfo, defaultRequestInterceptor);
    }

    public Single<ResponseBodyInfo> rxResponse2(final RequestInfo requestInfo, final RequestInterceptor interceptor) {
        return Single.create(emitter -> asyncResponse(requestInfo, new ConnectCallback() {
            String tag = requestInfo.getTag();

            @Override public void onResponse(boolean success, RequestChain chain, ResponseMap responseMap) {
                if (success) {
                    ResponseBodyInfo bodyInfo = responseMap.getBody(tag);
                    emitter.onSuccess(bodyInfo);
                } else {
                    emitter.onError(new ResponseFailedError());
                }
            }

            @Override public void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex, RequestChain chain) {
                emitter.onError(new SocketTimeoutException());
            }

            @Override public void onRequestException(String requestTag, RequestInfo request, Exception ex, RequestChain chain) {
                emitter.onError(ex);
            }
        }, interceptor, true));
    }

    public Single<ResponseMap> rxResponse2(RequestChain chain, RequestInterceptor interceptor) {
        return Single.create(emitter -> asyncResponse(chain, new ConnectCallback() {

            @Override public void onResponse(boolean success, RequestChain chain, ResponseMap responseMap) {
                if (success)
                    emitter.onSuccess(responseMap);
                else {
                    emitter.onError(new ResponseFailedError());
                }
            }

            @Override public void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex, RequestChain chain) {
                emitter.onError(new SocketTimeoutException());
            }

            @Override public void onRequestException(String requestTag, RequestInfo request, Exception ex, RequestChain chain) {
                emitter.onError(ex);
            }
        }, interceptor, true));
    }

    public Single<ResponseMap> concatObservables(Single<ResponseBodyInfo>... bodyInfoArray) {
        Single<ResponseMap> result = Single.just(new ResponseMap());
        for (Single<ResponseBodyInfo> bodyInfo : bodyInfoArray) {
            result = result.zipWith(bodyInfo, (map, body) -> {
                map.putBody(body.getRequestTag(), body);
                return map;
            });
        }
        return result;
    }

    // 其他

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public void setDefaultInterceptor(RequestInterceptor interceptor) {
        this.defaultRequestInterceptor = interceptor;
    }

    public void setProgressListener(String requestTag, ProgressResponseBody.OnResponseProgressListener listener) {
        progressListenerMap.put(requestTag, listener);
    }

    public void removeProgressListener(String requestTag) {
        if (progressListenerMap.containsKey(requestTag))
            progressListenerMap.remove(requestTag);
    }

    public void enableDebugLog(String output) {
        this.enableDebugLog = true;
        this.debugLogOutputPath = output;
    }

    public void disableDebugLog() {
        this.enableDebugLog = false;
        this.debugLogOutputPath = null;
    }

    class IterateResponseCallback implements ResponseCallback {
        private ApiConnector apiConnector;
        private RequestInfo requestInfo;
        private RequestChain chain;
        private RequestSet set;
        private RequestInterceptor interceptor;
        private ResponseMap responseMap;
        private ConnectCallback callback;
        private boolean callbackOnUiThread;
        private Logger logger;
        private boolean enableDebugLog;
        private String debugLogPath;

        public IterateResponseCallback(ApiConnector apiConnector) {
            this.apiConnector = apiConnector;
        }

        void setData(RequestInfo requestInfo,
                     RequestChain chain,
                     RequestSet set,
                     RequestInterceptor interceptor,
                     ResponseMap responseMap,
                     ConnectCallback callback,
                     boolean callbackOnUiThread,
                     Logger logger) {
            this.requestInfo = requestInfo;
            this.chain = chain;
            this.set = set;
            this.interceptor = interceptor;
            this.responseMap = responseMap;
            this.callback = callback;
            this.callbackOnUiThread = callbackOnUiThread;
            this.logger = logger;
        }

        void setDebugLog(boolean enableDebug, String logOutputPath) {
            this.enableDebugLog = enableDebug;
            this.debugLogPath = logOutputPath;
        }

        String getOutputFileSuffix(RequestInfo requestInfo) {
            switch (requestInfo.getResponseType()) {
                case BINARY:
                    return "";
                case HTML:
                    return ".html";
                case JSON:
                    return ".json";
                case TEXT:
                    return ".txt";
            }
            return "";
        }

        @Override public void onResponse(Response response, boolean isRequestSuccess, Exception ex) throws Exception {
            logger.d("[%s] 請求回應, 請求成功=%b", requestInfo.toString(), isRequestSuccess);
            //Response轉換至ResponseBodyInfo是否成功
            boolean isParseSuccess = false;
            ResponseBodyInfo responseBodyInfo = null;
            byte[] byteContent = response == null || response.body() == null ? new byte[0] : response.body().bytes();
            if (isRequestSuccess) {
                try {
                    logger.d("[%s] 開始轉換請求回應...", requestInfo.toString());
                    //由使用者設置的responseType去找到對應的parser
                    ResponseBodyParser parser = ResponseBodyParserFactory.getInstance().findParser(requestInfo.getResponseType());
                    //使用parser將Response轉換成ResponseBodyInfo
                    responseBodyInfo = parser.parse(requestInfo, byteContent, interceptor);
                    responseBodyInfo.requestTag = requestInfo.getTag();
                    isParseSuccess = true;
                } catch (Exception e) {
                    logger.e("[%s] 請求回應轉換發生例外, msg=%s", requestInfo.toString(), e.getMessage());
                    e.printStackTrace();
                }
            }
            requestInfo.endTiming();
            if (enableDebugLog && byteContent.length > 0) {
                String content = new String(byteContent, "utf-8");
                FileUtils.write(content, debugLogPath, requestInfo.getTag() + getOutputFileSuffix(requestInfo));
            }

            // 選擇一個截斷器執行 注意有順序之分
            if (requestInfo.hasRequestInterceptor()) {
                for (RequestInterceptor requestInterceptor : requestInfo.getRequestInterceptors()) {
                    responseBodyInfo = requestInterceptor.overrideResponseBodyInfo(chain,
                                                                                   requestInfo,
                                                                                   responseBodyInfo,
                                                                                   isParseSuccess,
                                                                                   byteContent);
                }
            } else if (interceptor != null) {
                responseBodyInfo = interceptor.overrideResponseBodyInfo(chain, requestInfo, responseBodyInfo, isParseSuccess, byteContent);
            }

            boolean userNotifyFailed = false;

            if (responseBodyInfo != null) {
                if (responseBodyInfo.responseFailed) {
                    userNotifyFailed = true;
                } else {
                    logger.d("[%s] 儲存轉換後的請求回應", requestInfo.toString());
                    if (enableDebugLog) {
                        String content = responseBodyInfo.toString();
                        FileUtils.write(content, debugLogPath, requestInfo.getTag() + ".override" + getOutputFileSuffix(requestInfo));
                    }
                    responseMap.putBody(requestInfo.getTag(), responseBodyInfo);
                }
            } else {
                logger.d("[%s] 轉換後的請求回應為null, 不儲存", requestInfo.toString());
            }

            //攔截器通知請求結束
            if (interceptor != null) {
                interceptor.afterRequest(chain,
                                         requestInfo,
                                         responseMap,
                                         isRequestSuccess && isParseSuccess && !userNotifyFailed,
                                         requestInfo.getCostTimeMills(),
                                         byteContent);
            }

            if (isRequestSuccess && !userNotifyFailed) {
                if ((responseBodyInfo != null && isParseSuccess) || requestInfo.isIgnoreParseFailed()) {
                    logger.d("[%s] 通知Set本次請求完成", requestInfo.toString());
                    set.adjustCompletion();
                } else {
                    logger.e("[%s] 通知Set本次請求失敗", requestInfo.toString());
                    set.notifyRequestFailed();
                }
            } else {
                logger.e("[%s] 請求回應轉換失敗", requestInfo.toString());
                set.notifyRequestFailed();
            }
        }

        @Override public void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex) {
            logger.e("[%s] 請求逾時, 通知Set本次請求失敗", requestInfo.toString());
            request.setTimeout(true);
            set.notifyRequestFailed();
            postUserTimeout(chain, requestInfo, requestTag, callback, ex, callbackOnUiThread);
        }

        @Override public void onRequestException(String requestTag, RequestInfo request, Exception ex) {
            logger.e("[%s] 請求例外, 通知Set本次請求失敗, msg=%s", requestInfo.toString(), ex.getMessage());
            ex.printStackTrace();
            set.notifyRequestFailed();
            postUserException(chain, requestInfo, requestTag, callback, ex, callbackOnUiThread);
        }
    }
}