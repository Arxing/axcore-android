package org.arxing.apiconnector.requestBody;



import org.arxing.apiconnector.RequestInfo;
import org.arxing.axutils_android.Logger;

import okhttp3.Request;

public class GetRequestBuilder implements RequestBuilder {
    private Logger logger = new Logger("ApiConnector");

    @Override public Request.Builder createBuilder(RequestInfo requestInfo) {
        String url = requestInfo.getUrl();
        url += requestInfo.getParam().transGet();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        logger.i("建立Get請求, tag=%s, url=%s", requestInfo.getTag(), url);
        return builder;
    }
}
