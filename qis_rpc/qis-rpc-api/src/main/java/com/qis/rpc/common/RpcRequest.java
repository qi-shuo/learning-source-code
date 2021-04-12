package com.qis.rpc.common;

import lombok.Data;

/**
 * @author qishuo1
 * @create 2021/4/12 17:41
 */
@Data
public class RpcRequest {

    /**
     * 请求对象的ID
     */
    private String requestId;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 入参
     */
    private Object[] parameters;

}