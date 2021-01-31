package com.qis.executor;

import com.qis.pojo.Configuration;
import com.qis.pojo.MappedStatement;

import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 6:12 下午
 */
public interface Executor {

    /**
     * 执行JDBC代码
     *
     * @param configuration   配置文件
     * @param mappedStatement 关于sql的信息
     * @param params          可变参数
     * @param <E>
     * @return * @throws Exception
     */
    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;


    /**
     * insert,update,delete 都直接执行该方法
     *
     * @param configuration   配置文件
     * @param mappedStatement 关于sql的信息
     * @param params          可变参数
     * @return * @throws Exception
     */
    void update(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

}
