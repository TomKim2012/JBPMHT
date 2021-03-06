package com.duggan.workflow.client.ui.login;

import com.duggan.workflow.client.ui.component.IssuesPanel;
import com.gwtplatform.mvp.client.ViewImpl;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class LoginView extends ViewImpl implements LoginPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, LoginView> {
	}

	@UiField Anchor aLogin;
	@UiField IssuesPanel issues;

	
	@Inject
	public LoginView(final Binder binder) {
		widget = binder.createAndBindUi(this);
	}
	
	public native String getUsername()/*-{
		var userid = $doc.getElementById("userid").value;
		return userid;
	}-*/;

	@Override
	public Widget asWidget() {
		return widget;
	}
	
	public native String getPassword()/*-{
		var userpass = $doc.getElementById("userpass").value;
		return userpass;	
	}-*/;
	
	public HasClickHandlers getLoginBtn(){
		return aLogin;
	}

	@Override
	public void setPasswordKeyHandler(KeyDownHandler keyHandler) {
		//passwordBox.addKeyDownHandler(keyHandler);
	}

	@Override
	public boolean isValid() {
		String username=getUsername();
		String pass=getPassword();
		boolean isValid=true;
		
		if(isNullOrEmpty(username)){
			issues.addError("Username required");
			isValid=false;
		}
		
		if(isNullOrEmpty(pass)){
			issues.addError("Password required");
			isValid=false;
		}
		
		return isValid;
	}
	
	boolean isNullOrEmpty(String value){
		return value==null || value.trim().length()==0;
	}


	@Override
	public void setError(String error) {
		issues.addError(error);
	}
}
