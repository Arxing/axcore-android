package org.arxing.apiconnector.protocol;


import org.arxing.apiconnector.RequestChain;
import org.arxing.apiconnector.RequestInfo;

public interface ConnectException {
    void onRequestException(String requestTag, RequestInfo request, Exception ex, RequestChain chain);
}
