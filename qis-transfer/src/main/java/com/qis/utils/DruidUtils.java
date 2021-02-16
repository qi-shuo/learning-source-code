package com.qis.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author qishuo
 * @date 2021/2/14 2:18 下午
 */
public class DruidUtils {

    private DruidUtils() {
    }

    private static volatile DruidDataSource druidDataSource = null;


    public static DruidDataSource getInstance() {
        if (druidDataSource == null) {
            synchronized (DruidDataSource.class) {
                if (druidDataSource == null) {
                    druidDataSource = new DruidDataSource();
                    druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
                    druidDataSource.setUrl("jdbc:mysql://localhost:3306/qis-transfer");
                    druidDataSource.setUsername("root");
                    druidDataSource.setPassword("123456");
                }
            }
        }
        return druidDataSource;
    }

}

