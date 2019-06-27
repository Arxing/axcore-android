package org.arxing.apiconnector.requestBody;

import android.annotation.SuppressLint;

import org.arxing.apiconnector.ParamBox;
import org.arxing.apiconnector.RequestInfo;
import org.arxing.axutils_android.Logger;

import okhttp3.Request;
import okhttp3.RequestBody;

public class PatchRequestBuilder implements RequestBuilder {
    private Logger logger = new Logger("ApiConnector");

    @SuppressLint("CheckResult") @Override public Request.Builder createBuilder(RequestInfo requestInfo) {
        String url = requestInfo.getUrl();
        Request.Builder builder = new Request.Builder();
        builder.url(url);

        ParamBox pb = requestInfo.getParam();
        ParamBox.Type type = pb.getType();
        logger.i("建立PATCH請求, tag=%s, type=%s, url=%s", requestInfo.getTag(), type.name(), url);
        RequestBody requestBody = RequestBodyBuilder.build(requestInfo);
        builder.patch(requestBody);
        return builder;
    }
}
