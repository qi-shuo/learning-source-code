package com.qis.dao;

import com.qis.pojo.User;

import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 10:24 下午
 */
public interface UserMapper {
    /**
     * 查询所有
     *
     * @return
     */
    List<User> findAll();

}
