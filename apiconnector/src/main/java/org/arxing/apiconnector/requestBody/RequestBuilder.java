package org.arxing.apiconnector.requestBody;


import org.arxing.apiconnector.RequestInfo;

import okhttp3.Request;

public interface RequestBuilder {

    Request.Builder createBuilder(RequestInfo requestInfo);
}
