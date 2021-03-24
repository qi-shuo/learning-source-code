package com.qis.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author qishuo
 * @date 2021/3/21 5:21 下午
 */
public class StaticResourceUtil {
    public static String getAbsolutePath(String path) {
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        return absolutePath.replaceAll("\\\\", "/") + path;
    }

    /**
     * 读取静态资源文件输入流,通过输出流输出
     *
     * @param inputStream
     * @param outputStream
     */
    public static void outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {
        int count = 0;
        while (count == 0) {
            count = inputStream.available();
        }
        outputStream.write(HttpProtocolUtil.getHttpHeader200(count).getBytes(StandardCharsets.UTF_8));
        long written = 0;
        int byteSize = 1024;
        byte[] bytes = new byte[byteSize];
        while (written < count) {
            if (written + byteSize > count) {
                byteSize = (int) (count - written);
                bytes = new byte[byteSize];
            }
            inputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();
            written += byteSize;
        }
    }
}
