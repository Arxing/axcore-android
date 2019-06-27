package org.arxing.axutils_java;

import java.io.IOException;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Date date = TimeUtils.now();
        TimeUtils.setMonth(date, 0);
        System.out.println(TimeUtils.format(TimeUtils.PATTERN_FULL_1, date));
    }
}
