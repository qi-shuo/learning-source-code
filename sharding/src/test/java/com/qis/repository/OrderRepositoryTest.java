package com.qis.repository;

import com.qis.entity.COrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author qishuo
 * @date 2021/7/11 8:27 下午
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderRepositoryTest {
    @Resource
    private OrderRepository orderRepository;

    @Test
    public void save() {
        Date date = new Date();
        for (int i = 1; i <= 20; i++) {
            COrder order = COrder
                    .builder()
                    .isDel(0)
                    .userId(i)
                    .companyId(i)
                    .publishUserId(i)
                    .positionId(i)
                    .resumeType(0)
                    .status("ARRANGE_INTERVIEW-通知⾯试")
                    .createTime(date)
                    .updateTime(date)
                    .build();
            orderRepository.save(order);

        }
    }

    @Test
    public void findByUserId() {
        COrder cOrder1 = orderRepository.findByUserId(1);
        System.out.println("打印结果: " + cOrder1);

        COrder cOrder2 = orderRepository.findByUserId(2);
        System.out.println("打印结果: " + cOrder2);

    }

}
