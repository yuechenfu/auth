package com.hiveel.auth.model;

import com.hiveel.auth.model.entity.Account;
import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;

import java.io.Serializable;

public class TokenRest implements Serializable, NotNullObject {
    private Account account;
    private String token;
    private String refreshToken;

    public static final Null NULL = new Null();
    private static class Null extends TokenRest implements NullObject {
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public TokenRest build() {
            try {
                return super.build(TokenRest.class);
            } catch (InstantiationException | IllegalAccessException e) {
                return NULL;
            }
        }
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "TokenRest{" +
                "account=" + account +
                ", token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
