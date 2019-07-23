package org.arxing.xmath;

import java.lang.reflect.Array;

@SuppressWarnings("all")
public class XMath {

    /**
     * 取得二維陣列中最長的寬度
     *
     * @param array2d 陣列
     * @param <T>     任意類型
     * @return 最長寬度
     */
    public static <T> int array2dMaxWith(T[][] array2d) {
        int max = 0;
        for (T[] element : array2d) {
            if (element.length > max)
                max = element.length;
        }
        return max;
    }

    /**
     * 取得二維陣列中最短的寬度
     *
     * @param array2d 二維陣列
     * @param <T>     任意類型
     * @return 最短寬度
     */
    public static <T> int array2dMinWith(T[][] array2d) {
        int min = Integer.MAX_VALUE;
        for (T[] element : array2d) {
            if (element.length < min)
                min = element.length;
        }
        return min;
    }

    /**
     * 轉置矩陣
     *
     * @param type 一維類型
     * @param src  二維陣列
     * @param <T>  任意類型
     * @return 轉置後的矩陣
     */
    public static <T> T[][] transposeT(Class<T[]> type, T[][] src) {
        Class<T> componentType = (Class<T>) type.getComponentType();
        T[][] dst = (T[][]) Array.newInstance(type, array2dMaxWith(src));
        for (int i = 0; i < Array.getLength(dst); i++) {
            dst[i] = (T[]) Array.newInstance(componentType, src.length);
            for (int j = 0; j < dst[i].length; j++) {
                if (src.length > j && src[j].length > i)
                    dst[i][j] = src[j][i];
            }
        }
        return dst;
    }
}
