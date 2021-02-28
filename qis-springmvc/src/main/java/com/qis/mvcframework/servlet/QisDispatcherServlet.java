package com.qis.mvcframework.servlet;

import com.qis.mvcframework.annotation.*;
import com.qis.mvcframework.factory.ProxyFactory;
import com.qis.mvcframework.pojo.Handler;
import com.qis.mvcframework.utils.RequestThreadLocalUtil;
import com.qis.mvcframework.utils.ResponseThreadLocalUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * @author qishuo
 * @date 2021/2/20 11:19 下午
 */
public class QisDispatcherServlet extends HttpServlet {
    private String contextConfigLocation;
    private Properties properties;

    private final List<String> classNameList = new ArrayList<>();
    /**
     * BeanMap
     */
    private final Map<String, Object> beanMap = new HashMap<>();
    /**
     * 使用autowired注入时候使用
     */
    private final Map<Class<?>, List<Object>> autowiredMap = new HashMap<>();
    /**
     * 放入controller的bean
     */
    private final Map<String, Object> controllerBeanMap = new HashMap<>();
    /**
     * 代用验证的bean
     */
    private final Map<String, Object> securityBeanMap = new HashMap<>();
    /**
     * handler的映射
     */
    private final List<Handler> handlerMapping = new ArrayList<>();


