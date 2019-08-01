package com.hiveel.auth.controller.rest.api;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.service.AccountService;
import com.hiveel.core.exception.FailException;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("AccountControllerApi")
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountService service;

    @PostMapping("/account")
    public Rest<Account> save(Account e) throws FailException, ParameterException {
        ParameterExceptionUtil.verify("account.username", e.getUsername()).isLengthIn(3, 30);
        ParameterExceptionUtil.verify("account.password", e.getPassword()).isLengthIn(3, 30);
        service.save(e);
        return Rest.createSuccess(e);
    }

    @DeleteMapping("/account/{id}")
    public Rest<Boolean> delete(Account e) throws ParameterException {
        ParameterExceptionUtil.verify("account.id", e.getId()).isPositive();
        return Rest.createSuccess(service.delete(e));
    }

    @DeleteMapping("/account")
    public Rest<Boolean> deleteByPersonIdAndUsername(Account e) throws ParameterException {
        ParameterExceptionUtil.verify("account.personId", e.getPersonId()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("account.username", e.getUsername()).isNotEmpty();
        service.deleteByPersonIdAndUsername(e);
        return Rest.createSuccess(true);
    }

    @PutMapping("/account")
    public Rest<Boolean> updateByPersonIdAndUsername(Account e) throws ParameterException, FailException {
        ParameterExceptionUtil.verify("account.personId", e.getPersonId()).isLengthIn(1, 50);
        ParameterExceptionUtil.verify("account.username", e.getUsername()).isNotEmpty();
        service.updateByPersonIdAndUsername(e);
        return Rest.createSuccess(true);
    }

    @GetMapping("/account/{id}")
    public Rest<Account> findById(Account e) throws ParameterException {
        ParameterExceptionUtil.verify("account.id", e.getId()).isPositive();
        Account inDb = service.findById(e);
        return Rest.createSuccess(inDb);
    }

    @GetMapping("/personId/{personId}/account")
    public Rest<Account> findByPersonId(SearchCondition searchCondition, Account e) throws ParameterException {
        ParameterExceptionUtil.verify("account.personId", e.getPersonId()).isLengthIn(1, 50);
        List<Account> list = service.findByPersonId(searchCondition, e);
        return Rest.createSuccess(list.get(0));
    }
}
