package org.arxing.apiconnector;


import java.io.IOException;

import io.reactivex.annotations.Nullable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {
    private ResponseBody targetBody;
    private String requestTag;
    private OnResponseProgressListener listener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(String requestTag, ResponseBody targetBody, OnResponseProgressListener listener) {
        this.targetBody = targetBody;
        this.requestTag = requestTag;
        this.listener = listener;
    }

    @Nullable @Override public MediaType contentType() {
        return targetBody.contentType();
    }

    @Override public long contentLength() {
        return targetBody.contentLength();
    }

    @Override public BufferedSource source() {
        if (bufferedSource == null)
            bufferedSource = Okio.buffer(bundleSource(targetBody.source()));
        return bufferedSource;
    }

    private Source bundleSource(Source source) {
        return new ForwardingSource(source) {

            long totalRead = 0;

            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long byteRead = super.read(sink, byteCount);
                totalRead += byteRead != -1 ? byteRead : 0;
                boolean done = byteRead == -1;
                listener.onResponseProgress(requestTag, totalRead, contentLength(), done);
                return byteRead;
            }
        };
    }

    public interface OnResponseProgressListener {
        void onResponseProgress(String requestTag, long readCount, long contentSize, boolean done);
    }
}
