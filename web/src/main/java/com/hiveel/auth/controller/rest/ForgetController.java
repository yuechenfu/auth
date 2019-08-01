package com.hiveel.auth.controller.rest;

import com.hiveel.auth.model.ProjectRestCode;
import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.Forget;
import com.hiveel.auth.service.AccountService;
import com.hiveel.auth.service.ForgetService;
import com.hiveel.auth.util.JwtUtil;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.ServiceException;
import com.hiveel.core.exception.UnauthorizationException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ForgetController {
    private final static int MAX_FORGET_COUNT = 3;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ForgetService service;
    @Autowired
    private SendGridEmailUtil emailUtil;
    @Autowired
    private ShortMessageUtil shortMessageUtil;
    @Value("${core.sendgrid.templates.RESET_PASSWORD:d-fe20d9bdb7cb4d808260a3354072da23}")
    private String resetEmailTeplate;

    @PostMapping("/forget/verify")
    public Rest sendCode(Forget e) throws ParameterException, ServiceException {
        ParameterExceptionUtil.verify("forget.userName", e.getUserName()).isLengthIn(3, 30);
        Account account = accountService.findByUsername(new Account.Builder().set("username", e.getUserName()).build());
        if (account.isNull())
            throw new ServiceException("username incorrect", ProjectRestCode.USERNAME_PASSWORD_INCORRECT);
        if (tooManyForget(account))
            throw new ServiceException("too many password change", ProjectRestCode.TOO_MANY_PASSWORD_CHANGE);
        String code = StringUtil.generateRandomInteger(4);
        e.setAccount(new Account.Builder().set("id", account.getId()).build());
        e.setCode(code);
        service.save(e);
        sendForgetCode(e);
        return Rest.createSuccess(DebugSetting.debug?code:"");
    }

    private boolean tooManyForget(Account account) {
        if (DebugSetting.debug) {
            return false;
        }
        SearchCondition searchCondition = new SearchCondition();
        LocalDateTime minTime = LocalDateTime.now(ZoneId.of("UTC")).minusDays(1);
        searchCondition.setMinDate(DateUtil.dateToString(minTime));
        int count = service.countByAccountId(searchCondition, new Forget.Builder().set("account", account).build());
        return count >= MAX_FORGET_COUNT;
    }

    private void sendForgetCode(Forget forget) {
        ThreadUtil.run(() -> {
            String name = forget.getUserName();
            try {
                if (name.indexOf("@") != -1) {
                    String subject = "Change Your Email";
                    Map<String, Object> data = new HashMap<>();
                    char[] codes = forget.getCode().toCharArray();
                    for (int i = 0; i < codes.length; i++) {
                        String key = "code" + (i + 1);
                        data.put(key, new String(new char[]{codes[i]}));
                    }
                    emailUtil.sendByTemplate(name, subject, resetEmailTeplate, data);
                } else {
                    String content = "You can reset your password by enter this 4-digit code in your app. The code is:" + forget.getCode();
                    shortMessageUtil.sendByNexmo(name, content);
                }
            } catch (Exception ex) {
                LogUtil.error("send code fail:", ex);
            }
        });
    }

    @PostMapping("/forget/code")
    public Rest<String> verfiyCode(Forget e) throws ParameterException, ServiceException {
        ParameterExceptionUtil.verify("forget.code", e.getCode()).isLength(4);
        ParameterExceptionUtil.verify("forget.userName", e.getUserName()).isLengthIn(3, 30);
        Account account = accountService.findByUsername(new Account.Builder().set("username", e.getUserName()).build());
        if (account.isNull())
            throw new ServiceException("username incorrect", ProjectRestCode.USERNAME_PASSWORD_INCORRECT);
        Forget forget = service.findByAccountIdAndCode(new Forget.Builder().set("account", account).set("code", e.getCode()).build());
        if (forget.isNull()) throw new ServiceException("code incorrect", ProjectRestCode.FORGET_CODE_INVALIDATE);
        if (!forget.getCreateAt().isAfter(LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(30)))
            throw new ServiceException("code expired", ProjectRestCode.FORGET_CODE_INVALIDATE);
        String token = JwtUtil.generate(account);
        return Rest.createSuccess(token);
    }

    @PostMapping("/forget")
    public Rest<Boolean> changePasswd(Forget e) throws ParameterException, UnauthorizationException {
        ParameterExceptionUtil.verify("forget.token", e.getToken()).isLengthIn(4, 200);
        ParameterExceptionUtil.verify("forget.passwod", e.getPassword()).isLengthIn(4, 20);
        service.updatePassword(e);
        return Rest.createSuccess(true);
    }

    //for testcase usage
    public void setEmailUtil(SendGridEmailUtil emailUtil) {
        this.emailUtil = emailUtil;
    }

    //for testcase usage
    public void setShortMessageUtil(ShortMessageUtil shortMessageUtil) {
        this.shortMessageUtil = shortMessageUtil;
    }
}

