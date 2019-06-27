package org.arxing.apiconnector;

import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseMap {
    private Map<String, ResponseBodyInfo> responseBodyInfoMap;
    private Map<String, Code> codeMap;

    public ResponseMap() {
        responseBodyInfoMap = new ConcurrentHashMap<>();
        codeMap = new ConcurrentHashMap<>();
    }

    public void putBody(String key, ResponseBodyInfo bodyInfo) {
        responseBodyInfoMap.put(key, bodyInfo);
    }

    public ResponseBodyInfo getBody(String tag) {
        return responseBodyInfoMap.get(tag);
    }

    public <T> T getBodyAsInstance(String tag) {
        return getBodyAsInstanceOrNull(tag);
    }

    public <T> T getBodyAsInstanceOrNull(String tag) {
        return getBodyAsInstanceOrDefault(tag, null);
    }

    public <T> T getBodyAsInstanceOrDefault(String tag, T defVal) {
        return responseBodyInfoMap.containsKey(tag) ? responseBodyInfoMap.get(tag).getAsInstance() : defVal;
    }

    public <T> T getBodyAsInstanceOrThrow(String tag) throws Exception {
        return getBodyAsInstance(tag);
    }

    public void clear() {
        responseBodyInfoMap.clear();
    }

    public List<String> requestTags() {
        return Stream.of(responseBodyInfoMap.keySet()).toList();
    }

    public void putCode(String key, int code) {
        codeMap.put(key, new Code(code));
    }

    public Code getCode(String key) {
        return codeMap.get(key);
    }

    public static class Code {
        private final int code;
        private final boolean success;

        public Code(int code) {
            this.code = code;
            this.success = (code == 200 || code == 201);
        }

        public int getCode() {
            return code;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
