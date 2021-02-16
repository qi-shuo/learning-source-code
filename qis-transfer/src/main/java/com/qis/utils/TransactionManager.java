package com.qis.utils;

import com.qis.annotation.Autowired;
import com.qis.annotation.Component;

import java.sql.SQLException;

/**
 * @author qishuo
 * @date 2021/2/14 2:24 下午
 */
@Component
public class TransactionManager {
    @Autowired
    private ConnectionUtils connectionUtils;



    /**
     * 开启手动事务控制
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }



    /**
     * 事务提交
     * @throws SQLException
     */
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }



    /**
     * 回滚事务
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }
}

