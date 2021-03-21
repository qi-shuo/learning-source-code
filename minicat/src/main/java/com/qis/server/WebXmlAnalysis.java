package com.qis.server;

import com.qis.server.servlet.impl.HttpServlet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对于web.xml的解析
 *
 * @author qishuo
 * @date 2021/3/21 4:14 下午
 */
public class WebXmlAnalysis {
    public final static Map<String, HttpServlet> servletMap = new HashMap<>();

    /**
     * 解析web.xml
     *
     * @param inputStream
     */
    public static Map<String, HttpServlet> webXmlAnalysis(InputStream inputStream, ClassLoader... classLoaders) {
        SAXReader saxReader = new SAXReader();
        Map<String, HttpServlet> servletMap = new HashMap<>();
        try {
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();

            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                // <servlet-name>qisServlet</servlet-name>
                Element servletNameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletNameElement.getStringValue();
                // <servlet-class>com.qis.server.servlet.QisServlet</servlet-class>
                Element servletClassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletClassElement.getStringValue();


                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /qisServlet
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                if (classLoaders != null && classLoaders.length > 0) {
                    servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass,true,classLoaders[0]).getDeclaredConstructor().newInstance());
                }else {
                    servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).getDeclaredConstructor().newInstance());
                }

            }


        } catch (DocumentException | IllegalAccessException | InstantiationException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return servletMap;
    }
}
