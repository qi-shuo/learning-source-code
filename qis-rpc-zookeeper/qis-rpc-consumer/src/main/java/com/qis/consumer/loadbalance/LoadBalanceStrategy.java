package com.qis.consumer.loadbalance;

import com.qis.consumer.client.NettyClient;

import java.util.List;

/**
 * @author qishuo
 * @date 2021/4/25 9:41 下午
 */
public interface LoadBalanceStrategy {


    /**
     * 选择服务器
     * @param nettyClientList
     * @return
     */
    NettyClient doSelect(List<NettyClient> nettyClientList);
}
