package com.qis.server.servlet;

import com.qis.server.Request;
import com.qis.server.Response;

/**
 * @author qishuo
 * @date 2021/3/21 5:19 下午
 */
public interface Servlet {
    void init() throws Exception;

    void destory() throws Exception;

    void service(Request request, Response response) throws Exception;
}
