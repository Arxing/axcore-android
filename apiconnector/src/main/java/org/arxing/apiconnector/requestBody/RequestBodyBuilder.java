package org.arxing.apiconnector.requestBody;

import android.annotation.SuppressLint;

import org.arxing.apiconnector.BinaryRequestBody;
import org.arxing.apiconnector.FormRequestBody;
import org.arxing.apiconnector.MultipartRequestBody;
import org.arxing.apiconnector.ParamBox;
import org.arxing.apiconnector.RawRequestBody;
import org.arxing.apiconnector.RequestInfo;
import org.arxing.axutils_android.Logger;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class RequestBodyBuilder {
    private static Logger logger = new Logger("RequestBodyBuilder");

    @SuppressLint("CheckResult") static RequestBody build(RequestInfo requestInfo) {
        ParamBox pb = requestInfo.getParam();
        ParamBox.Type type = pb.getType();

        RequestBody requestBody;
        if (type == ParamBox.Type.MULTIPART) {
            //handling post body of multipart
            final MultipartBody.Builder mb = new MultipartBody.Builder();
            mb.setType(MultipartBody.FORM);
            MultipartRequestBody body = pb.getBodyAsMultipart();
            body.visitParams().subscribe(pair -> {
                String key = pair.first;
                MultipartRequestBody.Param param = pair.second;
                if (param instanceof MultipartRequestBody.TextParam) {
                    String value = ((MultipartRequestBody.TextParam) param).getValue();
                    mb.addFormDataPart(key, value);
                    logger.i("\t|key=%s, value=[TEXT]:%s", key, value);
                } else if (param instanceof MultipartRequestBody.FileParam) {
                    MediaType mediaType = ((MultipartRequestBody.FileParam) param).getMediaType();
                    File file = ((MultipartRequestBody.FileParam) param).getFile();
                    String fileName = ((MultipartRequestBody.FileParam) param).getFileName();
                    RequestBody body1 = RequestBody.create(mediaType, file);
                    mb.addFormDataPart(key, fileName, body1);
                    logger.i("\t|key=%s, value=[FILE]:%s;%s;%s", key, mediaType.toString(), file.toString(), fileName);
                } else if (param instanceof MultipartRequestBody.ByteParam) {
                    MultipartRequestBody.ByteParam p = (MultipartRequestBody.ByteParam) param;
                    MediaType mediaType = p.getMediaType();
                    String fileName = p.getFileName();
                    byte[] bytes = p.getBytes();
                    int offset = p.getOffset();
                    int length = p.getLength();
                    RequestBody body1 = RequestBody.create(mediaType, bytes, offset, length);
                    mb.addFormDataPart(key, fileName, body1);
                    double kbSize = bytes.length / 1024f;
                    logger.i("\t|key=%s, value=[BYTE]:%s;%.2fKB;%s", key, mediaType.toString(), kbSize, fileName);
                }
            });
            requestBody = mb.build();
        } else if (type == ParamBox.Type.FORM) {
            final FormBody.Builder fb = new FormBody.Builder();
            FormRequestBody body = pb.getBodyAsForm();
            body.getParamsObservable().subscribe(pair -> {
                String key = pair.first;
                String value = pair.second;
                fb.add(key, value);
                logger.i("key=%s, value=%s", key, value);
            }).dispose();
            requestBody = fb.build();
        } else if (type == ParamBox.Type.RAW) {
            RawRequestBody body = pb.getBodyAsRaw();
            requestBody = RequestBody.create(MediaType.parse(body.getContentType()), body.getContent());
            logger.i("contentType=%s, content=%s", body.getContentType(), body.getContent());
        } else if (type == ParamBox.Type.BINARY) {
            BinaryRequestBody body = pb.getBodyAsBinary();
            requestBody = RequestBody.create(MediaType.parse(body.getContentType()), body.getBytes());
            logger.i("contentType=%s, size=%d", body.getContentType(), body.getBytes().length);
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
        return requestBody;
    }
}
