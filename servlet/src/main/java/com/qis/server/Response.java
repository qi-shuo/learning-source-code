package com.qis.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author qishuo
 * @date 2021/3/21 5:20 下午
 */
public class Response {
    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void outputHtml(String path) throws IOException {
        // 获取静态资源文件的绝对路径
        String absoluteResourcePath = StaticResourceUtil.getAbsolutePath(path);
        File file = new File(absoluteResourcePath);
        if (file.exists()) {
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        } else {
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }

    public void output(String content) throws IOException {
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
    }
}
