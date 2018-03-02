package com.iflytek.im.demo.common.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpPacket extends Packet {
    public enum RequestType {
        POST("POST"), GET("GET");
        private String description;

        RequestType(String representation) {
            this.description = representation;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private final String SEPERATOR = "\r\n";

    private RequestType requestType;
    private String url;
    private Map<String, String> headers;
    private String content;

    public static HttpPacket buildPostRequest(String url, String content) {
        return new HttpPacket(RequestType.POST, url, content);
    }

    public static HttpPacket buildGetRequest(String url, String content) {
        return new HttpPacket(RequestType.GET, url, content);
    }

    private HttpPacket(RequestType requestType, String url, String content) {
        this.requestType = requestType;
        this.url = url;
        this.content = content;
        this.headers = new HashMap<>();
        for (Entry<String, String> entry : headers.entrySet()) {
            headers.put(entry.getKey(), entry.getValue());
        }

    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        for (Entry<String, String> entry : headers.entrySet()) {
            addHeader(entry.getKey(), entry.getValue());
        }
    }

    public RequestType getRequestType() {
        return requestType;
    }

    @Override
    public String getHeader() {
        StringBuilder container = new StringBuilder();
        container.append(getRequestType()).append(" ").append(getUrl()).append(" HTTP/1.1").append(SEPERATOR);
        for (Entry<String, String> entry : getHeaders().entrySet()) {
            container.append(entry.getKey()).append(": ").append(entry.getValue()).append(SEPERATOR);
        }
        container.append(SEPERATOR);
        return container.toString();
    }

    @Override
    public String getBody() {
        if (getContent() != null) {
            return getContent();
        } else {
            return "";
        }
    }
}
