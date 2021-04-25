package com.qis.rpc.listener;


import java.util.List;

/**
 * @author qishuo
 * @date 2021/4/24 11:45 下午
 */
public interface NodeChangeListener {

    /**
     * 通知全部
     *
     * @param serviceName 服务名称,也就是方法的全路径
     * @param serviceList 存活的service的列表ip_port
     */
    void notifyAll(String serviceName, List<String> serviceList);
}
