package com.qis.mvcframework.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qishuo
 * @date 2021/2/28 10:28 下午
 */
public class RequestThreadLocalUtil {
    /**
     * 存储当前线程
     */
    private static ThreadLocal<HttpServletRequest> httpServletRequestThreadLocal = new ThreadLocal<>();

    public static HttpServletRequest getHttpServletRequest() {

        return httpServletRequestThreadLocal.get();
    }

    public static void putHttpServletRequest(HttpServletRequest request) {
        httpServletRequestThreadLocal.set(request);
    }

}
