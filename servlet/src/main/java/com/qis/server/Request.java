package com.qis.server;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author qishuo
 * @date 2021/3/21 5:19 下午
 */
public class Request {
    /**
     * 请求方式比如get/post
     */
    private String method;
    /**
     * 请求地址比如/.hello.html
     */
    private String url;
    /**
     * 输入流,其他属性从输入流中解析出来
     */
    private InputStream inputStream;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Request() {
    }

    public Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }
        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        String inputStr = new String(bytes);
        // GET /HTTP/1.1
        String firstLineStr = inputStr.split("\\n")[0];

        String[] strings = firstLineStr.split(" ");
        this.method = strings[0];
        this.url = strings[1];
    }
}
