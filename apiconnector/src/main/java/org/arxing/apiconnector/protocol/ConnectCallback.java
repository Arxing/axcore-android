package org.arxing.apiconnector.protocol;


import org.arxing.apiconnector.RequestChain;
import org.arxing.apiconnector.RequestInfo;
import org.arxing.apiconnector.ResponseMap;

import java.net.SocketTimeoutException;

public interface ConnectCallback {
    void onResponse(boolean success, RequestChain chain, ResponseMap responseMap);

    void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex, RequestChain chain);

    void onRequestException(String requestTag, RequestInfo request, Exception ex, RequestChain chain);
}
