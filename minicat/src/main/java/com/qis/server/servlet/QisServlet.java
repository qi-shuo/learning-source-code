package com.qis.server.servlet;


import com.qis.server.HttpProtocolUtil;
import com.qis.server.Request;
import com.qis.server.Response;
import com.qis.server.servlet.impl.HttpServlet;

import java.io.IOException;

/**
 * @author qishuo
 * @date 2021/3/21 10:48 上午
 */
public class QisServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {


        String content = "<h1>LagouServlet get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>LagouServlet post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destory() throws Exception {

    }

}
