package com.qis.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author qishuo
 * @date 2021/7/11 4:59 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "c_order")
@Entity
@ToString
public class COrder {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_del")
    private Integer isDel;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "company_id")
    private Integer companyId;
    @Column(name = "publish_user_id")
    private Integer publishUserId;
    @Column(name = "position_id")
    private Integer positionId;
    @Column(name = "resume_type")
    private Integer resumeType;
    @Column(name = "status")
    private String status;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
