package org.arxing.axutils_android;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileUtils {
    private static Logger logger = new Logger("FileUtils");
    public static final int MODE_PRIVATE = 0x10;
    public static final int MODE_SDCARD = 0x11;

    private static String privatePath;
    private static String sdcardPath;

    private static String getPrivatePath() {
        if (privatePath == null)
            throw new NullPointerException("Lost path value. Please call init(Context) first.");
        return privatePath;
    }

    private static String getSdcardPath() {
        if (sdcardPath == null)
            throw new NullPointerException("Lost path value. Please call init(Context) first.");
        return sdcardPath;
    }

    private static String getBasePathWithMode(int mode) {
        switch (mode) {
            case MODE_PRIVATE:
                return getPrivatePath();
            case MODE_SDCARD:
                return getSdcardPath();
        }
        throw new Error("Wrong mode.");
    }

    private static String parseValidPath(String path) {
        String regex = "(/{2,}|\\\\{2,}|\\\\)";
        String replace = "/";
        return path.replaceAll(regex, replace);
    }

    public static void setShowLog(boolean showLog) {
        logger.setEnable(showLog);
    }

    public static void init(Context context) {
        privatePath = context.getFilesDir().getAbsolutePath();
        sdcardPath = Environment.getExternalStorageDirectory().getPath();
    }

    public static boolean write(String content, String directory, String fileName) {
        return write(content, directory, fileName, MODE_SDCARD);
    }

    public static boolean write(String content, String directory, String fileName, int mode) {
        return write(content, directory, fileName, false, mode);
    }

    public static boolean write(String content, String directory, String fileName, boolean append, int mode) {
        String realDirectory = getBasePathWithMode(mode).concat("/").concat(directory).concat("/");
        return write(content, realDirectory, fileName, append);
    }

    public static boolean write(String content, String directory, String fileName, boolean append) {
        return write(content.getBytes(Charset.defaultCharset()), directory, fileName, append);
    }

    public static boolean write(byte[] content, String directory, String fileName, boolean append) {
        File fDir = new File(parseValidPath(directory));
        File fFile = new File(directory, fileName);
        if (!fDir.exists()) {
            if (!fDir.mkdirs()) {
                logger.i("Make dirs failed, path=%s", fDir.toString());
                return false;
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(fFile, append);
            fos.write(content);
            fos.flush();
            fos.close();
            if (content.length > 1024) {
                double kbSize = content.length / 1024f;
                logger.i("Write file success, path=%s, size=%.2f KB", fFile.toString(), kbSize);
            } else {
                logger.i("Write file success, path=%s, size=%d Bytes", fFile.toString(), content.length);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.i("Write file exception, path=%s, please check stack trace.", fFile.toString());
        return false;
    }
}
