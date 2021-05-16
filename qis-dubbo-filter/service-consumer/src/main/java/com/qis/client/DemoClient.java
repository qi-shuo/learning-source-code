package com.qis.client;

import com.qis.service.impl.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qishuo
 * @date 2021/5/16 2:26 下午
 */
@Component
public class DemoClient {
    @Reference
    private DemoService demoService;
    ExecutorService executorService = Executors.newFixedThreadPool(3);


    public void demo() {
        for (int i = 0; true; i++) {
            executorService.execute(() -> {
                demoService.methodA();
            });
            executorService.execute(() -> {
                demoService.methodB();
            });
            executorService.execute(() -> {
                demoService.methodC();
            });
            if (i % 2000 == 0) {
                try {
                    Thread.sleep(3000);
                    System.out.println("调用2000次,休息3s");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
