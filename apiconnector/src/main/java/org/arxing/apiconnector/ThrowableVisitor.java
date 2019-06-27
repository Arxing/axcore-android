package org.arxing.apiconnector;

public interface ThrowableVisitor {

    void onError(Exception ex);
}
