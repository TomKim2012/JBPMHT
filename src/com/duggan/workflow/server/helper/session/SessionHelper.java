package com.duggan.workflow.server.helper.session;

import java.io.Closeable;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.duggan.workflow.server.actionhandlers.ServerConstants;
import com.duggan.workflow.shared.model.CurrentUser;
import com.duggan.workflow.shared.model.HTUser;

/**
 * A utility class for retrieval and use of 
 * session variables & the HTTPSession
 * 
 * @author duggan
 *
 */
public class SessionHelper{

	static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
	
	/**
	 * 
	 * @return User This is the currently logged in user
	 */
	public static HTUser getCurrentUser(){
				
		HttpSession session = request.get().getSession(false);
		if(session==null){
			return null;
		}
		
		if(session.getAttribute(ServerConstants.USER)==null){
			return null;
		}
		
		return (HTUser)session.getAttribute(ServerConstants.USER);
	}
	
	/**
	 * This is a utility method to enable retrieval of request from 
	 * any part of the application
	 * 
	 * @param request
	 */
	public static void setHttpRequest(HttpServletRequest httprequest){
		request.set(httprequest);
	}
	
	public static void afterRequest(){
		request.set(null);
	}
	
	public static HttpServletRequest getHttpRequest(){
		return request.get();
	}
}
