package com.hiveel.auth.controller.rest;

import com.hiveel.auth.model.ProjectRestCode;
import com.hiveel.auth.model.TokenRest;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.LoginRecord;
import com.hiveel.auth.service.AccountService;
import com.hiveel.auth.service.LoginRecordService;
import com.hiveel.auth.util.JwtUtil;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.ServiceException;
import com.hiveel.core.exception.UnauthorizationException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AccountController {
    @Autowired
    private AccountService service;
    @Autowired
    private LoginRecordService loginRecordService;

    @PutMapping("/me/password")
    public Rest<Boolean> updatePassword(@RequestAttribute("loginAccount") Account loginAccount, Account e, String oldPassword) throws ParameterException, ServiceException {
        ParameterExceptionUtil.verify("account.password", e.getPassword()).isNotEmpty();
        ParameterExceptionUtil.verify("oldPassword", oldPassword).isNotEmpty();
        Account inDb = service.findById(loginAccount);
        if (!inDb.getPassword().equals(DigestUtil.md5(oldPassword))) throw new ServiceException("password incorrect", ProjectRestCode.OLD_PASSWORD_INCORRECT);
        e.setPersonId(inDb.getPersonId());
        service.updatePassword(e);
        return Rest.createSuccess(true);
    }

    @GetMapping("/me")
    public Rest<Account> me(@RequestAttribute("loginAccount") Account loginAccount) {
        return Rest.createSuccess(loginAccount);
    }

    @PostMapping("/token/refresh")
    public Rest<TokenRest> refresh(TokenRest tokenRest) throws UnauthorizationException {
        Object tokenInMemcached = MemcachedUtil.getAndRefresh(tokenRest.getRefreshToken(), 60 * 30);
        if (tokenInMemcached == null) {
            return Rest.createSuccess(tokenRest);
        }
        Account e = JwtUtil.decode(String.valueOf(tokenInMemcached));
        tokenRest.setToken(JwtUtil.generate(e));
        MemcachedUtil.save(tokenRest.getRefreshToken(),tokenRest.getToken(),60*30);
        return Rest.createSuccess(tokenRest);
    }

    @PostMapping("/login")
    public Rest<TokenRest> login(Account e, HttpServletRequest request) throws ServiceException {
        Account inDb = service.findByLogin(e);
        if (inDb.isNull()) throw new ServiceException("username password incorrect", ProjectRestCode.USERNAME_PASSWORD_INCORRECT);
        saveLogin(inDb, request.getRemoteAddr(), WebClientUtil.getBowser(request));
        String token = JwtUtil.generate(inDb);
        String refreshToken = StringUtil.generateRandomString(200);
        MemcachedUtil.save(refreshToken, token, 60 * 30);
        TokenRest tokenRest = new TokenRest.Builder().set("account", inDb).set("token", token).set("refreshToken", refreshToken).build();
        return Rest.createSuccess(tokenRest);
    }

    private void saveLogin(Account account, String ip, String device) {
        ThreadUtil.run(() -> {
            LoginRecord loginRecord = new LoginRecord.Builder().set("personId", account.getPersonId()).set("ip", ip).set("device", device).build();
            loginRecordService.save(loginRecord);
        });
    }

    @PostMapping("/logout")
    public Rest<Boolean> logout(TokenRest tokenRest) {
        Object tokenInMemcached = MemcachedUtil.get(tokenRest.getRefreshToken());
        if (tokenInMemcached == null) {
            return Rest.createSuccess(false);
        }
        String token = String.valueOf(tokenInMemcached);
        MemcachedUtil.save("block:" + token, "ok", 60 * 30);
        MemcachedUtil.remove(tokenRest.getRefreshToken());
        return Rest.createSuccess(true);
    }

    @GetMapping("/account/check")
    public Rest<Boolean> check(String userName) throws ParameterException{
        ParameterExceptionUtil.verify("username", userName).isLengthIn(1,60);
        boolean exists = service.countByUsername(new Account.Builder().set("username",userName).build()) > 0;
        return Rest.createSuccess(exists);
    }
}
