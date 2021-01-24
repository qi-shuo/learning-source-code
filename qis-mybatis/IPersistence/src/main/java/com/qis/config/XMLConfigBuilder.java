package com.qis.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.qis.io.Resources;
import com.qis.pojo.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 解析xml
 *
 * @author qishuo
 * @date 2021/1/24 12:03 下午
 */
public class XMLConfigBuilder {
    private final Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    /**
     * 使用dom4j对配置文件解析
     * 该方法解析xml,封装Configuration
     *
     * @param inputStream
     * @return
     */
    public Configuration parseConfig(InputStream inputStream) throws DocumentException, PropertyVetoException, IOException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.selectNodes("//property");
        Properties properties = new Properties();
        elementList.forEach(element -> {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.setProperty(name, value);
        });
        ComboPooledDataSource dataSources = new ComboPooledDataSource();
        dataSources.setJdbcUrl(properties.getProperty("jdbcUrl"));
        dataSources.setDriverClass(properties.getProperty("driverClass"));
        dataSources.setUser(properties.getProperty("username"));
        dataSources.setPassword(properties.getProperty("password"));
        //赋值dataSources
        configuration.setDataSource(dataSources);
        //获取mapper映射的文件地址
        List<Element> mapperList = rootElement.selectNodes("//mapper");
        for (Element element : mapperList) {
            String mapperPath = element.attributeValue("resource");

            //解析mapper
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
            xmlMapperBuilder.parseMapper(mapperPath);

        }


        return configuration;
    }
}
