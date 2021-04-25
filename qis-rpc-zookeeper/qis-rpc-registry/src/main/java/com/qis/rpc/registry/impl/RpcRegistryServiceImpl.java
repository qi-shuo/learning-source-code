package com.qis.rpc.registry.impl;

import com.qis.rpc.listener.NodeChangeListener;
import com.qis.rpc.service.RpcRegistryService;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author qishuo
 * @date 2021/4/24 9:10 下午
 */
public class RpcRegistryServiceImpl implements RpcRegistryService {

    //    private static final String ZK_IP = "127.0.0.1";
//    private static final Integer ZK_PORT = 2181;
    private static final String ROOT_PATH = "/qis_rpc";
    private static final String ZK_SPLIT = "/";
    private final ZkClient ZK_CLIENT;

    private List<NodeChangeListener> listenerList = new ArrayList<>();
    /**
     * 服务缓存使用CopyOnWriteArrayList,避免并发问题
     */
    private List<String> serviceList = new CopyOnWriteArrayList<>();

    public RpcRegistryServiceImpl(String ip, Integer port) {
        ZK_CLIENT = new ZkClient(ip, port);
    }

    @Override
    public void registry(String ip, Integer port, String service) {

        if (!ZK_CLIENT.exists(ROOT_PATH)) {
            create(ROOT_PATH, true);
        }

        String servicePath = ROOT_PATH + ZK_SPLIT + service;
        if (!ZK_CLIENT.exists(servicePath)) {
            create(servicePath, true);
        }
        //创建临时节点
        create(servicePath + ZK_SPLIT + ip + "_" + port, false);

    }

    @Override
    public List<String> discovery(String service) {
        //如果根节点不存在直接返回肯定是null
        if (!ZK_CLIENT.exists(ROOT_PATH)) {
            return new ArrayList<>();
        }
        serviceList = ZK_CLIENT.getChildren(ROOT_PATH + ZK_SPLIT + service);

        registryWatcher(service);
        return serviceList;
    }

    @Override
    public void destroy() {
        ZK_CLIENT.close();

    }

    @Override
    public void addListener(NodeChangeListener listener) {
        listenerList.add(listener);
    }

    private void create(String path, boolean isPersistent) {
        if (isPersistent) {
            ZK_CLIENT.createPersistent(path);
        } else {
            ZK_CLIENT.createEphemeral(path);

        }
    }

    /**
     * 注册监听
     *
     * @param service
     */
    private void registryWatcher(String service) {
        //更新缓存
        ZK_CLIENT.subscribeChildChanges(ROOT_PATH + ZK_SPLIT + service, (parentPath, currentChilds) -> {
            serviceList = currentChilds;
            listenerList.forEach(listener -> listener.notifyAll(service, serviceList));
        });
    }
}
