package org.arxing.axutils_java;

public class Logger {

    public static void print(String format, Object... params) {
        System.out.print(String.format(format, params));
    }

    public static void println(String format, Object... params) {
        System.out.println(String.format(format, params));
    }

    public static void newLine() {
        System.out.println();
    }

    public static void error(Throwable t) {
        println("[ERROR] %s", t.getMessage());
        t.printStackTrace();
    }
}
