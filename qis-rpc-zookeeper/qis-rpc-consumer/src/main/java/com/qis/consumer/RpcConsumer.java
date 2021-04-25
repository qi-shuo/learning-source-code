package com.qis.consumer;

import com.qis.consumer.client.NettyClient;
import com.qis.consumer.loadbalance.impl.MinRequestTimeBalance;
import com.qis.rpc.listener.NodeChangeListener;
import com.qis.rpc.pojo.RpcRequest;
import com.qis.rpc.pojo.RpcResponse;
import com.qis.rpc.service.RpcRegistryService;
import com.qis.rpc.statistics.StatisticsRequest;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;

import static com.qis.rpc.statistics.StatisticsRequest.statistics_map;

/**
 * @author qishuo
 * @date 2021/4/25 12:02 上午
 */
@Slf4j
public class RpcConsumer implements NodeChangeListener {

    MinRequestTimeBalance minRequestTimeBalance = new MinRequestTimeBalance();

    ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private ZkClient zkClient;

    //public static AtomicInteger count = new AtomicInteger(-1);
    /**
     * key是服务名称也就是接口的全限定名
     * value:对应的NettyClient服务
     */
    private Map<String, List<NettyClient>> nettyClientMap = new ConcurrentHashMap<>();

    public RpcConsumer(List<Class<?>> rpcServiceList, RpcRegistryService rpcRegistryService) {

        rpcServiceList.forEach(rpcService -> {
            String name = rpcService.getName();
            List<String> discoveryList = rpcRegistryService.discovery(name);

            changeNettyClientList(name, discoveryList);

        });
        rpcRegistryService.addListener(this);
        this.zkClient = new ZkClient("127.0.0.1", 2181);
        monitor();
    }


    public Object createProxy(Class<?> serviceClass) {

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass}, (proxy, method, args) -> {
            // 封装request对象
            RpcRequest request = new RpcRequest();
            String requestId = UUID.randomUUID().toString();

            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();

            Class<?>[] parameterTypes = method.getParameterTypes();

            request.setRequestId(requestId);
            request.setClassName(className);
            request.setMethodName(methodName);
            request.setParameterTypes(parameterTypes);
            request.setParameters(args);
            //System.out.printf("请求内容=[%s]", JSONObject.toJSONString(request));
            //System.out.println();
            List<NettyClient> nettyClientList = nettyClientMap.get(serviceClass.getName());
            if (CollectionUtils.isEmpty(nettyClientList)) {
                return null;
            }
            NettyClient nettyClient = minRequestTimeBalance.doSelect(nettyClientList);
            long startTime = System.currentTimeMillis();
            RpcResponse response = (RpcResponse) nettyClient.send(request);
            long endTime = System.currentTimeMillis();
            report(nettyClient.getIpAndPort(), endTime);
            StatisticsRequest statisticsRequest = StatisticsRequest
                    .builder()
                    .ipAndPort(nettyClient.getIpAndPort())
                    .lastTime(endTime - startTime)
                    .build();
            statistics_map.put(nettyClient.getIpAndPort(), statisticsRequest);
            return response.getResult();
        });
    }


    /**
     * 上报上一次请求的时间
     *
     * @param ipAndPort
     * @param endTime
     */
    public void report(String ipAndPort, long endTime) {

        if (!zkClient.exists("/statistics")) {
            zkClient.createPersistent("/statistics");
        }
        String path = "/statistics" + "/" + ipAndPort;
        if (zkClient.exists(path)) {
            zkClient.writeData(path, endTime);

        } else {
            zkClient.createEphemeral(path, endTime);
        }


    }

    public void monitor() {
        scheduled.scheduleWithFixedDelay(() -> {
            //说明所有节点还没有轮询完毕不检查
            if (CollectionUtils.isEmpty(statistics_map)) {
                return;
            }
            //记录超过5秒的服务
            List<String> list = new ArrayList<>();
            statistics_map.forEach((ipAndPort, value) -> {

                if (!zkClient.exists("/statistics")) {
                    zkClient.createPersistent("/statistics");
                }
                try {
                    long preTime = zkClient.readData("/statistics" + "/" + ipAndPort);
                    long currTime = System.currentTimeMillis();
                    //大于5秒
                    if (currTime - preTime >= 5000) {
                        list.add(ipAndPort);
                    }
                } catch (Exception e) {
                }

            });
            list.forEach(key -> statistics_map.remove(key));
        }, 0, 5, TimeUnit.SECONDS);
    }


    @Override
    public void notifyAll(String serviceName, List<String> serviceList) {
        changeNettyClientList(serviceName, serviceList);
    }

    private void changeNettyClientList(String name, List<String> discoveryList) {
        if (!CollectionUtils.isEmpty(discoveryList)) {
            List<NettyClient> nettyClientList = new ArrayList<>();
            discoveryList.forEach(serverInfo -> {
                String[] split = serverInfo.split("_");
                String ip = split[0];
                String port = split[1];
                NettyClient nettyClient = new NettyClient(ip, Integer.parseInt(port));
                nettyClient.initClient();
                nettyClientList.add(nettyClient);
            });
            nettyClientMap.put(name, nettyClientList);
        }
    }
}
