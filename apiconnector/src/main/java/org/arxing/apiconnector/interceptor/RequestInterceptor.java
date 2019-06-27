package org.arxing.apiconnector.interceptor;


import org.arxing.apiconnector.RequestChain;
import org.arxing.apiconnector.RequestInfo;
import org.arxing.apiconnector.ResponseBodyInfo;
import org.arxing.apiconnector.ResponseMap;

public interface RequestInterceptor {

    void beforeRequest(RequestChain chain, RequestInfo requestInfo, ResponseMap responseMap);

    void afterRequest(RequestChain chain,
                      RequestInfo requestInfo,
                      ResponseMap responseMap,
                      boolean success,
                      long costTimeMills,
                      byte[] content);

    ResponseBodyInfo overrideResponseBodyInfo(RequestChain chain,
                                              RequestInfo requestInfo,
                                              ResponseBodyInfo response,
                                              boolean isParseSuccess,
                                              byte[] content);

    String overrideStringContent(RequestInfo requestInfo, String content);

    byte[] overrideBytesContent(RequestInfo requestInfo, byte[] content);
}
