package com.qis.rpc.api;

import com.qis.rpc.pojo.User;

/**
 * @author qishuo1
 * @create 2021/4/12 17:42
 */
public interface IUserService {
    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    User getById(int id);
}
