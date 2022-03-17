package org.csu.mypetstore.service;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.persistence.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;

    public Account getAccount(String username) {
        return accountMapper.getAccountByUsername(username);
    }

    public Account getAccount(String username, String password) {
        return accountMapper.getAccountByUsernameAndPassword(username, password);
    }

    //添加事务，出错回滚
    @Transactional
    public void insertAccount(Account account) {
        accountMapper.insertAccount(account);
        accountMapper.insertProfile(account);
        accountMapper.insertSignon(account);
    }

    @Transactional
    public void updateAccount(Account account) {
        accountMapper.updateAccount(account);
        accountMapper.updateProfile(account);

        //如果值password存在且长度大于0，更新signon中的值，完成修改密码
        Optional.ofNullable(account.getPassword()).filter(password -> password.length() > 0)
                .ifPresent(password -> accountMapper.updateSignon(account));
    }


    public List<Account> getAllAccount() {
        return accountMapper.getAllAccount();
    }

    public List<Account> searchAccount(String keyword) {
        return accountMapper.searchAccount("%" + keyword + "%");
    }

    public void updateGiteeToken(String usernameLogin, String username) {
        accountMapper.updateGiteeToken(usernameLogin, username);
    }

    public String getAccountByGiteeName(String usernameLogin) {
        return accountMapper.getAccountByGiteeName(usernameLogin);
    }
}
