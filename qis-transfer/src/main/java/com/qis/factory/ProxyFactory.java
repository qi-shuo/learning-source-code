package com.qis.factory;

import com.qis.annotation.Autowired;
import com.qis.annotation.Component;
import com.qis.annotation.Transactional;
import com.qis.utils.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

/**
 * @author qishuo
 * @date 2021/2/14 2:24 下午
 */
@Component
@Slf4j
public class ProxyFactory {
    @Autowired
    private TransactionManager transactionManager;

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
                        if (isClassTransaction(obj.getClass()) || isMethodTransaction(obj.getClass().getMethod(method.getName(), method.getParameterTypes()))) {
                            result = transaction(method, obj, args);
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
                if (isClassTransaction(obj.getClass()) || isMethodTransaction(method)) {
                    result = transaction(method, obj, objects);
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

    /**
     * 方法是否是事务方法
     *
     * @param method
     * @return
     */
    private boolean isMethodTransaction(Method method) {
        return method.getAnnotation(Transactional.class) != null;
    }

    /**
     * 类是否是事务的类
     */
    private boolean isClassTransaction(Class<?> clazz) {
        return clazz.getAnnotation(Transactional.class) != null;
    }

    /**
     * 处理事务
     *
     * @param method
     * @param obj
     * @param args
     * @return
     */
    private Object transaction(Method method, Object obj, Object[] args) throws SQLException, InvocationTargetException, IllegalAccessException {
        Object result;
        try {
            // 开启事务(关闭事务的自动提交)
            transactionManager.beginTransaction();

            result = method.invoke(obj, args);

            // 提交事务

            transactionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            transactionManager.rollback();
            // 抛出异常便于上层servlet捕获
            throw e;
        }

        return result;
    }

}
