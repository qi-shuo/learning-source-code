package com.qis.rpc.pojo;

import lombok.Data;

/**
 * @author qishuo
 * @date 2021/4/24 10:52 下午
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
