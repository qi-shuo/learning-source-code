package com.qis.rpc.statistics;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统计请求时间
 *
 * @author qishuo
 * @date 2021/4/25 9:51 下午
 */
@Data
@Builder
public class StatisticsRequest {
    private Long lastTime;
    /**
     * ip_port
     */
    private String ipAndPort;


    /**
     * 记录时间
     */
    public static Map<String, StatisticsRequest> statistics_map = new ConcurrentHashMap<>();

}
