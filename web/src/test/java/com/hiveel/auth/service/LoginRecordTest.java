package com.hiveel.auth.service;

import com.hiveel.auth.model.SearchCondition;
import com.hiveel.auth.model.entity.LoginRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LoginRecordTest {
	@Autowired
	private LoginRecordService service;

	@Test
	public void saveDelete() {
		LoginRecord e = new LoginRecord.Builder().set("personId", "3").set("ip","140.115.61.93").set("device","chrome").build();
		int count = service.save(e);
		Assert.assertEquals(1, count);
		count = service.delete(e);
		Assert.assertEquals(1, count);
	}

	@Test
	public void count(){
		SearchCondition searchCondition = new SearchCondition();
		searchCondition.setMinDate("2019-02-05T00:34:21");
		searchCondition.setMaxDate("2019-02-08T12:34:21");
		int count = service.count(searchCondition);
		Assert.assertEquals(3,count);
	}

	@Test
	public void find(){
		SearchCondition searchCondition = new SearchCondition();
		// 时间记录hms吧
		searchCondition.setMinDate("2019-02-05T00:34:21");
		searchCondition.setMaxDate("2019-02-08T12:34:21");
		List<LoginRecord> list = service.find(searchCondition);
		Assert.assertEquals(3,list.size());
	}

	@Test
	public void countByPersonId(){
		SearchCondition searchCondition = new SearchCondition();
		LoginRecord loginRecord = new  LoginRecord.Builder().set("personId","5").build();
		int count = service.countByPersionId(searchCondition,loginRecord);
		Assert.assertEquals(3,count);
	}

	@Test
	public void findByPersonId(){
		SearchCondition searchCondition = new SearchCondition();
		LoginRecord loginRecord = new  LoginRecord.Builder().set("personId","5").build();
		List<LoginRecord> list = service.findByPersionId(searchCondition,loginRecord);
		Assert.assertEquals(3,list.size());
	}
}
