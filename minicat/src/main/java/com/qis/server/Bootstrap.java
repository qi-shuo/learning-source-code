package com.qis.server;

import com.qis.server.servlet.impl.HttpServlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author qishuo
 * @date 2021/3/21 10:45 上午
 */
public class Bootstrap {

    public void start() throws IOException {
        Mapper mapper = new Mapper();
        mapper.createMapper();
        Map<String, HttpServlet> stringHttpServletMap = loadServlet();
        Map<String, Map<String, HttpServlet>> servletMap = mapper.getServletMap();
        servletMap.put("/", stringHttpServletMap);
        //创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 100L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        ServerSocket socket = new ServerSocket(Integer.parseInt(mapper.getPort()));
        System.out.println("server socket启动,端口=" + mapper.getPort());
        while (true) {
            Socket accept = socket.accept();
            executor.execute(new RequestProcessor(accept, servletMap));
        }
    }

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private Map<String, HttpServlet>  loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");

        return WebXmlAnalysis.webXmlAnalysis(resourceAsStream);

    }

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动miniCat
            bootstrap.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
