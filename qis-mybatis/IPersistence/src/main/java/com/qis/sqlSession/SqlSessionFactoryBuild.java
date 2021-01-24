package com.qis.sqlSession;

import com.qis.config.XMLConfigBuilder;
import com.qis.pojo.Configuration;
import com.qis.sqlSession.impl.DefaultSqlSessionFactory;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author qishuo
 * @date 2021/1/24 11:56 上午
 */
public class SqlSessionFactoryBuild {
    public SqlSessionFactory build(InputStream in) throws DocumentException, PropertyVetoException, IOException {
        //1:使用dom4j解析配置文件
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseConfig(in);


        //2:创建SqlSessionFactory对象 工厂类,生产SqlSession
        SqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return sqlSessionFactory;
    }
}
