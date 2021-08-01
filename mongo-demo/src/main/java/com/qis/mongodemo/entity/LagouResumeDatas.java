package com.qis.mongodemo.entity;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author qishuo
 * @date 2021/8/1 3:55 下午
 */
@Document("lagou_resume_datas")
public class LagouResumeDatas {

    private String id;
    private String name;
    private String salary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "LagouResumeDatas{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }
}
