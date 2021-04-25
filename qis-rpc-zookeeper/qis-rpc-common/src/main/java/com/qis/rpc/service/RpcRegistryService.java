package com.qis.rpc.service;

import com.qis.rpc.listener.NodeChangeListener;

import java.util.List;

/**
 * @author qishuo
 * @date 2021/4/24 11:49 下午
 */
public interface RpcRegistryService {
    /**
     * 将服务注册到zookeeper中
     *
     * @param ip
     * @param port
     * @param service 方法的全路径
     */
    void registry(String ip, Integer port, String service);

    /**
     * 服务发现
     *
     * @param service wo
     * @return
     */
    List<String> discovery(String service);

    /**
     * 注册中心销毁
     */
    void destroy();

    /**
     * 添加监听者
     *
     * @param listener
     */
    void addListener(NodeChangeListener listener);
}
