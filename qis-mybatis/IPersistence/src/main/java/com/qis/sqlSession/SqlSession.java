package com.qis.sqlSession;

import com.qis.pojo.MappedStatement;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 5:05 下午
 */
public interface SqlSession {

    /**
     * 查询list
     *
     * @param mappedStatement
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    <E> List<E> selectList(MappedStatement mappedStatement, Object... params) throws Exception;

    /**
     * 查询单个
     *
     * @param mappedStatement
     * @param params
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T selectOne(MappedStatement mappedStatement, Object... params) throws Exception;


    /**
     * 生成接口的代理实现类通过jdk动态代理
     *
     * @param mapperClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<?> mapperClass);

    /**
     * update,insert,delete都执行该方法
     *
     * @param mappedStatement
     * @param params
     */
    void update(MappedStatement mappedStatement, Object... params) throws Exception;



    /**
     * 代理类执行的方法
     *
     * @param method
     * @param mappedStatement
     * @param params
     * @return
     */
    Object invoke(Method method, MappedStatement mappedStatement, Object... params) throws Exception;

}
