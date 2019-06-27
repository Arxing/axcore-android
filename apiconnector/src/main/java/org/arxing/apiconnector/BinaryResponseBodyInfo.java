package org.arxing.apiconnector;

public class BinaryResponseBodyInfo extends ResponseBodyInfo {
    private byte[] bytes;

    public BinaryResponseBodyInfo(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
