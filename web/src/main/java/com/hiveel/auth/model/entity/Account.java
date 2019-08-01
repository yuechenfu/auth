package com.hiveel.auth.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hiveel.core.model.builder.AbstractBuilder;
import com.hiveel.core.nullhandler.NotNullObject;
import com.hiveel.core.nullhandler.NullObject;
import com.hiveel.core.util.DateUtil;
import com.hiveel.core.util.DigestUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Account implements Serializable, NotNullObject {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String personId;
    @JsonIgnore
    private Boolean mg; // 用于判断是否能请求mg路径
    private String extra;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public void fillNotRequire() {
        personId = personId != null ? personId : "";
        mg = mg != null ? mg : false;
        extra = extra != null ? extra : "";
    }

    public void createAt() {
        createAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public void md5Password(){
        if(this.getPassword()!=null){
            this.setPassword(DigestUtil.md5(this.getPassword()));
        }
    }

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
        if (!StringUtils.isEmpty(username) && username.length() == 10 && username.indexOf("@") == -1) { //如果是电话
            username = "1" + username; //自动加美国 电话区号
        }
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getPersonId() {
        return personId;
    }

    public Boolean getMg() {
        return mg;
    }

    public void setMg(Boolean mg) {
        this.mg = mg;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
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
                ", mg=" + mg +
                ", createAt='" + createAt + '\'' +
                ", updateAt='" + updateAt + '\'' +
                '}';
    }
}
