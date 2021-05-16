package com.qis.service.impl.impl;

import com.qis.service.impl.DemoService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Random;

/**
 * @author qishuo
 * @date 2021/5/16 1:49 下午
 */
@Service
public class DemoServiceImpl implements DemoService {

    private static Random random = new Random();

    @Override
    public String methodA() {
        int i = random.nextInt(100);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "methodA 休眠" + i;
    }

    @Override
    public String methodB() {
        int i = random.nextInt(100);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "methodB 休眠" + i;
    }

    @Override
    public String methodC() {
        int i = random.nextInt(100);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "methodC 休眠" + i;
    }
}
