package com.qis.springboot.init;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author qishuo
 * @date 2021/3/10 11:30 下午
 */
public interface MyWebApplicationInitializer {
    void onStartup(ServletContext servletContext) throws ServletException;
}
