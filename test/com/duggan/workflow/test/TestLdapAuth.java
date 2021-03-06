package com.duggan.workflow.test;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.duggan.workflow.server.helper.auth.LoginHelper;
import com.duggan.workflow.server.helper.jbpm.JBPMHelper;
import com.duggan.workflow.shared.model.HTUser;
import com.duggan.workflow.shared.model.UserGroup;

public class TestLdapAuth {

	@Test
	public void getUser(){
		String userId = "calcacuervo";
		HTUser user = LoginHelper.get().getUser(userId);
		
		Assert.assertNotNull(user);
	}
	
	@Ignore
	public void saveUser(){
		HTUser user = new HTUser();
		user.setEmail("mdkimani@gmail.com");
		user.setId("dkimani");
		user.setName("Duggan Kimani");
		user.setPassword("kimani2012");
		user.setSurname("Macharia");
		
		user = LoginHelper.get().createUser(user);
		
		try{
			server();
		}catch(Exception e){}
		
	
	}
	
	@Ignore
	public void getGroupsForUser(){
		List<UserGroup> groups = LoginHelper.get().getGroupsForUser("Administrator");
		
		for(UserGroup group: groups){
			System.err.println(group.getName());
		}
	}
	
	@Ignore
	public void getGroups(){
		List<UserGroup> groups = LoginHelper.get().retrieveGroups();
		
		for(UserGroup group: groups){
			System.err.println(group.getName());
		}
	}
	
	@Ignore
	public void getUsers(){
		List<HTUser> users = LoginHelper.get().retrieveUsers();
		
		for(HTUser user: users){
			System.err.println(user.getId()+" : "+user.getName()+" : "+user.getEmail());
		}
		Assert.assertTrue(users.size()>0);
	}
	
	@Ignore
	public void authenticate() throws InterruptedException{
		boolean valid = LoginHelper.get().login("mariano", "pass");

		Assert.assertTrue(valid);
	}
	
	@Ignore
	public void server() throws Exception{
		LoginHelper.get();
		Thread.sleep(3600*1000);
	}
	
	@After
	public void destroy() throws IOException{
		LoginHelper.get().close();
	}
}
