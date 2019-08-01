package com.hiveel.auth.service;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.core.exception.FailException;

import java.util.List;

public interface AccountService {

    boolean save(Account e) throws FailException;

    boolean delete(Account e);

    void deleteByPersonIdAndUsername(Account e);

    boolean update(Account e);

    void updatePassword(Account e);

    void updateByPersonIdAndUsername(Account e) throws FailException;

    Account findById(Account e);

    List<Account> findByPersonId(SearchCondition searchCondition, Account e);

    Account findByLogin(Account e);

    int countByUsername(Account e);

    Account findByUsername(Account e);


}
