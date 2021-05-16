package com.qis.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.Filter;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author qishuo
 * @date 2021/5/15 8:16 下午
 */
@Activate(group = {CommonConstants.CONSUMER,CommonConstants.PROVIDER})
public class TPMonitorFilter implements Filter {
    /**
     * key:方法名,value统计统计请求事件,key时间戳单位秒,value是对应时间戳的响应时间
     */
    static Map<String, ConcurrentHashMap<Long, List<Long>>> cacheMap = new ConcurrentHashMap<>();

    static {
        //创建一个定时的任务线程池
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        //进行打印监控情况,并且清理超过1分钟的数据
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            //当前时间
            long currTime = getTime();
            cacheMap.forEach((methodName, requestTimeMap) -> {
                List<Long> merge = new ArrayList<>();
                //打印监控情况,并清理超过1分钟的数据
                List<Long> clearKey = new ArrayList<>();
                requestTimeMap.forEach((time, requestTimeList) -> {
                    if (currTime - time > 60) {
                        clearKey.add(currTime);
                    } else {
                        merge.addAll(requestTimeList);
                    }
                });
                //批量清理
                clearKey.forEach(requestTimeMap::remove);
                //进行排序
                List<Long> sortList = merge.stream().sorted(Comparator.comparingLong(a -> a)).collect(Collectors.toList());
                if (merge.size() == 0) {
                    System.out.println(methodName + " 没有请求......");
                    System.out.println();
                    return;
                }
                System.out.printf(methodName + " :TP90:%s,TP99:%s", sortList.get((int)(0.9 * sortList.size())), sortList.get((int)(0.99 % sortList.size())));
                System.out.println();
            });
        }, 0, 5, TimeUnit.SECONDS);
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();
        if (!cacheMap.containsKey(methodName)) {
            //使用并发的map
            cacheMap.put(methodName, new ConcurrentHashMap<>());
        }
        long startTime = System.currentTimeMillis();
        try {
            return invoker.invoke(invocation);
        } finally {
            long endTime = System.currentTimeMillis();
            //请求时间
            long requestTime = endTime - startTime;
            Map<Long, List<Long>> longListMap = cacheMap.get(methodName);

            long time = getTime();
            List<Long> timeList = longListMap.getOrDefault(time, new ArrayList<>());
            timeList.add(requestTime);
            longListMap.put(time, timeList);
        }

    }

    /**
     * 获取秒级别的时间戳
     *
     * @return
     */
    private static long getTime() {
        return System.currentTimeMillis() / 1000;
    }


}
