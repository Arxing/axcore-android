package org.arxing.apiconnector;


import org.arxing.apiconnector.interceptor.RequestInterceptor;

public class BinaryResponseBodyParser implements ResponseBodyParser {

    @Override
    public ResponseBodyInfo parse(RequestInfo requestInfo, byte[] byteContent, RequestInterceptor interceptor) throws Exception {
        if (requestInfo.hasRequestInterceptor()) {
            for (RequestInterceptor requestInterceptor : requestInfo.getRequestInterceptors()) {
                byteContent = requestInterceptor.overrideBytesContent(requestInfo, byteContent);
            }
        }else if (interceptor != null)
            byteContent = interceptor.overrideBytesContent(requestInfo, byteContent);
        ResponseBodyInfo responseBodyInfo = new BinaryResponseBodyInfo(byteContent);
        return responseBodyInfo;
    }
}
