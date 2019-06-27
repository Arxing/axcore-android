package org.arxing.apiconnector;

import android.net.Uri;

import org.arxing.jparser.JParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;

@SuppressWarnings("DefaultLocale")
public class ParamBox {
    public enum Type {
        MULTIPART,
        FORM,
        RAW,
        BINARY
    }

    private Type type;
    private RequestBody requestBody;

    public ParamBox() {
        this(Type.FORM);
    }

    public ParamBox(Type type) {
        this.type = type;
        this.requestBody = buildRequestBody(type);
    }

    private RequestBody buildRequestBody(Type type) {
        switch (type) {
            case MULTIPART:
                return new MultipartRequestBody();
            case FORM:
                return new FormRequestBody();
            case RAW:
                return new RawRequestBody();
            case BINARY:
                return new BinaryRequestBody();
        }
        throw new AssertionError("Error type.");
    }

    private void throwInvalidTypeException() {
        try {
            throw new Exception("Invalid type.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Type getType() {
        return type;
    }

    public MultipartRequestBody getBodyAsMultipart() {
        return (MultipartRequestBody) requestBody;
    }

    public FormRequestBody getBodyAsForm() {
        return (FormRequestBody) requestBody;
    }

    public RawRequestBody getBodyAsRaw() {
        return (RawRequestBody) requestBody;
    }

    public BinaryRequestBody getBodyAsBinary() {
        return (BinaryRequestBody) requestBody;
    }

    public ParamBox add(String key, List<String> values) {
        switch (type) {
            case MULTIPART:
                getBodyAsMultipart().addTextParam(key, values);
                break;
            case FORM:
                getBodyAsForm().addParam(key, values);
                break;
            default:
                throwInvalidTypeException();
        }
        return this;
    }

    public ParamBox addJson(String key, Object model) {
        return add(key, JParser.toJson(model));
    }

    public ParamBox add(String key, Object value) {
        return add(key, String.valueOf(value));
    }

    public ParamBox addIfNotNull(String key, Object value) {
        return value == null ? this : add(key, String.valueOf(value));
    }

    public ParamBox add(String key, String value) {
        return add(key, Collections.singletonList(value));
    }

    public ParamBox addIfNotNull(String key, String value) {
        return value == null ? this : add(key, Collections.singletonList(value));
    }

    public ParamBox add(String key, String... values) {
        return add(key, Arrays.asList(values));
    }

    public ParamBox addFile(String key, String fileName, byte[] bytes, MediaType mediaType) {
        return addFile(key, fileName, bytes, 0, bytes.length, mediaType);
    }

    public ParamBox addFileIfNotNull(String key, String fileName, byte[] bytes, MediaType mediaType) {
        return fileName != null && bytes != null ? addFile(key, fileName, bytes, 0, bytes.length, mediaType) : this;
    }

    public ParamBox addFile(String key, String fileName, byte[] bytes, int offset, int length, MediaType mediaType) {
        switch (type) {
            case MULTIPART:
                getBodyAsMultipart().addByteParam(key, fileName, bytes, offset, length, mediaType);
                break;
            default:
                throwInvalidTypeException();
        }
        return this;
    }

    public ParamBox addFileIfNotNull(String key, String filePath, String fileName, MediaType mediaType) {
        return filePath != null && fileName != null ? addFile(key, filePath, fileName, mediaType) : this;
    }

    public ParamBox addFile(String key, String filePath, String fileName, MediaType mediaType) {
        if (filePath == null || fileName == null)
            return this;
        switch (type) {
            case MULTIPART:
                getBodyAsMultipart().addFileParam(key, filePath, fileName, mediaType);
                break;
            default:
                throwInvalidTypeException();
        }
        return this;
    }

    public ParamBox setRawData(String contentType, String data) {
        switch (type) {
            case RAW:
                getBodyAsRaw().setContentType(contentType);
                getBodyAsRaw().setContent(data);
                break;
            default:
                throwInvalidTypeException();
        }
        return this;
    }

    public ParamBox setBinary(byte[] bytes) {
        switch (type) {
            case BINARY:
                getBodyAsBinary().setBytes(bytes);
                break;
            default:
                throwInvalidTypeException();
        }
        return this;
    }

    public String transGet() {
        final StringBuilder sb = new StringBuilder("?");
        Disposable d = getBodyAsForm().getParamsObservable().subscribe(pair -> {
            sb.append(Uri.encode(pair.first));
            sb.append("=");
            sb.append(Uri.encode(pair.second));
            sb.append("&");
        });
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
