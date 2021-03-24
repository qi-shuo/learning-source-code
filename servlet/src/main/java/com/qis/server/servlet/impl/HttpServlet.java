package com.qis.server.servlet.impl;

import com.qis.server.Request;
import com.qis.server.Response;
import com.qis.server.servlet.Servlet;

/**
 * @author qishuo
 * @date 2021/3/21 5:20 下午
 */
public abstract class HttpServlet implements Servlet {
    public abstract void doGet(Request request, Response response);

    public abstract void doPost(Request request, Response response);


    @Override
    public void service(Request request, Response response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }
}
