package com.qis.mvcframework.factory;

import com.qis.mvcframework.annotation.Component;
import com.qis.mvcframework.annotation.Security;
import com.qis.mvcframework.utils.RequestThreadLocalUtil;
import com.qis.mvcframework.utils.ResponseThreadLocalUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * @author qishuo
 * @date 2021/2/28 10:21 下午
 */
@Component
public class ProxyFactory {

    /**
     * Jdk动态代理
     *
     * @param obj 委托对象
     * @return 代理对象
     */
    public Object getJdkProxy(Object obj) {

        // 获取代理对象
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    //如果是equals方法直接判断代理类与目标参数是否相同
                    if (Object.class.equals(method.getDeclaringClass())) {
                        if ("equals".equals(method.getName())) {
                            Object other = args[0];
                            return proxy == other;
                        }
                    }

                    Object result;

                    try {
                        //判断是否是事务方法
                        if (isClassSecurity(obj.getClass()) || isMethodSecurity(obj.getClass().getMethod(method.getName(), method.getParameterTypes()))) {
                            //验证
                            result = security(method, obj, args);
                        } else {
                            result = method.invoke(obj, args);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        throw e;

                    }

                    return result;
                });

    }

    /**
     * 使用cglib动态代理生成代理对象
     *
     * @param obj 委托对象
     * @return
     */
    public Object getCglibProxy(Object obj) {
        return Enhancer.create(obj.getClass(), (MethodInterceptor) (o, method, objects, methodProxy) -> {
            Object result;
            //如果是equals方法直接判断代理类与目标参数是否相同
            if (Object.class.equals(method.getDeclaringClass())) {
                if ("equals".equals(method.getName())) {
                    Object other = objects[0];
                    if (o == other) {
                        return Boolean.TRUE;
                    } else {
                        return Boolean.FALSE;
                    }
                }
            }
            try {
                //判断是否是事务方法
                if (isClassSecurity(obj.getClass()) || isMethodSecurity(method)) {
                    result = security(method, obj, objects);
                } else {
                    result = method.invoke(obj, objects);
                }

            } catch (Exception e) {
                e.printStackTrace();

                throw e;

            }


            return result;
        });
    }

    private Object security(Method method, Object obj, Object[] objects) throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException {
        HttpServletRequest httpServletRequest = RequestThreadLocalUtil.getHttpServletRequest();
        HttpServletResponse httpServletResponse = ResponseThreadLocalUtil.getHttpServletResponse();
        Security annotation = null;
        //获取类上的Security注解
        Class<?> aClass = obj.getClass();
        if (aClass.isAnnotationPresent(Security.class)) {
            annotation = aClass.getAnnotation(Security.class);
        }
        //如果方法上拥有Security注解以方法为标准
        if (aClass.getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(Security.class)) {
            annotation = aClass.getMethod(method.getName(), method.getParameterTypes()).getAnnotation(Security.class);
        }

        if (annotation == null || annotation.values().length == 0) {
            //没有限制
            return method.invoke(obj, objects);

        } else {
            String userName = httpServletRequest.getParameter("userName");
            String[] values = annotation.values();
            for (String value : values) {
                if (value.equals(userName)) {
                    return method.invoke(obj, objects);
                }
            }
            httpServletResponse.getWriter().write(userName + " :用户没有权限");
        }

        return null;
    }

    /**
     * 方法是否是Security
     *
     * @param method
     * @return
     */
    private boolean isMethodSecurity(Method method) {
        return method.isAnnotationPresent(Security.class);
    }

    /**
     * 类是否是Security
     */
    private boolean isClassSecurity(Class<?> clazz) {
        return clazz.isAnnotationPresent(Security.class);
    }
}
