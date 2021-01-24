package com.qis.sqlSession.impl;

import com.qis.executor.Executor;
import com.qis.executor.impl.SimpleExecutor;
import com.qis.pojo.Configuration;
import com.qis.sqlSession.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 5:06 下午
 */
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        Executor executor = new SimpleExecutor();
        return executor.query(configuration, configuration.getMappedStatementMap().get(statementId), params);
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
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
    public <T> T getMapper(Class<?> mapperClass) {
        //class全路径名称
        String className = mapperClass.getName();
        //使用jdk动态代理实现
        return (T) Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, (proxy, method, args) -> {
            //底层调用selectList或者selectOne
            //参数1
            String statementId = className + "." + method.getName();
            Type genericReturnType = method.getGenericReturnType();
            //判断是不是参数是不是类型化
            if (genericReturnType instanceof ParameterizedType) {
                return selectList(statementId, args);
            } else {
                return selectOne(statementId, args);
            }
        });
    }
}
