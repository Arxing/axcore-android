package org.arxing.axutils_android.xhelper;

public class HelperTest {

    public static class Data {
        private String a = "666";

        public int m() {
            return 140;
        }
    }

    static Data data = new Data();

    public static void main(String[] args) {
        XField.of(data).map(o -> o.a).value();
    }
}
