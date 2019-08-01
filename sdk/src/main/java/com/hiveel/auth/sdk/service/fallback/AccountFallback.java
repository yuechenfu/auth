package com.hiveel.auth.sdk.service.fallback;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.auth.sdk.service.AccountService;
import com.hiveel.core.log.util.LogUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class AccountFallback implements FallbackFactory<AccountService> {
    @Override
    public AccountService create(Throwable throwable) {
        LogUtil.error("", throwable);
        return new AccountService() {
            @Override
            public Rest<Account> me(String token) {
                return Rest.createFail(BasicRestCode.UNAUTHORIZED);
            }
            @Override
            public Rest<Boolean> check(String userName) {
                return Rest.createFail();
            }
            @Override
            public Rest<Boolean> updateByPersonIdAndUsername(Account e) {
                return Rest.createFail();
            }
            @Override
            public Rest<Account> save(Account e) {
                return Rest.createFail();
            }
            @Override
            public Rest<Boolean> deleteByPersonIdAndUsername(Account e) {
                return Rest.createFail();
            }
            @Override
            public Rest<Account> findByPersonId(Account e) {
                return Rest.createFail();
            }
        };
    }
}
