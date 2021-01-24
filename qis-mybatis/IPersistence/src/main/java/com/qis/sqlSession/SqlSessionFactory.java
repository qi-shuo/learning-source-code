package com.qis.sqlSession;

/**
 * @author qishuo
 * @date 2021/1/24 11:56 上午
 */
public interface SqlSessionFactory {
    /**
     * 创建sqlSession
     *
     * @return
     */
    SqlSession createSqlSession();

}
