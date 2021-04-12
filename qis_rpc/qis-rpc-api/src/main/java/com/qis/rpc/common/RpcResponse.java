package com.qis.rpc.common;

import lombok.Data;

/**
 * @author qishuo1
 * @create 2021/4/12 17:41
 */
@Data
public class RpcResponse {

    /**
     * 响应ID
     */
    private String requestId;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 返回的结果
     */
    private Object result;

}
