package org.arxing.apiconnector.protocol;


import org.arxing.apiconnector.RequestChain;
import org.arxing.apiconnector.RequestInfo;

import java.net.SocketTimeoutException;

public interface ConnectTimeout {
    void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex, RequestChain chain);
}
