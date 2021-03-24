package com.qis.springsessiondemo.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @author qishuo
 * @date 2021/3/24 10:42 下午
 */
public class RequestInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        logger.info("sessionId=[{}],session=[{}]", session.getId(), session);
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object attribute = session.getAttribute(name);
            logger.info("session的属性为=[{}],对应的value=[{}]", name, attribute);
        }
        logger.info("当前的uri=[{}]", request.getRequestURI());
        Object userName = session.getAttribute("userName");
        if (Objects.isNull(userName)) {
            logger.info("当前未登录,重定向登录页面");
            response.sendRedirect(request.getContextPath() + "/login/toLogin");
            return false;
        }
        logger.info("当前用户=[{}],已登录", userName);
        return true;
    }
}
