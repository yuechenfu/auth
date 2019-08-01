package com.hiveel.auth.model;

import com.hiveel.core.model.rest.RestCode;

public enum ProjectRestCode implements RestCode {
    USERNAME_PASSWORD_INCORRECT(10, "用户名密码错"),
    OLD_PASSWORD_INCORRECT(11,"旧密码错误"),
    TOO_MANY_PASSWORD_CHANGE(12,"修改密码过于频繁"),
    FORGET_CODE_INVALIDATE(13,"验证码无效"),
    ;
    private int code;
    private String message;

    ProjectRestCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
    @Override
    public int getCode() {
        return code;
    }
    @Override
    public String getMessage() {
        return message;
    }

}
