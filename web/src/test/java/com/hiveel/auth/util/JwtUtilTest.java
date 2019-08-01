package com.hiveel.auth.util;

import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.service.XXMd5Test;
import com.hiveel.core.util.DigestUtil;
import org.junit.Assert;
import org.junit.Test;

public class JwtUtilTest {
    @Test
    public void decodeTest() throws Exception{
        Account e = new Account.Builder().set("id", 1L).set("personId", "2").set("mg",true).set("extra","MG").build();
        String encrypt = JwtUtil.generate(e);
        Account account = JwtUtil.decode(encrypt);
        Assert.assertEquals(e.getId(),account.getId());
        Assert.assertEquals(e.getPersonId(),account.getPersonId());
        Assert.assertEquals(true,account.getMg());
        Assert.assertEquals("MG",account.getExtra());
    }
}
