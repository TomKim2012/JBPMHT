package com.duggan.workflow.server.actionhandlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.duggan.workflow.client.util.Definitions;
import com.duggan.workflow.shared.model.HTUser;
import com.duggan.workflow.shared.requests.GetContextRequest;
import com.duggan.workflow.shared.responses.BaseResult;
import com.duggan.workflow.shared.responses.GetContextRequestResult;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.dispatch.server.ExecutionContext;
import com.gwtplatform.dispatch.shared.ActionException;

import static com.duggan.workflow.server.actionhandlers.ServerConstants.*;

public class GetContextRequestActionHandler extends 
		BaseActionHandler<GetContextRequest, GetContextRequestResult> {

	private final Provider<HttpServletRequest> httpRequest;

	@Inject
	public GetContextRequestActionHandler(Provider<HttpServletRequest> httpRequest) {
		this.httpRequest= httpRequest;
	}

	@Override
	public GetContextRequestResult execute(GetContextRequest action,
			BaseResult actionResult, ExecutionContext execContext)
			throws ActionException {
		
		HttpSession session = httpRequest.get().getSession(false);
		
		
		Object sessionid=session.getAttribute(ServerConstants.AUTHENTICATIONCOOKIE);
		Object user = session.getAttribute(ServerConstants.USER);

		GetContextRequestResult result = (GetContextRequestResult)actionResult;
		result.setIsValid(session!=null && user!=null);
		
		if(result.getIsValid()){
			result.setUser((HTUser)user);
		}
		
		return result;
	}

	@Override
	public void undo(GetContextRequest action, GetContextRequestResult result,
			ExecutionContext context) throws ActionException {
	}

	@Override
	public Class<GetContextRequest> getActionType() {
		return GetContextRequest.class;
	}
}
