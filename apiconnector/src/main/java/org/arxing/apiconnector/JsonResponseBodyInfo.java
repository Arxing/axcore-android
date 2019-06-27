package org.arxing.apiconnector;

public class JsonResponseBodyInfo<T> extends ResponseBodyInfo {
    private Class<T> type;
    private T instance;

    public JsonResponseBodyInfo(Class<T> type, T instance) {
        this.type = type;
        this.instance = instance;
    }

    public T getInstance() {
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
