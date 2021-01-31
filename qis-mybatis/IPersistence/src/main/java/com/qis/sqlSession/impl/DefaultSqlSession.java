package com.qis.sqlSession.impl;

import com.qis.executor.Executor;
import com.qis.executor.impl.SimpleExecutor;
import com.qis.pojo.Configuration;
import com.qis.pojo.MappedStatement;
import com.qis.sqlSession.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author qishuo
 * @date 2021/1/24 5:06 下午
 */
public class DefaultSqlSession implements SqlSession {
    private final Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(MappedStatement mappedStatement, Object... params) throws Exception {
        Executor executor = new SimpleExecutor();
        return executor.query(configuration, mappedStatement, params);
    }

    @Override
    public <T> T selectOne(MappedStatement mappedStatement, Object... params) throws Exception {
        List<Object> objects = selectList(mappedStatement, params);
        if (objects == null || objects.isEmpty()) {
            //查询为null
            return null;
        }
        if (objects.size() > 1) {
            throw new RuntimeException("查询结果大于1");
        }
        return (T) objects.get(0);
    }

    @Override
    public void update(MappedStatement mappedStatement, Object... params) throws Exception {
        Executor executor = new SimpleExecutor();
        executor.update(configuration, mappedStatement, params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        //class全路径名称
        String className = mapperClass.getName();
        //使用jdk动态代理实现
        return (T) Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, (proxy, method, args) -> {
            //如果是Object定义的方法直接调用
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(args);
            }
            //底层调用selectList或者selectOne
            //参数1
            String statementId = className + "." + method.getName();
            MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
            if (Objects.isNull(statementId)) {
                throw new RuntimeException("没有对应的mapper的映射");
            }

            return this.invoke(method, mappedStatement, args);
        });
    }


    @Override
    public Object invoke(Method method, MappedStatement mappedStatement, Object... params) throws Exception {
        Object result = null;
        String sqlCommandType = mappedStatement.getSqlCommandType().toUpperCase();
        switch (sqlCommandType) {
            case "SELECT":
                Class<?> aClass = method.getReturnType();
                //判断返回是否为多个
                if (Collection.class.isAssignableFrom(aClass) || aClass.isArray()) {
                    result = selectList(mappedStatement, params);
                } else {
                    result = selectOne(mappedStatement, params);
                }
                break;
            case "UPDATE":
                update(mappedStatement, params);
                break;
            case "INSERT":
                update(mappedStatement, params);
                break;
            case "DELETE":
                update(mappedStatement, params);
                break;
            default:
                throw new RuntimeException("未知标签");
        }
        if (result == null && !void.class.equals(method.getReturnType())) {
            throw new RuntimeException("返回结果为null");
        }
        return result;
    }


}
