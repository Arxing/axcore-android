package org.arxing.apiconnector;


import org.arxing.apiconnector.interceptor.RequestInterceptor;

public interface ResponseBodyParser {

    ResponseBodyInfo parse(RequestInfo requestInfo, byte[] byteContent, RequestInterceptor interceptor) throws Exception;
}
