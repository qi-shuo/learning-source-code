package com.qis.test;

import com.qis.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author qishuo
 * @date 2021/1/24 10:36 下午
 */
public class MybatisTest {
    @Test
    public void testMybatis() throws IOException {
        InputStream inputStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        //默认开启一个事务,但是该事务不会自动提交,所以在增删改是要手动提交
        SqlSession sqlSession = new SqlSessionFactoryBuilder().build(inputStream).openSession();
        List<User> userList = sqlSession.selectList("com.qis.dao.UserMapper.findAll");
        System.out.println(userList);
        sqlSession.close();

    }
}
