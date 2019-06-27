package org.arxing.apiconnector;

import android.util.Pair;

import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;

public class MultipartRequestBody extends RequestBody {

    private Map<String, List<Param>> map = new HashMap<>();

    public void addTextParam(String key, String value) {
        addTextParams(key, Collections.singletonList(new TextParam(value)));
    }

    public void addTextParam(String key, List<String> values) {
        List<TextParam> params = new ArrayList<>();
        for (String value : values) {
            params.add(new TextParam(value));
        }
        addTextParams(key, params);
    }

    public void addTextParams(String key, List<TextParam> textParams) {
        if (!map.containsKey(key))
            map.put(key, new ArrayList<>());
        map.get(key).addAll(textParams);
    }

    public void addByteParam(String key, String fileName, byte[] bytes, int offset, int length, MediaType mediaType) {
        addByteParams(key, Collections.singletonList(new ByteParam(fileName, bytes, offset, length, mediaType)));
    }

    public void addByteParams(String key, List<ByteParam> byteParams) {
        if (!map.containsKey(key))
            map.put(key, new ArrayList<>());
        map.get(key).addAll(byteParams);
    }

    public void addFileParam(String key, String filePath, String fileName, MediaType mediaType) {
        addFileParams(key, Collections.singletonList(new FileParam(new File(filePath), fileName, mediaType)));
    }

    public void addFileParams(String key, List<FileParam> fileParams) {
        if (!map.containsKey(key))
            map.put(key, new ArrayList<>());
        map.get(key).addAll(fileParams);
    }

    public Observable<Pair<String, Param>> visitParams() {
        return Observable.create(emitter -> Stream.of(map.entrySet()).forEach(entry -> {
            String key = entry.getKey();
            List<Param> values = entry.getValue();
            if (values.size() == 1)
                emitter.onNext(new Pair<>(key, values.get(0)));
            else if (values.size() > 1) {
                Stream.of(values).map(value -> new Pair<>(key + "[" + values.indexOf(value) + "]", value)).forEach(emitter::onNext);
            } else {
                throw new AssertionError("Value.size must > 0.");
            }
        }));
    }

    public static class Param {

    }

    public static class ByteParam extends Param {
        private byte[] bytes;
        private int offset;
        private int length;
        private MediaType mediaType;
        private String fileName;

        public ByteParam(String fileName, byte[] bytes, int offset, int length, MediaType mediaType) {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
            this.mediaType = mediaType;
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public void setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
        }
    }

    public static class FileParam extends Param {
        private File file;
        private String fileName;
        private MediaType mediaType;

        public FileParam(File file, String fileName, MediaType mediaType) {
            this.file = file;
            this.fileName = fileName;
            this.mediaType = mediaType;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public MediaType getMediaType() {
            return mediaType;
        }

        public void setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
        }
    }

    public static class TextParam extends Param {
        private String value;

        public TextParam(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
