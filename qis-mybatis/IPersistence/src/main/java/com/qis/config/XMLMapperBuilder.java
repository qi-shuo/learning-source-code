package com.qis.config;

import com.qis.enums.SqlCommandTypeEnum;
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
    private Element rootElement;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parseMapper(String mapperPath) throws DocumentException {
        InputStream mapperStream = Resources.getResourceAsSteam(mapperPath);

        Document document = new SAXReader().read(mapperStream);
        this.rootElement = document.getRootElement();
        handlerSelect();
        handlerUpdate();
        handlerInsert();
        handlerDelete();

    }

    /**
     * 处理select标签
     */
    private void handlerSelect() {
        List<Element> elementList = rootElement.selectNodes("//select");

        handleElementList(elementList, SqlCommandTypeEnum.SELECT.getType());
    }

    /**
     * 处理insert标签
     */
    private void handlerInsert() {
        List<Element> elementList = rootElement.selectNodes("//update");
        handleElementList(elementList, SqlCommandTypeEnum.UPDATE.getType());
    }

    /**
     * 处理update标签
     */
    private void handlerUpdate() {
        List<Element> elementList = rootElement.selectNodes("//insert");
        handleElementList(elementList, SqlCommandTypeEnum.INSERT.getType());
    }

    /**
     * 处理delete标签
     */
    private void handlerDelete() {
        List<Element> elementList = rootElement.selectNodes("//delete");
        handleElementList(elementList, SqlCommandTypeEnum.DELETE.getType());
    }

    /**
     * 获取标签的内容
     *
     * @param elementList
     */
    private void handleElementList(List<Element> elementList, String sqlCommendType) {
        elementList.forEach(element -> {
            String id = element.attributeValue("id");
            String paramType = element.attributeValue("paramType");
            String resultType = element.attributeValue("resultType", "");
            String sql = element.getTextTrim();
            //创建MappedStatement 并存放到map中
            MappedStatement mappedStatement = MappedStatement
                    .builder()
                    .id(id)
                    .paramType(paramType)
                    .resultType(resultType)
                    .sql(sql)
                    .sqlCommandType(sqlCommendType)
                    .build();
            String mappedStatementId = rootElement.attributeValue("namespance") + "." + id;
            configuration.getMappedStatementMap().put(mappedStatementId, mappedStatement);
        });
    }
}
