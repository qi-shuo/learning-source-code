package com.qis.mvcframework.pojo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author qishuo
 * @date 2021/2/28 9:07 下午
 */
public class Handler {
    /**
     * method.invoke(obj,)
     */
    private Object controller;

    private Method method;
    /**
     * spring中url是支持正则的
     */
    private Pattern pattern;
    /**
     * 参数顺序,是为了进行参数绑定，key是参数名，value代表是第几个参数 <name,2>
     */
    private Map<String, Integer> paramIndexMapping;


    public Handler(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        this.paramIndexMapping = new HashMap<>();
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }

}
