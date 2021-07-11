package com.qis.repository;

import com.qis.entity.COrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author qishuo
 * @date 2021/7/11 5:08 下午
 */
public interface OrderRepository extends JpaRepository<COrder, Long> {
    @Query("from COrder c where c.userId=:userId")
    COrder findByUserId(@Param("userId") Integer userId);
}
