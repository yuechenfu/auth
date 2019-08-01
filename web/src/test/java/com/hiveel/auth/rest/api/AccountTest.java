package com.hiveel.auth.rest.api;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.service.AccountService;
import com.hiveel.core.model.rest.BasicRestCode;
import com.hiveel.core.model.rest.Rest;
import com.hiveel.core.util.DigestUtil;
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
	@Autowired
	private AccountService accountService;

	@Test
	public void saveDelete() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.post("/api/account")
				.param("username", "bbb@ccc.com").param("password", "testpassword").param("extra","MG")
		).andReturn().getResponse().getContentAsString();
		Rest<Account> saveRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Account>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, saveRest.getCode());
		Account account = saveRest.getData();
		result = mvc.perform(MockMvcRequestBuilders.delete("/api/account/"+account.getId())
		).andReturn().getResponse().getContentAsString();
		Rest<Boolean> deleteRest = Rest.fromJson(result);
		Assert.assertEquals(BasicRestCode.SUCCESS, deleteRest.getCode());
	}


	@Test
	public void findById() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.get("/api/account/1")
		).andReturn().getResponse().getContentAsString();
		Rest<Boolean> rest = Rest.fromJson(result);
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
	}

	@Test
	public void findByPersonId() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.get("/api/personId/3/account")
		).andReturn().getResponse().getContentAsString();
		Rest<Boolean> rest = Rest.fromJson(result);
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
	}

	@Test
	public void updateAndDeleteByPersonId() throws Exception{
		//准备测试数据
		String personId = "25";
		String email = "hello@hiveel.com" ;
		String extra = "DR" ;
		Account e = new Account.Builder()
				.set("username", email)
				.set("password", "123456")
				.set("extra",extra)
				.set("mg",false)
				.set("personId",personId).build();
		accountService.save(e);

		//用户a(personId=25)已经有一条email账号数据
		List<Account> list = accountService.findByPersonId(new SearchCondition(),new Account.Builder().set("personId",personId).build());
		Assert.assertEquals(1,list.size());

		//用户a 更新 phone
		String phone = "9098872487" ;
		String result = mvc.perform(MockMvcRequestBuilders.put("/api/account")
				.param("username", phone).param("personId",personId)
		).andReturn().getResponse().getContentAsString();
		Rest<Boolean> updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

		//验证更新的结果
		list = accountService.findByPersonId(new SearchCondition(),new Account.Builder().set("personId",personId).build());
		Assert.assertEquals(2,list.size()); //多了一条账号记录
		for (Account account:list){
			String name = account.getUsername();
			if(name.indexOf("@")!=-1){
				Assert.assertEquals(email,name);
			}else {
				Assert.assertEquals(phone,name);
			}
		}

		String newEmail = "helloNNew@hiveel.com" ;
		result = mvc.perform(MockMvcRequestBuilders.put("/api/account")
				.param("username", newEmail).param("personId",personId)
		).andReturn().getResponse().getContentAsString();
		updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

		list = accountService.findByPersonId(new SearchCondition(),new Account.Builder().set("personId",personId).build());
		Assert.assertEquals(2,list.size()); //账号记录数量还是2条没变
		for (Account account:list){
			String name = account.getUsername();
			if(name.indexOf("@")!=-1){
				Assert.assertEquals(newEmail,name); //email的账号记录更新了
			}else {
				Assert.assertEquals(phone,name); //phone的账号记录没变
			}
		}

		String newPhone = "9099972488";
		String newPassword = "2314567";
		String newRole = "VE";
		result = mvc.perform(MockMvcRequestBuilders.put("/api/account")
				.param("username", newPhone).param("personId",personId)
				.param("password",newPassword).param("extra",newRole)
		).andReturn().getResponse().getContentAsString();
		updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

		list = accountService.findByPersonId(new SearchCondition(),new Account.Builder().set("personId",personId).build());
		Assert.assertEquals(2,list.size()); //账号记录数量还是2条没变
		for (Account account:list){
			String name = account.getUsername();
			if(name.indexOf("@")!=-1){
				Assert.assertEquals(newEmail,name); //email的账号记录没变
			}else {
				Assert.assertEquals(newPhone,name); //phone的账号更新了
			}
			Assert.assertEquals(newRole,account.getExtra());
			Assert.assertEquals(DigestUtil.md5(newPassword),account.getPassword());
		}


		//测试删除账号接口
		result = mvc.perform(MockMvcRequestBuilders.delete("/api/account")
				.param("username", newPhone).param("personId",personId)
		).andReturn().getResponse().getContentAsString();
		updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

		list = accountService.findByPersonId(new SearchCondition(),new Account.Builder().set("personId",personId).build());
		Assert.assertEquals(1,list.size()); //账号记录变成1
		Account account = list.get(0);
		//验证剩下的是email哪个账号
		Assert.assertEquals(newEmail, account.getUsername());

		result = mvc.perform(MockMvcRequestBuilders.delete("/api/account")
				.param("username", newEmail).param("personId",personId)
		).andReturn().getResponse().getContentAsString();
		updateRest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Boolean>>() {}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, updateRest.getCode());

		list = accountService.findByPersonId(new SearchCondition(),new Account.Builder().set("personId",personId).build());
		Assert.assertEquals(0,list.size()); //账号记录变成0

	}

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
}
