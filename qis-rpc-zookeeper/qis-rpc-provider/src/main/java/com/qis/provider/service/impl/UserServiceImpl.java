package com.qis.provider.service.impl;

import com.qis.rpc.annotations.RpcService;
import com.qis.rpc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.qis.provider.config.NettyConfig.PORT;

/**
 * @author qishuo
 * @date 2021/4/24 10:24 下午
 */
@Service
@Slf4j
@RpcService
public class UserServiceImpl implements UserService {


    @Override
    public String sayHello(String word) {

        int millis = new Random().nextInt(1000);
        System.out.printf("请求的服务端,端口号为=[%s],内容=[%s],响应时间=[%s]%n", PORT, word, millis);

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return word + "响应:" + PORT + "响应:" + millis;
    }
}
