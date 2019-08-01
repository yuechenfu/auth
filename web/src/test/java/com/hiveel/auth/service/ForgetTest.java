package com.hiveel.auth.service;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.Forget;
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
public class ForgetTest {
    @Autowired
    private ForgetService service;

    @Test
    public void saveDelete() {
        Forget e = new Forget.Builder().set("account", new Account.Builder().set("id",3L).build() ).set("code", "123525").build();
        int count = service.save(e);
        Assert.assertEquals(1, count);
        count = service.delete(e);
        Assert.assertEquals(1, count);
    }

    @Test
    public void findById() {
        Forget e = new Forget.Builder().set("id", 5L).build();
        Forget inDb = service.findById(e);
        Assert.assertEquals(Long.valueOf(1), inDb.getAccount().getId());
    }

    @Test
    public void update() {
        String code = "555555";
        Forget e = new Forget.Builder().set("id", 5L).set("code", code).build();
        service.update(e);
        Forget inDb = service.findById(e);
        Assert.assertEquals(code, inDb.getCode());
    }

    @Test
    public void count() {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setMinDate("2019-02-18T00:20:20");
        searchCondition.setMaxDate("2019-02-18T12:20:20");
        int count = service.count(searchCondition);
        Assert.assertEquals(3, count);
    }

    @Test
    public void find() {
        SearchCondition searchCondition = new SearchCondition();
        searchCondition.setMinDate("2019-02-18T00:20:20");
        searchCondition.setMaxDate("2019-02-18T12:20:20");
        List<Forget> list = service.find(searchCondition);
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void countByAccountId() {
        SearchCondition searchCondition = new SearchCondition();
        Forget loginRecord = new Forget.Builder().set("account", new Account.Builder().set("id",2L).build() ).build();
        int count = service.countByAccountId(searchCondition, loginRecord);
        Assert.assertEquals(3, count);
    }

    @Test
    public void findByAccountId() {
        SearchCondition searchCondition = new SearchCondition();
        Forget loginRecord = new Forget.Builder().set("account", new Account.Builder().set("id",2L).build()).build();
        List<Forget> list = service.findByAccountId(searchCondition, loginRecord);
        Assert.assertEquals(3, list.size());
    }

}
