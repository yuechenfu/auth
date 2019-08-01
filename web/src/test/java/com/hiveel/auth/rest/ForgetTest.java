package com.hiveel.auth.rest;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.controller.rest.ForgetController;
import com.hiveel.auth.model.ProjectRestCode;
import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.TokenRest;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.Forget;
import com.hiveel.auth.service.AccountService;
import com.hiveel.auth.service.ForgetService;
import com.hiveel.auth.util.JwtUtil;
import com.hiveel.core.debug.DebugSetting;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.SendGridEmailUtil;
import com.hiveel.core.util.ShortMessageUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ForgetTest {
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ForgetService forgetService;

    @Value("${core.sendgrid.templates.RESET_PASSWORD:d-fe20d9bdb7cb4d808260a3354072da23}")
    private String resetEmailTeplate;

    @Test
    public void resetPasswordByEmail() throws Exception {
        String email = emailAccount.getUsername();
        //请求重置密码发送验证码邮件接口
        String result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                .param("userName", email)
        ).andReturn().getResponse().getContentAsString();
        Rest rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        //查询产生的验证码
        List<Forget> list = forgetService.find(new SearchCondition());
        Forget forget = list.get(0);
        Assert.assertEquals(emailAccount.getId(),forget.getAccount().getId());
        String code = forget.getCode();

        //检查是否调用过发送验证码方法（email发邮件）
        String subject = "Change Your Email";
        Map<String, Object> data = new HashMap<>();
        char[] codes = forget.getCode().toCharArray();
        for (int i = 0; i < codes.length; i++) {
            String key = "code" + (i + 1);
            data.put(key, new String(new char[]{codes[i]}));
        }
        Mockito.verify(mockEmailUnit).sendByTemplate(email, subject, resetEmailTeplate, data);


        //使用验证码，获得token
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/code")
                .param("userName", email).param("code", code)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> tokenRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, tokenRest.getCode());
        String token = tokenRest.getData();

        //使用token 更改密码
        String password = "123456789";
        result = mvc.perform(MockMvcRequestBuilders.post("/forget")
                .param("password", password).param("token", token)
        ).andReturn().getResponse().getContentAsString();

        //使用新密码可以登入
        result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent", "chrome")
                .param("username", email).param("password", password)
        ).andReturn().getResponse().getContentAsString();
        Rest<TokenRest> loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {  }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());
        //清理数据
        forgetService.delete(forget);
    }

    @Test
    public void resetPasswordByPhone() throws Exception {
        String phone =  phoneAccount.getUsername();
        //请求重置密码发送验证码邮件接口
        String result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                .param("userName", phone)
        ).andReturn().getResponse().getContentAsString();
        Rest rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        //查询产生的验证码
        List<Forget> list = forgetService.find(new SearchCondition());
        Forget forget = list.get(0);
        Assert.assertEquals(phoneAccount.getId(),forget.getAccount().getId());
        String code = forget.getCode();

        //检查是否调用过发送验证码方法（发短信）
        String content = "you can reset your password by enter this 4-digit code in yourr app. the code is:" + forget.getCode();
        Mockito.verify(mockSmsUtil).sendByNexmo(phone, content);

        //使用验证码，获得token
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/code")
                .param("userName", phone).param("code", code)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> tokenRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, tokenRest.getCode());
        String token = tokenRest.getData();

        //使用token 更改密码
        String password = "12312345";
        result = mvc.perform(MockMvcRequestBuilders.post("/forget")
                .param("password", password).param("token", token)
        ).andReturn().getResponse().getContentAsString();

        //使用新密码可以登入
        result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent", "chrome")
                .param("username", phone).param("password", password)
        ).andReturn().getResponse().getContentAsString();
        Rest<TokenRest> loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {  }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());
        //清理数据
        forgetService.delete(forget);
    }

    @Test
    public void tooManyForgetFail() throws Exception {
        DebugSetting.debug = false;
        String email = "testusername6@hiveel.com";
        Long accountId = 6L;
        //发送3次验证码
        for (int i = 0; i < 3; i++) {
            //请求重置密码发送验证码邮件接口
            String result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                    .param("userName", email)
            ).andReturn().getResponse().getContentAsString();
            Rest<Boolean> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {
            }.getType());
            Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        }
        //第四次 发送验证码 不能再发
        String result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                .param("userName", email)
        ).andReturn().getResponse().getContentAsString();
        Rest  rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest >() { }.getType());
        Assert.assertEquals(ProjectRestCode.TOO_MANY_PASSWORD_CHANGE, rest.getCode());

        //清理数据
        List<Forget> list = forgetService.findByAccountId(new SearchCondition(), new Forget.Builder().set("account", new Account.Builder().set("id",accountId).build() ).build());
        list.stream().forEach(e -> forgetService.delete(e));
        DebugSetting.debug = true;
    }

    @Test
    public void verifyCodeFail() throws Exception {
        //输入不存在的email 或 手机
        String errEmail = "xxxbbb@hvwww.com";
        String result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                .param("userName", errEmail)
        ).andReturn().getResponse().getContentAsString();
        Rest  rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest >() { }.getType());
        Assert.assertEquals(ProjectRestCode.USERNAME_PASSWORD_INCORRECT, rest.getCode());

        //输入不存在的email 或 手机
        String errPhone = "1234567890";
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                .param("userName", errPhone)
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest >() { }.getType());
        Assert.assertEquals(ProjectRestCode.USERNAME_PASSWORD_INCORRECT, rest.getCode());

        String email = emailAccount.getUsername();
        Long accountId = emailAccount.getId();
        //请求重置密码发送验证码邮件接口
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/verify")
                .param("userName", email)
        ).andReturn().getResponse().getContentAsString();
        rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() { }.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

        //查询产生的验证码
        List<Forget> list = forgetService.find(new SearchCondition());
        Forget forget = list.get(0);
        Assert.assertEquals(emailAccount.getId(),forget.getAccount().getId());
        String code = forget.getCode();

        //使用验证码，假如输入错误的验证码
        String wrongCode = "8888";
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/code")
                .param("userName", email).param("code", wrongCode)
        ).andReturn().getResponse().getContentAsString();
        Rest<String> tokenRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(ProjectRestCode.FORGET_CODE_INVALIDATE, tokenRest.getCode());

        //使用验证码，假如输入错误的email
        String wrongEmail = "testusername1@hiveel.com";
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/code")
                .param("userName", wrongEmail).param("code", code)
        ).andReturn().getResponse().getContentAsString();
         tokenRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(ProjectRestCode.FORGET_CODE_INVALIDATE, tokenRest.getCode());

        //假如验证码已经过期
        //先人为设置验证码创建时间为半小时之前
        forget.setCreateAt(forget.getCreateAt().minusMinutes(31));
        jdbcTemplate.update("update forget set createAt = ? where id = ?", new Object[]{forget.getCreateAt(), forget.getId()});
        result = mvc.perform(MockMvcRequestBuilders.post("/forget/code")
                .param("userName", email).param("code", code)
        ).andReturn().getResponse().getContentAsString();
        tokenRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<String>>() { }.getType());
        Assert.assertEquals(ProjectRestCode.FORGET_CODE_INVALIDATE, tokenRest.getCode());

        //清理数据
        forgetService.delete(forget);
    }

    @Autowired
    private ForgetController forgetController;
    private SendGridEmailUtil mockEmailUnit = null;
    private ShortMessageUtil mockSmsUtil = null;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Account emailAccount ;

    private Account phoneAccount ;
    @Autowired
    private AccountService accountService;

    @Before
    public void setUp() throws Exception{
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockEmailUnit = Mockito.mock(SendGridEmailUtil.class);
        mockSmsUtil = Mockito.mock(ShortMessageUtil.class);
        forgetController.setEmailUtil(mockEmailUnit);
        forgetController.setShortMessageUtil(mockSmsUtil);
        emailAccount =  new Account.Builder().set("username", "handsomeBoy@hiveel.com").set("password", "aaaaaa").set("extra","DR").set("personId","30").set("mg",false).build();
        phoneAccount =  new Account.Builder().set("username", "9087972489").set("password", "aaaaaa").set("extra","DR").set("personId","30").set("mg",false).build();
        accountService.save(emailAccount);
        accountService.save(phoneAccount);
    }

    @After
    public void clear(){
        accountService.delete(emailAccount);
        accountService.delete(phoneAccount);
    }

}
