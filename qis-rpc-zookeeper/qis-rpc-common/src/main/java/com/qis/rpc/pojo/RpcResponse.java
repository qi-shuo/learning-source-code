package com.qis.rpc.pojo;

import lombok.Data;

/**
 * @author qishuo
 * @date 2021/4/24 10:54 下午
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
