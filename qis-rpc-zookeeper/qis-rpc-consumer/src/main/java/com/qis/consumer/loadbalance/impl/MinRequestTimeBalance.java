package com.qis.consumer.loadbalance.impl;

import com.qis.consumer.client.NettyClient;
import com.qis.consumer.loadbalance.LoadBalanceStrategy;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.qis.rpc.statistics.StatisticsRequest.statistics_map;

/**
 * @author qishuo
 * @date 2021/4/25 9:45 下午
 */
public class MinRequestTimeBalance implements LoadBalanceStrategy {

    LoadBalanceStrategy randomLoadBalance = new RandomLoadBalance();
    LoadBalanceStrategy roundLoadBalance = new RoundLoadBalance();

    @Override
    public NettyClient doSelect(List<NettyClient> nettyClientList) {
        if (CollectionUtils.isEmpty(nettyClientList)) {
            return null;
        }
        //只有一个直接返回
        if (nettyClientList.size() <= 1) {
            return nettyClientList.get(0);
        }
        //statistics_map中没有填满轮询
        if (CollectionUtils.isEmpty(statistics_map) || statistics_map.size() < nettyClientList.size()) {

            return roundLoadBalance.doSelect(nettyClientList);
        }
        long minTime = Long.MAX_VALUE;

        String ipAndPort = "";
        List<NettyClient> minNettyClient = new ArrayList<>();
        for (NettyClient nettyClient : nettyClientList) {
            Long lastTime = statistics_map.get(nettyClient.getIpAndPort()).getLastTime();
            if (lastTime < minTime) {
                ipAndPort = nettyClient.getIpAndPort();
                minTime = lastTime;
                minNettyClient.clear();
                minNettyClient.add(nettyClient);
            } else if (lastTime == minTime) {
                minNettyClient.add(nettyClient);
            }
        }
        if (minNettyClient.size() > 1) {
            return randomLoadBalance.doSelect(minNettyClient);
        }

        Map<String, NettyClient> ipPortMap = nettyClientList.stream().collect(Collectors.toMap(NettyClient::getIpAndPort, v -> v));

        return ipPortMap.get(ipAndPort);
    }
}
