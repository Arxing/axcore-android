package org.arxing.apiconnector;

public class TextResponseBodyInfo extends ResponseBodyInfo {
    private String content;

    public TextResponseBodyInfo(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
