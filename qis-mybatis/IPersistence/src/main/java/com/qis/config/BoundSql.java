package com.qis.config;

import com.qis.utils.ParameterMapping;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储出解析出来的sql和对应参数的名称
 *
 * @author qishuo
 * @date 2021/1/24 6:29 下午
 */
@Data
@Builder
public class BoundSql {
    /**
     * 解析出来的sql将#{}或${}替换为?之后的sql
     */
    private String sql;
    /**
     * 获取参数名称
     */
    private List<ParameterMapping> paramNameList;
}
