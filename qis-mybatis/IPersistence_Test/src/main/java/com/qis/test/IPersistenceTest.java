package com.qis.test;

import com.qis.dao.UserDao;
import com.qis.io.Resources;
import com.qis.pojo.User;
import com.qis.sqlSession.SqlSession;
import com.qis.sqlSession.SqlSessionFactory;
import com.qis.sqlSession.SqlSessionFactoryBuild;

import java.io.InputStream;

/**
 * 测试类
 *
 * @author qishuo
 * @date 2021/1/24 11:32 上午
 */
public class IPersistenceTest {
    public static void main(String[] args) throws Exception {
        //将配置文件加载成InputStream
        InputStream resourceAsSteam = Resources.getResourceAsSteam("SqlMapConfig.xml");
        //获取SqlSessionFactory
        SqlSessionFactoryBuild sqlSessionFactoryBuild = new SqlSessionFactoryBuild();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuild.build(resourceAsSteam);
        //获取SqlSession
        SqlSession sqlSession = sqlSessionFactory.createSqlSession();
        //查询所有
        UserDao mapper = sqlSession.getMapper(UserDao.class);
        // System.out.println(mapper.findAll());
        User userCondition = new User();
        userCondition.setId(2);
        userCondition.setUsername("tom");
        //查询单个
//        User user = mapper.findByCondition(userCondition);
//        System.out.println(user);
//        System.out.println("----------------------");

        User userParam = new User();
        userParam.setId(4);
        userParam.setUsername("testInsert");
        userParam.setPassword("123");
        userParam.setBirthday("12-12");

        //mapper.save(userParam);

        userParam.setUsername("testUpdate");

        //mapper.update(userParam);

        mapper.delete(userParam);
    }
}
