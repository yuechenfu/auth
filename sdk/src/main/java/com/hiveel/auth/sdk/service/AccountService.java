package com.hiveel.auth.sdk.service;

import com.hiveel.auth.sdk.model.Account;
import com.hiveel.auth.sdk.service.fallback.AccountFallback;
import com.hiveel.core.model.rest.Rest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-auth", url = "${freitx.accountServer}", fallbackFactory = AccountFallback.class)
public interface AccountService {
    /**
     * 保存账号
     *
     * @param e 参数对象里面的 username，password，personId，extra 属性必须有效
     * @return 是否保存成功
     */
    @PostMapping(value = "/api/account", consumes = {"application/x-www-form-urlencoded"})
    Rest<Account> save(Account e);

    /**
     * 删除账号
     *
     * @param e 参数对象里面必须包含有效的personId值 和 username值
     * @return 是否删除成功
     */
    @DeleteMapping(value = "/api/account", consumes = {"application/x-www-form-urlencoded"})
    Rest<Boolean> deleteByPersonIdAndUsername(Account e);

    /**
     * 更新账号
     *
     * @param e 参数对象里面必须包含有效的personId值
     * @return 是否删除成功
     */
    @PutMapping(value = "/api/account", consumes = {"application/x-www-form-urlencoded"} )
    Rest<Boolean> updateByPersonIdAndUsername(Account e);

    /**
     * 验证登录token是否有效，并返回用户信息
     *
     * @param token 登录后获得的token
     * @return 用户数据
     */
    @GetMapping(value = "/me")
    Rest<Account> me(@RequestHeader("authorization") String token);

    /**
     * 查询某账号是否存在
     *
     * @param userName
     * @return 账号存在返回true, 不存在返回false
     */
    @GetMapping(value = "/account/check", consumes = {"application/x-www-form-urlencoded"})
    Rest<Boolean> check(@RequestParam("userName") String userName);

    /**
     * 查找关联personId的账号
     *
     * @param e 参数对象里面必须要有有效的personId
     * @return 对应的account账号信息
     */
    @GetMapping(value = "/api/personId/{personId}/account")
    public Rest<Account> findByPersonId(Account e);
}
