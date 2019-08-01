package com.hiveel.auth.service;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.LoginRecord;

import java.util.List;

public interface LoginRecordService {

    int save(LoginRecord e)  ;

    int delete(LoginRecord e) ;

    Integer count(SearchCondition searchCondition);

    List<LoginRecord> find(SearchCondition searchCondition);

    Integer countByPersionId(SearchCondition searchCondition, LoginRecord e);

    List<LoginRecord> findByPersionId(SearchCondition searchCondition,LoginRecord e);
}
