package com.hiveel.auth.service.impl;

import com.hiveel.auth.dao.AccountDao;
import com.hiveel.auth.dao.ForgetDao;
import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.Forget;
import com.hiveel.auth.service.ForgetService;
import com.hiveel.auth.util.JwtUtil;
import com.hiveel.core.exception.UnauthorizationException;
import com.hiveel.core.util.DigestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForgetServiceImpl implements ForgetService {
    @Autowired
    private ForgetDao dao;
    @Autowired
    private AccountDao accountDao;

    @Override
    public int save(Forget e) {
        e.fillNotRequire();
        e.createAt();
        e.updateAt();
        return dao.save(e);
    }

    @Override
    public int delete(Forget e) {
        return dao.delete(e);
    }

    @Override
    public int update(Forget e) {
        e.updateAt();
        return dao.update(e);
    }

    @Override
    public Forget findById(Forget e) {
        return dao.findById(e);
    }

    @Override
    public Integer count(SearchCondition searchCondition) {
        return dao.count(searchCondition);
    }

    @Override
    public List<Forget> find(SearchCondition searchCondition) {
        searchCondition.setDefaultSortBy("createAt");
        return dao.find(searchCondition);
    }

    @Override
    public Integer countByAccountId(SearchCondition searchCondition, Forget e) {
        return dao.countByAccountId(searchCondition, e);
    }

    @Override
    public List<Forget> findByAccountId(SearchCondition searchCondition, Forget e) {
        searchCondition.setDefaultSortBy("id");
        return dao.findByAccountId(searchCondition, e);
    }

    @Override
    public Forget findByAccountIdAndCode(Forget e) {
        Forget result = dao.findByAccountIdAndCode(e);
        return result != null ? result : Forget.NULL;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePassword(Forget e) throws UnauthorizationException {
        String token = e.getToken();
        Account account = JwtUtil.decode(token);
        account.setPassword(DigestUtil.md5(e.getPassword()));
        account.updateAt();
        accountDao.updatePassword(account);
        List<Forget> inDbList = findByAccountId(new SearchCondition(), new Forget.Builder().set("account", account).build());
        Forget inDb = inDbList.get(0);
        update(new Forget.Builder().set("id", inDb.getId()).set("code", "").build());
    }
}
