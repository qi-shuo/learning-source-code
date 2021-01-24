package com.qis.io;

import java.io.InputStream;

/**
 * @author qishuo
 * @date 2021/1/24 11:40 上午
 */
public class Resources {
    public static InputStream getResourceAsSteam(String path) {
        return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
