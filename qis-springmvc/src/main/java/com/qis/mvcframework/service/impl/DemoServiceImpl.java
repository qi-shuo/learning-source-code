package com.qis.mvcframework.service.impl;

import com.qis.mvcframework.annotation.Service;
import com.qis.mvcframework.service.DemoService;

/**
 * @author qishuo
 * @date 2021/2/28 9:48 下午
 */
@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String demoService(String name) {
        return name;
    }
}
