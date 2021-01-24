package com.qis.sqlSession.impl;

import com.qis.pojo.Configuration;
import com.qis.sqlSession.SqlSession;
import com.qis.sqlSession.SqlSessionFactory;

/**
 * @author qishuo
 * @date 2021/1/24 11:58 上午
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession createSqlSession() {
        return new DefaultSqlSession(configuration);
    }
}
