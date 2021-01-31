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

    /**
     * 删除
     *
     * @param userCondition
     */
    void delete(User userCondition);

    /**
     * 更新
     *
     * @param userCondition
     */
    void update(User userCondition);

    /**
     * 新增
     *
     * @param user
     */
    void save(User user);

}
