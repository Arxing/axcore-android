package org.arxing.apiconnector;


import org.arxing.apiconnector.interceptor.RequestInterceptor;

public class TextResponseBodyParser implements ResponseBodyParser {

    @Override public ResponseBodyInfo parse(RequestInfo requestInfo, byte[] byteContent, RequestInterceptor interceptor) throws Exception {
        String content = new String(byteContent, "utf-8");
        if (requestInfo.hasRequestInterceptor()) {
            for (RequestInterceptor requestInterceptor : requestInfo.getRequestInterceptors()) {
                content = requestInterceptor.overrideStringContent(requestInfo, content);
            }
        } else if (interceptor != null)
            content = interceptor.overrideStringContent(requestInfo, content);
        ResponseBodyInfo responseBodyInfo = new TextResponseBodyInfo(content);
        return responseBodyInfo;
    }
}
