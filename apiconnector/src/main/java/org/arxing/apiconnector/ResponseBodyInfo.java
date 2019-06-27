package org.arxing.apiconnector;


import org.arxing.jparser.JParser;

public class ResponseBodyInfo {
    RequestInfo requestInfo;
    boolean responseFailed;
    String requestTag;

    public JsonResponseBodyInfo getAsJson() {
        return (JsonResponseBodyInfo) this;
    }

    public TextResponseBodyInfo getAsText() {
        return (TextResponseBodyInfo) this;
    }

    public BinaryResponseBodyInfo getAsBinary() {
        return (BinaryResponseBodyInfo) this;
    }

    public <T> T getAsInstance() {
        return (T) getAsJson().getInstance();
    }

    public <T> T getAsInstanceOrNull() {
        return getAsInstanceOrDefault(null);
    }

    public <T> T getAsInstanceOrDefault(T def) {
        return getAsJson() == null ? def : (T) getAsJson().getInstance();
    }

    public String getAsStringOrEmpty() {
        return getAsStringOrDefault("");
    }

    public String getAsStringOrDefault(String def) {
        return getAsText() == null ? def : getAsText().getContent();
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public void notifyResponseFailed() {
        this.responseFailed = true;
    }

    public String getRequestTag() {
        return requestTag;
    }

    public boolean isJson() {
        return this instanceof JsonResponseBodyInfo;
    }

    public boolean isBinary() {
        return this instanceof BinaryResponseBodyInfo;
    }

    public boolean isText() {
        return this instanceof TextResponseBodyInfo;
    }

    public String toString() {
        if (this instanceof TextResponseBodyInfo) {
            return getAsText().getContent();
        } else if (this instanceof JsonResponseBodyInfo) {
            return JParser.toPrettyJson(getAsJson().getInstance());
        } else if (this instanceof BinaryResponseBodyInfo) {
            return "";
        }
        return "";
    }
}
