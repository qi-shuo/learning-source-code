package com.qis.enums;

/**
 * @author qishuo
 * @date 2021/1/31 5:51 下午
 */
public enum SqlCommandTypeEnum {
    /**
     * 查询
     */
    SELECT("select"),
    /**
     * 新增
     */
    INSERT("insert"),
    /**
     * 更新
     */
    UPDATE("update"),
    /**
     * 删除
     */
    DELETE("delete"),
    /**
     * 未知
     */
    UNKNOWN("unknown");
    private final String type;

    SqlCommandTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
