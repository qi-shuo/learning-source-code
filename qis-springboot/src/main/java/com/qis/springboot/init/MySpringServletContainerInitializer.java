package com.qis.springboot.init;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author qishuo
 * @date 2021/3/9 9:07 下午
 */
@HandlesTypes({MyWebApplicationInitializer.class})
public class MySpringServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {
        if (webAppInitializerClasses.isEmpty()) {
            return;
        }
        List<MyWebApplicationInitializer> list = new ArrayList<>();
        for (Class<?> waiClass : webAppInitializerClasses) {
            //只有不是接口和不是抽象类并且属于MyWebApplicationInitializer.class
            if (!waiClass.isInterface() &&
                    !Modifier.isAbstract(waiClass.getModifiers()) &&
                    //只执行自定义的MyWebApplicationInitializer
                    MyWebApplicationInitializerImpl.class.isAssignableFrom(waiClass)) {
                try {
                    list.add((MyWebApplicationInitializer) waiClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        for (MyWebApplicationInitializer webApplicationInitializer : list) {
            webApplicationInitializer.onStartup(servletContext);
        }
    }
}
