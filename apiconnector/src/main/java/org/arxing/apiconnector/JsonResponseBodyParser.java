package org.arxing.apiconnector;


import org.arxing.apiconnector.interceptor.RequestInterceptor;
import org.arxing.jparser.JParser;
import org.arxing.utils.Logger;

public class JsonResponseBodyParser implements ResponseBodyParser {
    private Logger logger = new Logger("ApiConnector");

    // 若轉換過程中發生例外 則拋出給外部處理
    @Override public ResponseBodyInfo parse(RequestInfo requestInfo, byte[] byteContent, RequestInterceptor interceptor) throws Exception {
        String content = new String(byteContent, "utf-8");
        if (requestInfo.hasRequestInterceptor()) {
            for (RequestInterceptor requestInterceptor : requestInfo.getRequestInterceptors()) {
                content = requestInterceptor.overrideStringContent(requestInfo, content);
            }
        } else if (interceptor != null)
            content = interceptor.overrideStringContent(requestInfo, content);
        Class type = requestInfo.getDeserializationClass();
        Object instance;
        boolean success = true;
        if (requestInfo.getTypeAdapterFactory() != null)
            instance = JParser.fromJson(content, requestInfo.getDeserializationClass(), requestInfo.getTypeAdapterFactory());
        else
            instance = JParser.fromJson(content, requestInfo.getDeserializationClass());
        ResponseBodyInfo responseBodyInfo = new JsonResponseBodyInfo(type, instance);
        return responseBodyInfo;
    }
}
