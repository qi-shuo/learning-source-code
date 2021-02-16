package com.qis.factory;

import com.qis.annotation.*;
import com.qis.config.ScanConfig;
import com.qis.service.TransferService;
import com.qis.service.impl.TransferServiceImpl;
import com.qis.utils.ScanUtils;
import com.qis.utils.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author qishuo
 * @date 2021/2/14 2:23 下午
 */
public class BeanFactory {

    private static ProxyFactory proxyFactory;
    /**
     * 存放最终的bean
     */
    private final static Map<String, Object> BEAN_MAP = new HashMap<>();
    /**
     * 存放未设置属性的bean
     */
    private final static Map<String, Object> SECOND_MAP = new HashMap<>();


    /**
     * 解决依赖注入的map
     */
    private final static Map<Class<?>, List<Object>> AUTOWIRED_MAP = new HashMap<>();
    /**
     * 代理对象的map
     */
    private final static Map<String, Object> PROXY_MAP = new HashMap<>();

    /**
     * class与beanName的缓存
     */
    private final static Map<Class<?>, String> CLAZZ_TO_BEAN_NAME_MAP = new HashMap<>();
    /**
     * 标记为bean的注解
     */
    private final static List<Class<? extends Annotation>> COMPONENT_LIST = Arrays.asList(Component.class, Service.class, Repository.class);

    /**
     * 读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     */
    static {
        try {
            registerBean();
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取bean
     *
     * @param id
     * @return
     */
    public static Object getBean(String id) {
        return BEAN_MAP.get(id);
    }

    /**
     * 获取使用@Component,@Service,@Repository 标注的类相当于bean
     *
     * @return
     */
    private static void registerBean() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        String value = ScanConfig.class.getAnnotation(Scan.class).value();
        List<String> classNameList = ScanUtils.getFullyQualifiedClassNameList(value);

        for (String clazzName : classNameList) {
            Class<?> aClass = Class.forName(clazzName);

            String beanName = getBeanName(aClass);

            if (StringUtils.isEmpty(beanName)) {
                continue;
            }
            createBean(aClass, beanName);


        }
        proxyFactory = getProxyFactory();
        SECOND_MAP.forEach((beanName, bean) -> {
            try {
                initAttribute(bean, bean.getClass());
                if (PROXY_MAP.containsKey(beanName)) {
                    BEAN_MAP.put(beanName, PROXY_MAP.get(beanName));
                } else {
                    BEAN_MAP.put(beanName, createProxy(bean));
                }

            } catch (IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        //初始化代理工厂的属性,当执行代理方法时候才会用到对应属性所以放到最后进行赋值
        initAttribute(proxyFactory, proxyFactory.getClass());
        SECOND_MAP.clear();

    }

    /**
     * 获取代理工厂
     *
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    private static ProxyFactory getProxyFactory() throws ClassNotFoundException, IllegalAccessException {
        //先将创建代理的工厂初始化属性
        Object proxyFactoryObj = SECOND_MAP.get("proxyFactory");
        ProxyFactory proxyFactory = (ProxyFactory) proxyFactoryObj;
        SECOND_MAP.remove("proxyFactory");
        BEAN_MAP.put("proxyFactory", proxyFactory);

        return proxyFactory;
    }

    /**
     * 创建bean
     *
     * @param clazz
     * @param beanName
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static void createBean(Class<?> clazz, String beanName) throws IllegalAccessException, InstantiationException {
        if (clazz.isAnnotation()) {
            return;
        }
        if (BEAN_MAP.containsKey(beanName)) {
            return;
        }
        if (SECOND_MAP.containsKey(beanName)) {
            return;
        }

        Object obj = clazz.newInstance();
        SECOND_MAP.put(beanName, obj);
        Class<?>[] interfaces = clazz.getInterfaces();

        //将创建好的bean根据类型放到autowiredMap中
        if (interfaces.length == 0) {
            if (!AUTOWIRED_MAP.containsKey(clazz)) {
                AUTOWIRED_MAP.put(clazz, Collections.singletonList(obj));
            }
        } else {
            for (Class<?> anInterface : interfaces) {
                if (AUTOWIRED_MAP.containsKey(anInterface)) {
                    AUTOWIRED_MAP.get(anInterface).add(obj);
                } else {
                    AUTOWIRED_MAP.put(anInterface, Collections.singletonList(obj));
                }
            }
        }


    }

    /**
     * 初始化属性
     *
     * @param bean
     * @param clazz
     */
    private static void initAttribute(Object bean, Class<?> clazz) throws IllegalAccessException, ClassNotFoundException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation == null) {
                continue;
            }
            field.setAccessible(true);

            Class<?> typeClazz = field.getType();

            List<Object> objects = AUTOWIRED_MAP.get(typeClazz);
            if (objects == null || objects.size() != 1) {
                throw new RuntimeException("创建bean失败");

            } else {
                Object value = objects.get(0);
                value = createProxy(value);
                field.set(bean, value);
            }
        }

    }

    /**
     * 创建代理类
     *
     * @param value
     * @return
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private static Object createProxy(Object value) throws IllegalAccessException, ClassNotFoundException {
        if (PROXY_MAP.containsKey(getBeanName(value.getClass()))) {
            return PROXY_MAP.get(getBeanName(value.getClass()));
        }
        Class<?> clazz = value.getClass();
        if (value.getClass().getInterfaces().length == 0) {
            value = proxyFactory.getCglibProxy(value);
        } else {
            value = proxyFactory.getJdkProxy(value);
        }
        PROXY_MAP.put(getBeanName(clazz), value);
        return value;
    }

    /**
     * 获取声明成bean的注解
     *
     * @param annotations
     * @return
     */
    private static Annotation getComponentAnnotation(Annotation[] annotations) {
        if (annotations.length == 0) {
            return null;
        }
        //判断扫描的所有对象是否有声明成bean的注解
        for (Annotation annotation : annotations) {

            if (COMPONENT_LIST.contains(annotation.annotationType())) {
                return annotation;
            }

        }
        return null;

    }

    /**
     * 获取bean的名称
     *
     * @param clazz
     * @return
     */
    private static String getBeanName(Class<?> clazz) {
        if (CLAZZ_TO_BEAN_NAME_MAP.containsKey(clazz)) {
            return CLAZZ_TO_BEAN_NAME_MAP.get(clazz);
        }
        Annotation[] annotations = clazz.getAnnotations();
        Annotation componentAnnotation = getComponentAnnotation(annotations);
        if (componentAnnotation == null) {
            //不是bean返回空字符串
            return "";
        }
        String value;
        if (componentAnnotation instanceof Service) {
            Service service = (Service) componentAnnotation;
            value = service.value();
        } else if (componentAnnotation instanceof Repository) {
            Repository repository = (Repository) componentAnnotation;
            value = repository.value();

        } else {
            Component component = (Component) componentAnnotation;
            value = component.value();
        }
        if (StringUtils.isEmpty(value)) {
            value = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1);

        }
        CLAZZ_TO_BEAN_NAME_MAP.put(clazz, value);
        return value;

    }

    /**
     * 使用main方法验证生成的代理bean是否单例,只有proxyFactory不是有代理类创建其他的bean都是代理对象
     *
     * @param args
     */
    public static void main(String[] args) {
        new BeanFactory();
        BEAN_MAP.forEach((beanName, bean) -> {
            if (PROXY_MAP.containsKey(beanName)) {
                System.out.println(beanName + ": " + PROXY_MAP.get(beanName).equals(bean));
            } else {
                System.out.println("不包含: " + beanName);
            }
        });
    }
}
