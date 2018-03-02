package com.iflytek.im.demo.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.CharArrayBuffer;

import android.text.TextUtils;

import com.iflytek.im.demo.common.Logging;


/**
 * Http 响应处理类
 *
 * @author ruiding
 * @date 2015/01/07
 */
@SuppressWarnings("deprecation")
public class Response {
    private String TAG = "Response";
    private final HttpResponse mResponse;
    //private boolean mStreamConsumed = false;

    public Response(HttpResponse res) {
        mResponse = res;
    }

    /**
     * 将httpResponse转换成输入流
     *
     * @return InputStream or null
     * @throws ResponseException
     */
    public InputStream asStream() throws ResponseException {
        if (mResponse == null){
            return null;
        }
        try {
            final HttpEntity entity = mResponse.getEntity();
            if (entity != null) {
                return entity.getContent();
            }
        } catch (IllegalStateException | IOException e) {
            throw new ResponseException(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将httpResponse转换成string
     *
     * @return
     * @throws ResponseException
     */
    public String asString() throws ResponseException {
        if (mResponse == null){
            return null;
        }
        try {
            return entityToString(mResponse.getEntity());
        } catch (IOException e) {
            throw new ResponseException(e.getMessage(), e);
        }
    }

    /**
     * EntityUtils.toString(entity, "UTF-8");
     *
     * @param entity
     * @return
     * @throws java.io.IOException
     * @throws ResponseException
     */
    private String entityToString(final HttpEntity entity) throws IOException,
            ResponseException {

        if (null == entity) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }
        InputStream is = entity.getContent();
        if (is == null) {
            return "";
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "HTTP entity too large to be buffered in memory");
        }

        int i = (int) entity.getContentLength();
        if (i < 0) {
            i = 4096;
        }
        Logging.d(TAG, i + " content length");

        Reader reader = new BufferedReader(new InputStreamReader(is,
                "UTF-8"));
        CharArrayBuffer buffer = new CharArrayBuffer(i);
        try {
            char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
        } finally {
            reader.close();
        }

        return buffer.toString();
    }

    private static Pattern escaped = Pattern.compile("&#([0-9]{3,5});");

    /**
     * Unescape UTF-8 escaped characters to string.
     *
     * @param original The string to be unescaped.
     * @return The unescaped string
     */
    public static String unescape(String original) {
        if (TextUtils.isEmpty(original)){
            return original;
        }
        Matcher mm = escaped.matcher(original);
        StringBuffer unescaped = new StringBuffer();
        while (mm.find()) {
            mm.appendReplacement(unescaped, Character.toString((char) Integer
                    .parseInt(mm.group(1), 10)));
        }
        mm.appendTail(unescaped);
        return unescaped.toString();
    }
}
