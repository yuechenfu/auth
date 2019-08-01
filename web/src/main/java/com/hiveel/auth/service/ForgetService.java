package com.hiveel.auth.service;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Forget;
import com.hiveel.core.exception.ServiceException;
import com.hiveel.core.exception.UnauthorizationException;

import java.util.List;

public interface ForgetService {

    int save(Forget e)  ;

    int delete(Forget e) ;

    int update(Forget e);

    Forget findById(Forget e);

    Integer count(SearchCondition searchCondition);

    List<Forget> find(SearchCondition searchCondition);

    Integer countByAccountId(SearchCondition searchCondition, Forget e);

    List<Forget> findByAccountId(SearchCondition searchCondition, Forget e);

    Forget findByAccountIdAndCode(Forget e);

    /**
     * 重置密码
     *
     * @param e 必须包含有效的token 和新的密码
     * @throws ServiceException
     */
    void updatePassword(Forget e) throws UnauthorizationException;
}
