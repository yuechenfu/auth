package com.hiveel.auth.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;
import com.hiveel.core.util.DateUtil;

import java.io.Serializable;

public class Account implements Serializable, NotNullObject {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String personId;
    private String extra;
    private String createAt;
    private String updateAt;

    public static final Null NULL = new Null();
    private static class Null extends Account implements NullObject {
    }
    public static class Builder extends AbstractBuilder {
        @Override
        public Account build() {
            try {
                return super.build(Account.class);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", personId='" + personId + '\'' +
                ", createAt='" + createAt + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }
}
