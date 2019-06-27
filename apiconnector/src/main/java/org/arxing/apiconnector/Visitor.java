package org.arxing.apiconnector;

public interface Visitor<T> {
    void onVisit(T data);
}
