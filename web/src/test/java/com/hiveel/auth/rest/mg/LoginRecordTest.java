package com.hiveel.auth.rest.mg;

import com.google.gson.reflect.TypeToken;
import com.hiveel.auth.model.entity.Account;
import com.hiveel.auth.model.entity.LoginRecord;
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
public class LoginRecordTest {
	private MockMvc mvc;
	@Autowired
	private WebApplicationContext webApplicationContext;

	private String token;

	@Test
	public void findByPersonId() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.get("/mg/loginRecord").header("authorization", "Bearer "+token)
				.param("personId","5")
		).andReturn().getResponse().getContentAsString();
		Rest<List<LoginRecord>> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<List<LoginRecord>>>(){}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
		List<LoginRecord> list  = rest.getData();
		Assert.assertEquals(3,list.size());
	}

	@Test
	public void countByPersonId() throws Exception {
		String result = mvc.perform(MockMvcRequestBuilders.get("/mg/loginRecord/count").header("authorization", "Bearer "+token)
				.param("personId","5")
		).andReturn().getResponse().getContentAsString();
		Rest<Integer> rest = Rest.fromJson(result, BasicRestCode.class, new TypeToken<Rest<Integer>>(){}.getType());
		Assert.assertEquals(BasicRestCode.SUCCESS, rest.getCode());
		Integer count  = rest.getData();
		Assert.assertEquals(new Integer(3),count);
	}

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		token = JwtUtil.generate(new Account.Builder().set("id", 1L).set("personId","1").set("mg", true).build());
	}
}
