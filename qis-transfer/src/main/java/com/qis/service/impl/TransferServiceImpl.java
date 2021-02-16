package com.qis.service.impl;

import com.qis.annotation.Autowired;
import com.qis.annotation.Service;
import com.qis.annotation.Transactional;
import com.qis.dao.AccountDao;
import com.qis.service.TransferService;
import com.qis.vo.Account;

/**
 * @author qishuo
 * @date 2021/2/14 2:22 下午
 */
@Service("transferService")
public class TransferServiceImpl implements TransferService {
    /**
     * AccountDao
     */
    @Autowired
    private AccountDao accountDao;


    @Transactional
    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(to);
        //int c = 1 / 0;
        accountDao.updateAccountByCardNo(from);
    }
}
