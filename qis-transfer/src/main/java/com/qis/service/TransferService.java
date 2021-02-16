package com.qis.service;

/**
 * @author qishuo
 * @date 2021/2/14 2:22 下午
 */
public interface TransferService {
    /**
     * 转账
     *
     * @param fromCardNo
     * @param toCardNo
     * @param money
     * @throws Exception
     */
    void transfer(String fromCardNo, String toCardNo, int money) throws Exception;
}
