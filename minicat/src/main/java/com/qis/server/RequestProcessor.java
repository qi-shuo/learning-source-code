package com.qis.server;

import com.qis.server.servlet.impl.HttpServlet;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

/**
 * @author qishuo
 * @date 2021/3/21 5:32 下午
 */
public class RequestProcessor extends Thread {

    private Socket socket;
    private Map<String, Map<String, HttpServlet>> servletMap;

    public RequestProcessor(Socket socket, Map<String, Map<String, HttpServlet>> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            String url = request.getUrl();
            String[] split = url.split("/");
            String newUrl = url.substring((split[1] + "/").length());
            Map<String, HttpServlet> stringHttpServletMap;
            if (servletMap.containsKey(split[1])) {
                stringHttpServletMap = servletMap.get(split[1]);
            } else {
                stringHttpServletMap = servletMap.get("/");
            }
            // 静态资源处理
            if (!stringHttpServletMap.containsKey(newUrl)) {
                response.outputHtml(request.getUrl());
            } else {
                // 动态资源servlet请求
                HttpServlet httpServlet = stringHttpServletMap.get(newUrl);
                httpServlet.service(request, response);
            }

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
