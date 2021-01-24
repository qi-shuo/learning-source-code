package com.qis.pojo;

import lombok.Data;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 主配置文件
 *
 * @author qishuo
 * @date 2021/1/24 11:09 上午
 */
@Data
public class Configuration {
    private DataSource dataSource;
    private Map<String, MappedStatement> mappedStatementMap = new HashMap<>();
}
