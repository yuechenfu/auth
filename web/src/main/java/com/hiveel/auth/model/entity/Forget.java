package com.hiveel.auth.model.entity;

import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Forget implements Serializable, NotNullObject {

    private Long id;
    private Account account;
    private String userName;
    private String password;
    private String code;
    private String token;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public static final Forget.Null NULL = new Forget.Null();
    public void createAt() {
        createAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void updateAt() {
        updateAt =  LocalDateTime.now(ZoneId.of("UTC"));
    }
    public void fillNotRequire() {

    }

    private static class Null extends Forget implements NullObject {
        @Override
        public Account getAccount() {
            return Account.NULL;
        }
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Forget build() {
            try {
                return super.build(Forget.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (!StringUtils.isEmpty(userName) && userName.length() == 10 && userName.indexOf("@") == -1) { //如果是电话
            userName = "1" + userName; //自动加美国 电话区号
        }
        this.userName = userName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
