package com.qis.rpc.provider.datasource;

import com.qis.rpc.pojo.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qishuo
 * @date 2021/4/12 9:55 下午
 */
public class UserData {
    private static final Map<Integer, User> USER_MAP = new HashMap<>();

    static {
        User user1 = new User();
        user1.setId(1);
        user1.setName("张三");
        User user2 = new User();
        user2.setId(2);
        user2.setName("李四");
        putUser(user1.getId(), user1);
        putUser(user2.getId(), user2);
    }

    public static User getUser(Integer id) {
        return USER_MAP.getOrDefault(id, null);
    }

    public static void putUser(Integer id, User user) {
        USER_MAP.put(id, user);
    }

    public static int userSize() {
        return USER_MAP.size();
    }
}
