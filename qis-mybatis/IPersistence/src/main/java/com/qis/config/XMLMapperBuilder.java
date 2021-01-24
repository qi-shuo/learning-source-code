package com.qis.config;

import com.qis.io.Resources;
import com.qis.pojo.Configuration;
import com.qis.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析mapper的xml
 *
 * @author qishuo
 * @date 2021/1/24 4:50 下午
 */
public class XMLMapperBuilder {
    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parseMapper(String mapperPath) throws DocumentException {
        InputStream mapperStream = Resources.getResourceAsSteam(mapperPath);

        Document document = new SAXReader().read(mapperStream);
        Element rootElement = document.getRootElement();
        List<Element> elementList = rootElement.selectNodes("//select");

        elementList.forEach(element -> {
            String id = element.attributeValue("id");
            String paramType = element.attributeValue("paramType");
            String resultType = element.attributeValue("resultType");
            String sql = element.getTextTrim();
            //创建MappedStatement 并存放到map中
            MappedStatement mappedStatement = MappedStatement
                    .builder()
                    .id(id)
                    .paramType(paramType)
                    .resultType(resultType)
                    .sql(sql)
                    .build();
            String mappedStatementId = rootElement.attributeValue("namespance") + "." + id;
            configuration.getMappedStatementMap().put(mappedStatementId, mappedStatement);
        });


    }


}
