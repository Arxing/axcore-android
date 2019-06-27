package org.arxing.axutils_java;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.arxing.axutils_java.Logger.println;

public class FileUtils {

    public static File getRuntimePathFile(Class cls) {
        return new File(cls.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    }

    public static String getRuntimePath(Class cls) {
        return getRuntimePathFile(cls).toString();
    }

    public static void copy(File from, File to) throws Exception {
        AssertUtils.exception(!from.exists(), "'%s' is not exists.", from.toString());
        AssertUtils.exception(from.isDirectory(), "directory can not be copied.");
        write(new FileInputStream(from), new FileOutputStream(to));
    }

    public static void copy(String fromPath, String toPath) throws Exception {
        copy(new File(fromPath), new File(toPath));
    }

    public static void copyAllTo(File from, File toFolder) throws Exception {
        AssertUtils.exception(from == null || toFolder == null, "from and to can not be null.");
        AssertUtils.exception(!from.exists(), "'%s' is not exists.", from.toString());
        List<File> fromFiles = new ArrayList<>();
        if (from.isFile())
            fromFiles.add(from);
        else if (from.isDirectory())
            fromFiles.addAll(Stream.of(from.listFiles()).toList());

        AssertUtils.exception(!toFolder.exists() && !toFolder.mkdirs(), "'%s' mkdirs failed.", toFolder.toString());
        AssertUtils.exception(!toFolder.isDirectory(), "'%s' must be directory.", toFolder.toString());
        for (File file : fromFiles) {
            if (file.isDirectory()) {
                copyAllTo(file, new File(toFolder, file.getName()));
            } else {
                String fileName = file.getName();
                copy(file, new File(toFolder, fileName));
            }
        }
    }

    public static void copyAllTo(String fromPath, String toFolderPath) throws Exception {
        copyAllTo(new File(fromPath), new File(toFolderPath));
    }

    public static void deleteAllAt(File folder) throws Exception {
        AssertUtils.exception(folder == null, "folder can not be null.");
        AssertUtils.exception(folder.isFile(), "must be directory.");
        File[] children = folder.listFiles();
        if (children == null)
            return;
        for (File file : children) {
            if (file.isDirectory()) {
                deleteAllAt(file);
            }
            if (!file.delete())
                Logger.println("'%s' 刪除失敗", file);
        }
    }

    public static void deleteAllAt(String folderPath) throws Exception {
        deleteAllAt(new File(folderPath));
    }

    public static ByteArrayOutputStream read(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        write(in, out, false);
        return out;
    }

    public static ByteArrayOutputStream read(File file) throws IOException {
        return read(new FileInputStream(file));
    }

    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = read(in);
        byte[] result = baos.toByteArray();
        baos.close();
        return result;
    }

    public static byte[] readBytes(File file) throws IOException {
        return readBytes(new FileInputStream(file));
    }

    public static byte[] readBytes(String path) throws IOException {
        return readBytes(new File(path));
    }

    public static String readString(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            stringBuilder.append(line).append("\n");
        return stringBuilder.toString();
    }

    public static String readString(File file) throws IOException {
        return readString(new FileInputStream(file));
    }

    public static String readString(String path) throws IOException {
        return readString(new File(path));
    }

    public static <T> T readJson(InputStream is, Type type) throws Exception {
        return JParser.fromJson(readString(is), type);
    }

    public static <T> T readJson(File file, Type type) throws Exception {
        return readJson(new FileInputStream(file), type);
    }

    public static <T> T readJson(String path, Type type) throws Exception {
        return readJson(new File(path), type);
    }

    public static void write(InputStream in, OutputStream out, boolean release) throws IOException {
        int readCount;
        byte[] buffer = new byte[1024];
        while ((readCount = in.read(buffer)) > 0)
            out.write(buffer, 0, readCount);
        if (release) {
            out.flush();
            out.close();
            in.close();
        }
    }

    public static void write(InputStream in, OutputStream out) throws IOException {
        write(in, out, true);
    }

    public static void write(InputStream in, File fileOut) throws IOException {
        write(in, new FileOutputStream(fileOut));
    }

    public static void write(File fileIn, File fileOut) throws IOException {
        write(new FileInputStream(fileIn), new FileOutputStream(fileOut));
    }

    public static void write(byte[] bytes, File file) throws IOException {
        write(new ByteArrayInputStream(bytes), file);
    }

    public static void write(String content, File file) throws IOException {
        write(content.getBytes(StandardCharsets.UTF_8), file);
    }

    public static void writeToJson(Object object, File file) throws Exception {
        write(JParser.toPrettyJson(object), file);
    }

    public static void writeToJson(Object object, String path) throws Exception {
        writeToJson(object, new File(path));
    }

    /**
     * <pre>
     * 輸入多個檔案路徑 從中取出最短的相等目錄
     * Example:
     * Input /A/B/C/D/apple1.jpg
     *       /A/B/C/D/E/apple2.jpg
     *       /A/B/apple3.jpg
     * Output
     *       /A/B/
     *
     * @param files 輸入路徑
     * @return 最短相等目錄
     */
    public static String computeShortestPath(List<File> files) {
        return Stream.of(files)
                     .filter(File::isFile)
                     .map(File::getParentFile)
                     .distinct()
                     .map(o -> Stream.of(o.toPath().iterator()).toList())
                     .reduce((out, in) -> {
                         List<Path> result = new ArrayList<>();
                         for (int i = 0; i < out.size() && i < in.size(); i++) {
                             Path pOut = out.get(i);
                             Path pIn = in.get(i);
                             if (pOut.equals(pIn)) {
                                 result.add(pOut);
                             }
                         }
                         return new ArrayList<>(result);
                     })
                     .map(o -> Stream.of(o).map(p -> p.toString()).collect(Collectors.joining("/")))
                     .get();
    }

    public static void unzip(File zipIn, File outDir) throws IOException {
        File outFile;
        ZipFile zipFile = new ZipFile(zipIn);
        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(zipIn));
        ZipEntry entry;
        InputStream input;
        while ((entry = zipInput.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                outFile = new File(outDir, entry.getName());
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }
                input = zipFile.getInputStream(entry);
                write(input, outFile);
            }
        }
    }

    public static void unzip(String zipPath, String outDir) throws IOException {
        unzip(new File(zipPath), new File(outDir));
    }

    public static void zip(List<File> files, File zipOut) throws IOException {
        //先找出最短路徑
        String shortestPath = computeShortestPath(files);
        FileInputStream fis;
        ZipOutputStream zOut = new ZipOutputStream(new FileOutputStream(zipOut));
        for (File file : files) {
            if (file.isFile()) {
                fis = new FileInputStream(file);
                File shortestFile = new File(file.toPath().getRoot().toFile(), shortestPath);
                String relative = shortestFile.toPath().relativize(file.toPath()).toString();
                ZipEntry entry = new ZipEntry(relative);
                println("file %s", file);
                println("entry %s", relative);
                zOut.putNextEntry(entry);
                write(fis, zOut, false);
                fis.close();
            }
        }
        zOut.flush();
        zOut.close();
    }

    public static void zip(List<File> files, String zipOutPath) throws IOException {
        zip(files, new File(zipOutPath));
    }

    public static boolean exists(File file) {
        try {
            AssertUtils.exception(file == null, "file can not be null.");
            return file.exists();
        } catch (Exception e) {
            Logger.error(e);
            return false;
        }
    }

    public static boolean exists(String path) {
        return exists(new File(path));
    }

}
