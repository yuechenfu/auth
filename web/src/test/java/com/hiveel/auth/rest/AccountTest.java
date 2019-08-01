package com.hiveel.auth.rest;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.model.ProjectRestCode;
import com.hiveel.auth.model.TokenRest;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.LoginRecord;
import com.hiveel.auth.service.AccountService;
import com.hiveel.auth.util.JwtUtil;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountTest {
	private MockMvc mvc;
	@Autowired
	private WebApplicationContext webApplicationContext;

	private String token;

	@Test
	public void loginAndLogout() throws Exception{
		//先登入
		String result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", "testusername1@hiveel.com").param("password", "testpassword")
		).andReturn().getResponse().getContentAsString();
		Rest<TokenRest> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
		TokenRest tokenRest = rest.getData();

		//登入后使用得到的token 调用 /me接口成功
		result = mvc.perform(MockMvcRequestBuilders.get("/me")
				.header("authorization", "Bearer "+tokenRest.getToken())
		).andReturn().getResponse().getContentAsString();
		Rest<Account> meRest = Rest.fromJson(result,BasicRestCode.class, new TypeToken<Rest<Account>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, meRest.getCode());
		Account account = meRest.getData();
		Assert.assertEquals(Long.valueOf(1L),account.getId());
		Assert.assertEquals("DR",account.getExtra());

		//登出接口 验证
		result = mvc.perform(MockMvcRequestBuilders.post("/logout")
				.param("refreshToken", tokenRest.getRefreshToken()).param("token", tokenRest.getToken())
		).andReturn().getResponse().getContentAsString();
		Rest logoutRest = Rest.fromJson(result,BasicRestCode.class);
		Assert.assertEquals(BasicRestCode.SUCCESS, logoutRest.getCode());

		//登出之后，如果再用原来的token 调用 /me接口 显示失败
		result = mvc.perform(MockMvcRequestBuilders.get("/me")
				 .header("authorization", "Bearer "+tokenRest.getToken())
		).andReturn().getResponse().getContentAsString();
		meRest = Rest.fromJson(result,BasicRestCode.class);
		Assert.assertEquals(BasicRestCode.UNAUTHORIZED, meRest.getCode());
	}

	@Test
	public void login() throws Exception{
        //先登入
        String result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
                .param("username", "testusername3@hiveel.com").param("password", "testpassword")
        ).andReturn().getResponse().getContentAsString();
        Rest<TokenRest> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

        //查询登入日志是否存在
        result = mvc.perform(MockMvcRequestBuilders.get("/mg/loginRecord").header("authorization", "Bearer " + token)
                .param("personId","3")
        ).andReturn().getResponse().getContentAsString();
        Rest<List<LoginRecord>> restLogin = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<LoginRecord>>>(){}.getType());
        Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
        List<LoginRecord> list  = restLogin.getData();
        Assert.assertEquals(1,list.size());
    }

	@Test
	public void loginFail() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", "testusername1").param("password", "testpassword-wrong")
		).andReturn().getResponse().getContentAsString();
		Rest<TokenRest> rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(ProjectRestCode.USERNAME_PASSWORD_INCORRECT, rest.getCode());
	}

	@Test
	public void me() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.get("/me").header("authorization", "Bearer " + token)
		).andReturn().getResponse().getContentAsString();
		Rest<Account> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Account>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
		Assert.assertEquals(1, (long)rest.getData().getId());
	}

	@Autowired
	private AccountService accountService;

	@Test
	public void updatePassword() throws Exception {
		String personId = "26";
		String email = "hello@hiveel.com" ;
		String oldPassword = "testpassword";
		String extra = "DR" ;
		Account emailAccount = new Account.Builder()
				.set("username", email)
				.set("password", oldPassword)
				.set("extra",extra)
				.set("mg",false)
				.set("personId",personId).build();
		accountService.save(emailAccount);
		String phone = "9098874878" ;
		Account phoneAccount = new Account.Builder()
				.set("username", phone)
				.set("password", oldPassword)
				.set("extra",extra)
				.set("mg",false)
				.set("personId",personId).build();
		accountService.save(phoneAccount);

		Thread.sleep(1000l); //等待1秒以上才能产生新的loginToken，避免受 loginAndLogout()影响

		//先登入
		//用email登入
		String result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", email).param("password", oldPassword)
		).andReturn().getResponse().getContentAsString();
		Rest<TokenRest> loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());
		TokenRest tokenRest = loginRest.getData();

		//用phone登入
		result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", phone).param("password", oldPassword)
		).andReturn().getResponse().getContentAsString();
		loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());

		//改密码
		String newPassword = "testpassword222";
		result = mvc.perform(MockMvcRequestBuilders.put("/me/password").header("authorization", "Bearer " + tokenRest.getToken())
				.param("oldPassword", oldPassword)
				.param("password", newPassword)
		).andReturn().getResponse().getContentAsString();
		Rest<Boolean> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());

		//修改密码后用旧密码不能登入成功
		result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", "testusername1").param("password", oldPassword)
		).andReturn().getResponse().getContentAsString();
        loginRest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(ProjectRestCode.USERNAME_PASSWORD_INCORRECT, loginRest.getCode());


		//修改密码后用新密码登入成功
		result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", email).param("password", newPassword)
		).andReturn().getResponse().getContentAsString();
        loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());

		//修改密码后用新密码登入成功
		result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", phone).param("password", newPassword)
		).andReturn().getResponse().getContentAsString();
		loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());

		//还原数据
		result = mvc.perform(MockMvcRequestBuilders.put("/me/password").header("authorization", "Bearer " + tokenRest.getToken())
				.param("oldPassword", newPassword)
				.param("password", oldPassword)
		).andReturn().getResponse().getContentAsString();
		rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
	}

	@Test
	public void updatePasswordFail() throws Exception {
		String oldPassword = "testpassword";
		String oldPasswordWrong = "testpassword_Wrong";
		//先登入
		String result = mvc.perform(MockMvcRequestBuilders.post("/login").header("User-Agent","chrome")
				.param("username", "testusername1@hiveel.com").param("password", oldPassword)
		).andReturn().getResponse().getContentAsString();
		Rest<TokenRest> loginRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<TokenRest>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, loginRest.getCode());
		TokenRest tokenRest = loginRest.getData();

		//改密码  因为输入旧密码错误，所以不能修改
		String newPassword = "testpassword222";
		result = mvc.perform(MockMvcRequestBuilders.put("/me/password").header("authorization", "Bearer " + tokenRest.getToken())
				.param("oldPassword", oldPasswordWrong)
				.param("password", newPassword)
		).andReturn().getResponse().getContentAsString();
		Rest<Boolean> rest = Rest.fromJson(result, ProjectRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(ProjectRestCode.OLD_PASSWORD_INCORRECT, rest.getCode());
	}


	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		token = JwtUtil.generate(new Account.Builder().set("id", 1L).set("personId","1").set("mg", true).build());
	}
}
