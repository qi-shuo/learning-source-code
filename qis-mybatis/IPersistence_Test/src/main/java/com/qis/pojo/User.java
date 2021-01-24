package com.qis.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author qishuo
 * @date 2021/1/24 11:24 上午
 */
@Data
@ToString
public class User {
    private Integer id;
    private String username;
    private String password;
    private String birthday;
}
