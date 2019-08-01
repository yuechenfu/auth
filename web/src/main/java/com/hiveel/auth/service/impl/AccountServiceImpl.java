package com.hiveel.auth.service.impl;

import com.hiveel.auth.dao.AccountDao;
import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.service.AccountService;
import com.hiveel.core.exception.FailException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao dao;

    @Override
    public boolean save(Account e) throws FailException {
        e.fillNotRequire();
        e.createAt();
        e.updateAt();
        boolean exists = dao.countByUsername(e) == 1;
        if (exists) throw new FailException("exists");
        e.md5Password();
        return dao.save(e) == 1;
    }

    @Override
    public boolean delete(Account e) {
        return dao.delete(e) == 1;
    }

    @Override
    public void deleteByPersonIdAndUsername(Account e) {
        List<Account> accountList = findByPersonId(new SearchCondition(), e);
        Account emailAccount = getAccount(accountList, email);
        Account phoneAccount = getAccount(accountList, phone);
        if (e.getUsername().indexOf("@") != -1) {
            dao.delete(emailAccount);
        } else {
            dao.delete(phoneAccount);
        }
    }

    @Override
    public boolean update(Account e) {
        e.updateAt();
        e.md5Password();
        return dao.update(e) == 1;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePassword(Account e) {
        e.md5Password();
        List<Account> accountList = findByPersonId(new SearchCondition(), e);
        accountList.stream().forEach(acc->{
            acc.updateAt();
            acc.setPassword(e.getPassword());
            dao.update(acc);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateByPersonIdAndUsername(Account e) {
        List<Account> accountList = findByPersonId(new SearchCondition(), e);
        if (e.getExtra() != null || e.getPassword() != null) {
            String password = e.getPassword();
            accountList.stream().forEach(acc -> {
                acc.setExtra(e.getExtra());
                acc.setPassword(password);
                acc.md5Password();
                acc.updateAt();
                dao.update(acc);
                e.setPassword(acc.getPassword());
            });
        }
        Account emailAccount = getAccount(accountList, email);
        Account phoneAccount = getAccount(accountList, phone);
        String username = e.getUsername();
        if (username.indexOf("@") != -1) {
            if (emailAccount.isNull()) {
                BeanUtils.copyProperties(phoneAccount, e);
            }
            e.setId(emailAccount.getId());
        } else {
            if (phoneAccount.isNull()) {
                BeanUtils.copyProperties(emailAccount, e);
            }
            e.setId(phoneAccount.getId());
        }
        e.setUsername(username);
        if (e.getId() != null) {
            e.updateAt();
            dao.update(e);
        } else {
            e.createAt();
            e.updateAt();
            dao.save(e);
        }
    }

    private final static String email = "email";
    private final static String phone = "phone";

    private Account getAccount(List<Account> accountList, String type) {
        return accountList.stream()
                .filter(e -> type.equals(email) ? e.getUsername().indexOf("@") != -1 : e.getUsername().indexOf("@") == -1)
                .findFirst().orElse(Account.NULL);
    }

    @Override
    public Account findById(Account e) {
        Account result = dao.findById(e);
        return result != null ? result : Account.NULL;
    }

    @Override
    public List<Account> findByPersonId(SearchCondition searchCondition, Account e) {
        searchCondition.setDefaultSortBy("id");
        return dao.findByPersonId(searchCondition, e);
    }

    @Override
    public Account findByLogin(Account e) {
        e.md5Password();
        Account result = dao.findByUsernameAndPassword(e);
        return result != null ? result : Account.NULL;
    }

    @Override
    public int countByUsername(Account e) {
        return dao.countByUsername(e);
    }

    @Override
    public Account findByUsername(Account e) {
        Account result = dao.findByUsername(e);
        return result != null ? result : Account.NULL;
    }


}
