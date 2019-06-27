package org.arxing.apiconnector.protocol;


import org.arxing.apiconnector.RequestChain;
import org.arxing.apiconnector.ResponseMap;

public interface ConnectResponse {
    void onResponse(boolean success, RequestChain chain, ResponseMap responseMap);
}
