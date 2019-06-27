package org.arxing.apiconnector;

public interface PairVisitor<TKey, TValue> {
    void onVisit(TKey key, TValue value);
}
