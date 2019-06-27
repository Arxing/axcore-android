package org.arxing.apiconnector.error;

public class ResponseFailedError extends Error {
    public int code;

    public ResponseFailedError(int code) {
        this.code = code;
    }

    public ResponseFailedError() {
    }
}
