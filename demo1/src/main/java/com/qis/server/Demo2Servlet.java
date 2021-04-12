package com.qis.server;

import com.qis.server.servlet.impl.HttpServlet;

import java.io.IOException;

/**
 * @author qishuo
 * @date 2021/3/21 4:50 下午
 */
public class Demo2Servlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {


        String content = "<h1>demo2 get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>demo1 post</h1>";
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

