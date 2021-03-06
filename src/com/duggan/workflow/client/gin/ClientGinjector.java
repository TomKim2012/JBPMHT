package com.duggan.workflow.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.gwtplatform.dispatch.client.gin.DispatchAsyncModule;
import com.duggan.workflow.client.gin.ClientModule;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.google.gwt.inject.client.AsyncProvider;
import com.duggan.workflow.client.ui.MainPagePresenter;
import com.duggan.workflow.client.ui.error.ErrorPagePresenter;
import com.duggan.workflow.client.ui.home.HomePresenter;
import com.duggan.workflow.client.ui.task.personalreview.PersonalReviewPresenter;
import com.duggan.workflow.client.ui.task.perfomancereview.PersonnelReviewPresenter;
import com.duggan.workflow.client.ui.login.LoginGateKeeper;
import com.duggan.workflow.client.ui.login.LoginPresenter;
import com.duggan.workflow.client.ui.error.NotfoundPresenter;

@GinModules({ DispatchAsyncModule.class, ClientModule.class })
public interface ClientGinjector extends Ginjector {

	EventBus getEventBus();

	PlaceManager getPlaceManager();

	AsyncProvider<MainPagePresenter> getMainPagePresenter();

	AsyncProvider<HomePresenter> getTaskListUIPresenter();

	AsyncProvider<PersonalReviewPresenter> getPersonalReviewPresenter();

	AsyncProvider<PersonnelReviewPresenter> getPersonnelReviewPresenter();
	
	AsyncProvider<ErrorPagePresenter> getErrorPagePresenter();

	AsyncProvider<LoginPresenter> getLoginPresenter();
	
	LoginGateKeeper getLoggedInGateKeeper();

	AsyncProvider<NotfoundPresenter> getNotfoundPresenter(); 
	

}
