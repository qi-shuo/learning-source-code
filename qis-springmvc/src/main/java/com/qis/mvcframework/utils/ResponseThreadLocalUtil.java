package com.qis.mvcframework.utils;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qishuo
 * @date 2021/2/28 10:29 下午
 */
public class ResponseThreadLocalUtil {
    /**
     * 存储当前线程
     */
    private static ThreadLocal<HttpServletResponse> httpServletResponseThreadLocal = new ThreadLocal<>();

    public static HttpServletResponse getHttpServletResponse() {

        return httpServletResponseThreadLocal.get();
    }

    public static void putHttpServletResponse(HttpServletResponse response) {
        httpServletResponseThreadLocal.set(response);
    }
}
