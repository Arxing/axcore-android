package org.arxing.apiconnector;

import com.google.gson.TypeAdapterFactory;

import org.arxing.apiconnector.interceptor.RequestInterceptor;
import org.arxing.axutils_android.AssertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 描述了一條單獨的網路請求所需要的資訊
 */
public class RequestInfo {
    private String tag;
    private String url;
    private Class deserializationClass;
    private TypeAdapterFactory typeAdapterFactory;
    private HttpMethod method;
    private ParamBox param;
    private int id;
    private Map<String, String> headers = new HashMap<>();
    private ResponseType responseType;
    private long costTimeMills;
    private boolean ignoreRequestFailed;
    private boolean ignoreParseFailed;
    private boolean isTimeout;
    private List<RequestInterceptor> requestInterceptors = new ArrayList<>();

    private RequestInfo() {
        method = HttpMethod.GET;
        responseType = ResponseType.TEXT;
        param = new ParamBox();
    }

    public String getUrl() {
        return url;
    }

    public Class getDeserializationClass() {
        return deserializationClass;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getTag() {
        return tag;
    }

    public ParamBox getParam() {
        return param;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public TypeAdapterFactory getTypeAdapterFactory() {
        return typeAdapterFactory;
    }

    public boolean isIgnoreRequestFailed() {
        return ignoreRequestFailed;
    }

    public boolean isIgnoreParseFailed() {
        return ignoreParseFailed;
    }

    public List<RequestInterceptor> getRequestInterceptors() {
        return requestInterceptors;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    void visitAllHeader(PairVisitor<String, String> visitor) {
        for (String key : headers.keySet()) {
            visitor.onVisit(key, headers.get(key));
        }
    }

    void startTiming() {
        costTimeMills = System.currentTimeMillis();
    }

    void endTiming() {
        costTimeMills = System.currentTimeMillis() - costTimeMills;
    }

    long getCostTimeMills() {
        return costTimeMills;
    }

    void setTimeout(boolean isTimeout) {
        this.isTimeout = isTimeout;
    }

    boolean isTimeout() {
        return isTimeout;
    }

    public boolean hasRequestInterceptor() {
        return requestInterceptors.size() > 0;
    }

    @Override public String toString() {
        return tag;
    }

    public static class Builder {
        private RequestInfo ins = new RequestInfo();

        public Builder setUrl(String url) {
            ins.url = url;
            return this;
        }

        public Builder setDeserializationClass(Class cls) {
            ins.deserializationClass = cls;
            return this;
        }

        public Builder setMethod(HttpMethod method) {
            ins.method = method;
            return this;
        }

        public Builder setParam(ParamBox param) {
            ins.param = param;
            return this;
        }

        public Builder setTag(String tag) {
            ins.tag = tag;
            return this;
        }

        public Builder putHeader(String key, String value) {
            ins.getHeaders().put(key, value);
            return this;
        }

        public Builder setResponseType(ResponseType type) {
            ins.responseType = type;
            return this;
        }

        public Builder setIgnoreRequestFailed(boolean ignoreRequestFailed) {
            ins.ignoreRequestFailed = ignoreRequestFailed;
            return this;
        }

        public Builder setIgnoreParseFailed(boolean ignoreParseFailed) {
            ins.ignoreParseFailed = ignoreParseFailed;
            return this;
        }

        public Builder addInterceptor(RequestInterceptor interceptor) {
            ins.requestInterceptors.add(interceptor);
            return this;
        }

        public Builder setTypeAdapterFactory(TypeAdapterFactory factory) {
            ins.typeAdapterFactory = factory;
            return this;
        }

        public RequestInfo build() {
            if (ins.tag == null)
                AssertUtils.throwError("Request tag can not be null.");
            return ins;
        }
    }
}
