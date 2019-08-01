package com.hiveel.auth.service;

import com.hiveel.auth.model.entity.Account;
import com.hiveel.core.util.DigestUtil;
import org.junit.Assert;
import org.junit.Test;

public class XXMd5Test {
    @Test
    public void md5Test(){
        String aa = "0ab1b06fd7d54e53521a94db69004fda";
        String newPassword = "2314567";
        String md5 = DigestUtil.md5(newPassword);
        Account acc = new Account();
        acc.setPassword(newPassword);
        acc.md5Password();
        String bb = acc.getPassword();

    }
}
