package com.duggan.workflow.client.ui.error;

import com.duggan.workflow.client.place.NameTokens;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;

public class ErrorView extends PopupViewImpl implements ErrorPresenter.MyView {

	private final Widget widget;

	public interface Binder extends UiBinder<Widget, ErrorView> {
	}

	@UiField SpanElement spnError;
	@UiField Button btnOk;
	@UiField Hyperlink lnkError;
	
	@Inject PlaceManager manager;
	
	@Inject
	public ErrorView(final EventBus eventBus, final Binder binder) {
		super(eventBus);
		widget = binder.createAndBindUi(this);
		
		btnOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		lnkError.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		}, ClickEvent.getType());
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	@Override
	public void setMessage(String message, Integer id) {

		spnError.setInnerHTML(message);
		
		System.err.println("####### Error");
		if(id!=null){
			lnkError.setTargetHistoryToken(manager.buildHistoryToken(
					new PlaceRequest(NameTokens.error).with("errorid", id+"")));
			lnkError.setText("view");
		}
		
	}
}
