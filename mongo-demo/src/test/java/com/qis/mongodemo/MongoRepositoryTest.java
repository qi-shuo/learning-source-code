package com.qis.mongodemo;

import com.qis.mongodemo.entity.LagouResumeDatas;
import com.qis.mongodemo.repository.ResumeDatasRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author qishuo
 * @date 2021/8/1 3:59 下午
 */
@SpringBootTest
public class MongoRepositoryTest {
    @Resource
    private ResumeDatasRepository resumeDatasRepository;

    @Test
    void findAll() {
        List<LagouResumeDatas> all = resumeDatasRepository.findAll();
        System.out.println(all.size());
        System.out.println(all);
    }


    @Test
    void save() {
        List<LagouResumeDatas> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            LagouResumeDatas lagouResumeDatas = new LagouResumeDatas();
            lagouResumeDatas.setName("qis" + i);
            lagouResumeDatas.setSalary(String.valueOf(new Random().nextInt(100000)));
            list.add(lagouResumeDatas);
        }
        resumeDatasRepository.saveAll(list);
    }
}
