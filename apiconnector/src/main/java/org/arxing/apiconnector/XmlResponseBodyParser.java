package org.arxing.apiconnector;


import org.arxing.apiconnector.interceptor.RequestInterceptor;

public class XmlResponseBodyParser implements ResponseBodyParser {

    @Override public ResponseBodyInfo parse(RequestInfo requestInfo, byte[] byteContent, RequestInterceptor interceptor) throws Exception {
        return null;
    }
}
