package com.qis.server;

import com.qis.server.servlet.impl.HttpServlet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qishuo
 * @date 2021/3/21 3:37 下午
 */
public class Mapper {
    private String port;
    private Map<String, Map<String, HttpServlet>> servletMap = new HashMap<>();

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Map<String, Map<String, HttpServlet>> getServletMap() {
        return servletMap;
    }

    public void setServletMap(Map<String, Map<String, HttpServlet>> servletMap) {
        this.servletMap = servletMap;
    }

    /**
     * 创建映射
     */
    public void createMapper() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            Element serverElement = (Element) rootElement.selectSingleNode("//Server");
            List<Element> serviceList = serverElement.selectNodes("//Service");
            for (Element serviceElement : serviceList) {
                Element connectorNode = (Element) serviceElement.selectSingleNode("//Connector");
                this.port = connectorNode.attributeValue("port");
                List<Element> engineList = serviceElement.selectNodes("//Engine");
                for (Element engineElement : engineList) {
                    List<Element> hostElementList = engineElement.selectNodes("//Host");
                    for (Element hostElement : hostElementList) {
                        String appBase = hostElement.attributeValue("appBase");
                        openAppBase(appBase);

                    }
                }
            }


        } catch (DocumentException | FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void openAppBase(String appBase) throws FileNotFoundException, MalformedURLException {
        File file = new File(appBase);
        if (!file.exists()) {
            return;
        }
        File[] childrenList = file.listFiles();
        if (childrenList == null) {
            return;
        }
        for (File childrenFile : childrenList) {
            InputStream inputStream = new FileInputStream(childrenFile.getAbsolutePath() + "/META-INF/web.xml");
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{childrenFile.toURI().toURL()});
            Map<String, HttpServlet> stringHttpServletMap = WebXmlAnalysis.webXmlAnalysis(inputStream, classLoader);
            servletMap.put(childrenFile.getName(), stringHttpServletMap);
        }
    }
}
