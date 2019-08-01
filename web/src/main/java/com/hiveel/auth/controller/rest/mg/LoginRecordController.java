package com.hiveel.auth.controller.rest.mg;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.LoginRecord;
import com.hiveel.auth.service.LoginRecordService;
import com.hiveel.core.exception.ParameterException;
import com.hiveel.core.exception.util.ParameterExceptionUtil;
import com.hiveel.core.model.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("LoginRecordController")
@RequestMapping("/mg")
public class LoginRecordController {

    @Autowired
    private LoginRecordService service;

    @GetMapping("/loginRecord/count")
    public Rest<Integer> countByPersionId(SearchCondition searchCondition, LoginRecord e) throws ParameterException {
        ParameterExceptionUtil.verify("personId", e.getPersonId()).isNotEmpty();
        Integer count = service.countByPersionId(searchCondition, e);
        return Rest.createSuccess(count);
    }

    @GetMapping("/loginRecord")
    public Rest<List<LoginRecord>> findByPersonId(SearchCondition searchCondition, LoginRecord e) throws ParameterException {
        ParameterExceptionUtil.verify("personId", e.getPersonId()).isNotEmpty();
        List<LoginRecord> list = service.findByPersionId(searchCondition, e);
        return Rest.createSuccess(list);
    }

}
