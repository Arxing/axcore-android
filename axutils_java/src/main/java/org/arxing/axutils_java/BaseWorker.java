package org.arxing.axutils_java;


public abstract class BaseWorker implements IWorker {

    @Override public void run(Args args) throws Exception {
        preCheckAndSaveArgs(args);
    }

    protected abstract void preCheckAndSaveArgs(Args args) throws Exception;
}
