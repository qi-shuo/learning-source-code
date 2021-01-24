package com.qis.dao;

import com.qis.pojo.User;

import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 8:02 下午
 */
public interface UserDao {
    /**
     * 查询所有
     *
     * @return
     */
    List<User> findAll();

    /**
     * 按条件查询
     *
     * @param userCondition
     * @return
     */
    User findByCondition(User userCondition);
}
