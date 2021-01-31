package com.qis.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * sql的映射配置文件
 *
 * @author qishuo
 * @date 2021/1/24 11:51 上午
 */
@Data
@Builder
public class MappedStatement {
    /**
     * ID标识
     */
    private String id;
    /**
     * 返回类型
     */
    private String resultType;
    /**
     * 参数类型
     */
    private String paramType;
    /**
     * sql
     */
    private String sql;
    /**
     * sql类型 select,insert,update,delete,unknown
     */
    private String sqlCommandType;
}
