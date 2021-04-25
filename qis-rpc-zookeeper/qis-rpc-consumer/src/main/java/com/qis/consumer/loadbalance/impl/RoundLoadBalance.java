package com.qis.consumer.loadbalance.impl;

import com.qis.consumer.client.NettyClient;
import com.qis.consumer.loadbalance.LoadBalanceStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 *
 * @author qishuo
 * @date 2021/4/25 10:09 下午
 */
public class RoundLoadBalance implements LoadBalanceStrategy {
    private static AtomicInteger count = new AtomicInteger(0);

    @Override
    public NettyClient doSelect(List<NettyClient> nettyClientList) {

        return nettyClientList.get(count.incrementAndGet() % nettyClientList.size());
    }
}
