package com.qis.provider;

import com.qis.rpc.annotations.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务提供者启动类
 *
 * @author qishuo
 * @date 2021/4/24 10:18 下午
 */
@SpringBootApplication
@ComponentScan(value = "com.qis")
public class ProviderBootstrap implements ApplicationContextAware {
    public static ApplicationContext application_context;
    public static final Map<String, Object> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(ProviderBootstrap.class, args);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        application_context = applicationContext;

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!CollectionUtils.isEmpty(serviceBeanMap)) {
            Set<Map.Entry<String, Object>> entries = serviceBeanMap.entrySet();
            for (Map.Entry<String, Object> item : entries) {
                Object serviceBean = item.getValue();
                if (serviceBean.getClass().getInterfaces().length == 0) {
                    throw new RuntimeException("service must implements interface.");
                }
                String interfaceName = serviceBean.getClass().getInterfaces()[0].getName();
                SERVICE_INSTANCE_MAP.put(interfaceName, serviceBean);
            }
        }
    }
}
