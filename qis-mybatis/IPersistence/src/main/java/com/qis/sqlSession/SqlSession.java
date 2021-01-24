package com.qis.sqlSession;

import java.sql.SQLException;
import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 5:05 下午
 */
public interface SqlSession {

    /**
     * 查询list
     *
     * @param statementId
     * @param params
     * @param <E>
     * @return
     * @throws Exception
     */
    <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 查询单个
     *
     * @param statementId
     * @param params
     * @param <T>
     * @return
     * @throws Exception
     */
    <T> T selectOne(String statementId, Object... params) throws Exception;

    /**
     * 生成接口的代理实现类通过jdk动态代理
     *
     * @param mapperClass
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<?> mapperClass);

}
