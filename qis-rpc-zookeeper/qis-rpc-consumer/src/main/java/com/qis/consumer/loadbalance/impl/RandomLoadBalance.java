package com.qis.consumer.loadbalance.impl;

import com.qis.consumer.client.NettyClient;
import com.qis.consumer.loadbalance.LoadBalanceStrategy;

import java.util.List;
import java.util.Random;

/**
 * @author qishuo
 * @date 2021/4/25 9:46 下午
 */
public class RandomLoadBalance implements LoadBalanceStrategy {
    @Override
    public NettyClient doSelect(List<NettyClient> nettyClientList) {
        int length = nettyClientList.size();
        Random random = new Random();
        return nettyClientList.get(random.nextInt(length));
    }
}
