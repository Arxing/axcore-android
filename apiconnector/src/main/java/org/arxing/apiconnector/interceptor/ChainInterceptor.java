package org.arxing.apiconnector.interceptor;


import org.arxing.apiconnector.RequestChain;

public interface ChainInterceptor extends RequestInterceptor {

    void beforeChain(RequestChain chain);

    void afterChain(RequestChain chain, boolean success, long costTimeMills);

}
