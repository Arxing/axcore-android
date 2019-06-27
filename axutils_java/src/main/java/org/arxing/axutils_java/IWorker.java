package org.arxing.axutils_java;


import java.io.File;

public interface IWorker {

    String name();

    void run(Args args) throws Exception;

    default void print(String format, Object... objects) {
        Logger.print(format, objects);
    }

    default void println(String format, Object... objects) {
        Logger.println(format, objects);
    }

    default void println() {
        Logger.println("");
    }

    default void exit() {
        System.exit(0);
    }

    default void startDoing(String format, Object... objects) {
        println(format, objects);
    }

    default String getRuntimePath(Class cls) {
        return new File(cls.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }
}
