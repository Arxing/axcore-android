package org.arxing.apiconnector;

import java.net.SocketTimeoutException;

import okhttp3.Response;

interface ResponseCallback {
    void onResponse(Response response, boolean success, Exception ex) throws Exception;

    void onSocketTimeout(String requestTag, RequestInfo request, SocketTimeoutException ex);

    void onRequestException(String requestTag, RequestInfo request, Exception ex);
}
