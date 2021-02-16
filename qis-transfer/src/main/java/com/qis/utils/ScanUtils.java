package com.qis.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 扫描指定位置的下面所有的.java文件
 *
 * @author qishuo
 * @date 2021/2/14 5:08 下午
 */
@Slf4j
public class ScanUtils {
    public static void main(String[] args) throws IOException {
        System.out.println(getFullyQualifiedClassNameList("com.qis"));
    }
    /**
     * 获取指定包下的所有字节码文件的全类名
     */
    public static List<String> getFullyQualifiedClassNameList(String basePath) throws IOException {
        log.info("开始扫描包{}下的所有类", basePath);
        return doScan(basePath, new ArrayList<>());
    }

    private static List<String> doScan(String basePath, List<String> result) throws IOException {

        String splashPath = StringUtils.dotToSplash(basePath);
        URL url = Thread.currentThread().getContextClassLoader().getResource(splashPath);
        assert url != null;
        String filePath = StringUtils.getRootPath(url);
        List<String> names;
        if (isJarFile(filePath)) {
            names = readFromJarFile(filePath, splashPath);
        } else {
            names = readFromDirectory(filePath);
        }
        assert names != null;
        for (String name : names) {
            if (isClassFile(name)) {
                result.add(toFullyQualifiedName(name, basePath));
            } else {
                doScan(basePath + "." + name, result);
            }
        }

        return result;
    }

    private static String toFullyQualifiedName(String shortName, String basePackage) {

        return basePackage + '.' +
                StringUtils.trimExtension(shortName);
    }

    private static boolean isJarFile(String name) {
        return name.endsWith(".jar");
    }

    private static List<String> readFromDirectory(String path) {
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    private static List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {

        JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
        JarEntry entry = jarIn.getNextJarEntry();
        List<String> nameList = new ArrayList<>();
        while (null != entry) {
            String name = entry.getName();
            if (name.startsWith(splashedPackageName) && isClassFile(name)) {
                nameList.add(name);
            }

            entry = jarIn.getNextJarEntry();
        }

        return nameList;
    }

    private static boolean isClassFile(String name) {
        return name.endsWith(".class");
    }
}