    @Override
    public void init(ServletConfig config) {
        this.contextConfigLocation = config.getInitParameter("contextConfigLocation");

        try {
            //1:加载配置文件
            doLoadConfig();
            //2:扫描注解
            doScan();
            //3:初始化bean
            doInitBean();
            //4:维护对象的依赖关系
            doAutowired();
            //5:初始化handlerMapping
            initHandlerMapping();
            System.out.println("初始化 自定义 QisDispatcherServlet 完成,等待请求进来");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 初始化HandlerMapping
     */
    private void initHandlerMapping() {
        controllerBeanMap.forEach((beanName, bean) -> {
            String classRequestMappingValue = "";
            Class<?> aClass = bean.getClass();
            if (aClass.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);
                classRequestMappingValue = urlProcess(annotation.value());
            }

            for (Method method : aClass.getMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                String requestMappingValue = classRequestMappingValue + urlProcess(annotation.value());
                Handler handler = new Handler(beanMap.get(beanName), method, Pattern.compile(requestMappingValue));
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class<?> type = parameter.getType();
                    if (isHttpRequestServletOrHttpResponseServlet(type)) {
                        // 如果是request和response对象，那么参数名称写HttpServletRequest和HttpServletResponse
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), i);
                    } else {
                        handler.getParamIndexMapping().put(parameter.getName(), i);
                    }
                }
                handlerMapping.add(handler);
            }
        });
    }

    /**
     * 加工url信息,让其满足/xxx/xxx/xxx的格式,避免使用@RequestMapping 填写的映射格式不规范
     *
     * @param url
     * @return
     */
    private String urlProcess(String url) {
        if (url == null || url.isEmpty() || "/".equals(url)) {
            return "/";
        }
        String[] split = url.split("/");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if ("".equals(s.trim())) {
                continue;
            }
            sb.append("/").append(s);
        }

        return sb.toString();

    }

    private void doLoadConfig() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(this.contextConfigLocation);
        properties = new Properties();
        properties.load(resourceAsStream);
    }

    private void doScan() throws IOException {
        String scanPackage = properties.getProperty("scanPackage");
        //获取classpath的路径
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        //获取所有的className路径
        getClassName(scanPackagePath, scanPackage);

    }

    private void doInitBean() throws Exception {
        if (classNameList.isEmpty()) {
            return;
        }
        for (String className : classNameList) {
            Class<?> aClass = Class.forName(className);
            if (aClass.isAnnotationPresent(Service.class) || aClass.isAnnotationPresent(Controller.class) || aClass.isAnnotationPresent(Component.class)) {
                Object bean = aClass.getDeclaredConstructor().newInstance();
                String beanName = getBeanName(aClass);
                beanMap.put(beanName, bean);
                //如果是controller的bean方法到controllerMap中
                if (aClass.isAnnotationPresent(Controller.class)) {
                    controllerBeanMap.put(beanName, bean);
                }
                if (aClass.isAnnotationPresent(Security.class)) {
                    securityBeanMap.put(beanName, bean);
                }
                for (Method method : aClass.getMethods()) {
                    if (securityBeanMap.containsKey(beanName)) {
                        break;
                    }
                    if (method.isAnnotationPresent(Security.class)) {
                        //只要放进map中就可以直接结束循环了因为bean肯定要创建代理对象了
                        securityBeanMap.put(beanName, bean);
                        break;
                    }
                }
                Class<?>[] interfaces = aClass.getInterfaces();

                //如果有接口就保存接口的类型与bean
                for (Class<?> anInterface : interfaces) {
                    if (autowiredMap.containsKey(anInterface)) {
                        autowiredMap.get(anInterface).add(bean);
                    } else {
                        autowiredMap.put(anInterface, new ArrayList<>() {{
                            add(bean);
                        }});
                    }

                }
            }
        }
        //创建工厂
        if (!securityBeanMap.isEmpty()) {
            ProxyFactory proxyFactory = (ProxyFactory) beanMap.get("proxyFactory");
            securityBeanMap.forEach((beanName, bean) -> {
                Object proxyBean;
                if (bean.getClass().getInterfaces().length != 0) {
                    proxyBean = proxyFactory.getJdkProxy(bean);
                } else {
                    proxyBean = proxyFactory.getCglibProxy(bean);
                }
                beanMap.put(beanName, proxyBean);
//                if (controllerBeanMap.containsKey(beanName)) {
//                    controllerBeanMap.put(beanName, proxyBean);
//                }
            });
        }

    }

    /**
     * 获取bean的名称
     *
     * @param clazz
     * @return
     */
    private String getBeanName(Class<?> clazz) {
        List<Class<? extends Annotation>> beanAnnotationList = Arrays.asList(Service.class, Controller.class, Component.class);
        String value = null;
        for (Class<? extends Annotation> aClass : beanAnnotationList) {
            Annotation annotation = clazz.getAnnotation(aClass);
            if (annotation == null) {
                break;
            }
            if (annotation instanceof Service) {
                value = ((Service) annotation).value();
            }
            if (annotation instanceof Controller) {
                value = ((Controller) annotation).value();
            }
            if (annotation instanceof Component) {
                value = ((Component) annotation).value();
            }
        }
        if (value != null && !"".equals(value.trim())) {
            return value;
        }
        String simpleName = clazz.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    /**
     * 获取所有的className全路径
     *
     * @param classPath
     * @param scanPackage
     * @throws IOException
     */
    public void getClassName(String classPath, String scanPackage) throws IOException {

        String filePath = classPath + scanPackage.replace(".", "/");
        File parentFile = new File(filePath);
        File[] files = parentFile.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                getClassName(classPath, scanPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                classNameList.add(scanPackage + "." + file.getName().replaceAll(".class", ""));
            }
        }
    }

    /**
     * 实现依赖
     *
     * @throws IllegalAccessException
     */
    private void doAutowired() throws IllegalAccessException {
        if (beanMap.isEmpty()) {
            return;
        }
        for (Object bean : beanMap.values()) {
            //bean.getClass().getFields()只能获取public标注的字段
            Field[] fields = bean.getClass().getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {

                    Class<?> type = field.getType();
                    List<Object> objects = autowiredMap.get(type);
                    if (objects.size() > 1) {
                        throw new RuntimeException(type.toString() + "存在多个实现");
                    }
                    field.setAccessible(true);
                    field.set(bean, objects.get(0));
                }
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {


            RequestThreadLocalUtil.putHttpServletRequest(req);
            ResponseThreadLocalUtil.putHttpServletResponse(resp);
            //避免响应中文乱码
            resp.setHeader("Content-type", "text/html;charset=UTF-8");

            Handler handler = getHandler(req);
            if (Objects.isNull(handler)) {
                resp.getWriter().write("404 未找到请求");
                resp.setStatus(SC_NOT_FOUND);
                return;
            }
            Method method = handler.getMethod();
            Parameter[] parameters = method.getParameters();
            Object[] params = new Object[parameters.length];

            Map<String, String[]> parameterMap = req.getParameterMap();
            parameterMap.forEach((param, value) -> {
                if (!handler.getParamIndexMapping().containsKey(param)) {
                    return;
                }
                // name=1&name=2   name [1,2]

                String paramValue = Arrays.toString(value).replace("[", "").replace("]", "");

                // 方法形参确实有该参数，找到它的索引位置，对应的把参数值放入paraValues
                Integer index = handler.getParamIndexMapping().get(param);
                params[index] = paramValue;
            });

            Integer requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
            if (requestIndex != null && requestIndex > 0) {
                params[requestIndex] = req;
            }

            Integer responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());

            if (responseIndex != null && responseIndex > 0) {
                params[responseIndex] = resp;
            }


            //调用方法
            Object invoke = method.invoke(handler.getController(), params);

            if (invoke != null) {
                resp.getWriter().write(invoke.toString());
            }
            resp.setStatus(SC_OK);
        } catch (IllegalAccessException | InvocationTargetException e) {
            resp.getWriter().write("服务器内部错误");
            resp.setStatus(SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    /**
     * 判断是否是HttpRequestServlet或HttpResponseServlet
     *
     * @param clazz
     * @return
     */
    private boolean isHttpRequestServletOrHttpResponseServlet(Class<?> clazz) {
        return clazz == HttpServletRequest.class || clazz == HttpServletResponse.class;
    }

    /**
     * 获取handler
     *
     * @param req
     * @return
     */
    private Handler getHandler(HttpServletRequest req) {
        if (handlerMapping.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (matcher.matches()) {
                return handler;
            }

        }
        return null;
    }

}
