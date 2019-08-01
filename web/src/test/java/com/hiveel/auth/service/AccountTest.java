package com.hiveel.auth.service;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.core.exception.FailException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountTest {

    @Autowired
    private AccountService service;

    @Test
    public void saveDelete() throws FailException {
        Account e = new Account.Builder().set("username", "111111").set("password", "aaaaaa").build();
        boolean success = service.save(e);
        Assert.assertEquals(true, success);
        success = service.delete(e);
        Assert.assertEquals(true, success);
    }

    @Test
    public void update() throws FailException {
        Account e = new Account.Builder().set("id", 1L).set("personId", "3").build();
        boolean success = service.update(e);
        Assert.assertEquals(true, success);
        e.setPersonId("1");
        service.update(e);
    }

    @Test
    public void findById() {
        Account e = new Account.Builder().set("id", 1L).build();
        Account inDb = service.findById(e);
        Assert.assertEquals(1, (long) inDb.getId());
    }

    @Test
    public void findByPersonId() {
        Account e = new Account.Builder().set("personId", "1").build();
        List<Account> list = service.findByPersonId(new SearchCondition(), e);
        Assert.assertEquals(1, (long) list.get(0).getId());
    }

    @Test
    public void countByUsername() {
        Account e = new Account.Builder().set("username", "testusername1@hiveel.com").build();
        int count = service.countByUsername(e);
        Assert.assertEquals(1, count);
    }

    @Test
    public void findByLogin() {
        Account e = new Account.Builder().set("username", "testusername1@hiveel.com").set("password", "testpassword").build();
        Account inDb = service.findByLogin(e);
        Assert.assertEquals(1, (long) inDb.getId());
    }
}
