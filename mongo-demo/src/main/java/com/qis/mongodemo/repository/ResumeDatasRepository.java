package com.qis.mongodemo.repository;

import com.qis.mongodemo.entity.LagouResumeDatas;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author qishuo
 * @date 2021/8/1 3:58 下午
 */

public interface ResumeDatasRepository extends MongoRepository<LagouResumeDatas, String> {
}
