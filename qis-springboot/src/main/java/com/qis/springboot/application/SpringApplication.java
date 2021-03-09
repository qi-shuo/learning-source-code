package com.qis.springboot.application;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;

/**
 * @author qishuo
 * @date 2021/3/9 9:04 下午
 */
public class SpringApplication {
    public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
    public static void run() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        File baseDir = createTempDir("tomcat");
        //设置连接协议
        Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setThrowOnFailure(true);
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);
        tomcat.setConnector(connector);
        tomcat.getHost().setAutoDeploy(false);
        //表示这是一个webapp
        tomcat.addWebapp("/",baseDir.getAbsolutePath());
        tomcat.start();
    }

    private static File createTempDir(String prefix) {
        try {
            File tempDir = File.createTempFile(prefix + ".", "." + "8080");
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            return tempDir;
        } catch (IOException ex) {
            throw new RuntimeException("无法创建临时目录");
        }
    }
}
