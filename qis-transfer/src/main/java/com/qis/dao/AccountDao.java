package com.qis.dao;

import com.qis.utils.ConnectionUtils;
import com.qis.vo.Account;

/**
 * @author qishuo
 * @date 2021/2/14 2:14 下午
 */
public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;

    ConnectionUtils getConnectionUtils();
}

