package com.musselwhizzle.dispatcher.models;

import com.musselwhizzle.dispatcher.events.EventDispatcher;
import com.musselwhizzle.dispatcher.events.SimpleEvent;

public class FooModel extends EventDispatcher {
	
	public static class ChangeEvent extends SimpleEvent {
		public static final String MY_LABEL_CHANGED = "myLabelChanged";
		public static final String MY_COUNT_CHANGED = "myCountChanged";
		
		public ChangeEvent(String type) {
			super(type);
		}
	}
	
	
	private String myLabel;
	public String getMyLabel() { return myLabel; }
	public void setMyLabel(String myLabel) {
		if (this.myLabel != null && this.myLabel.equals(myLabel)) return;
		this.myLabel = myLabel;
		dispatchEvent(new ChangeEvent(ChangeEvent.MY_LABEL_CHANGED));
	}
	
	private int myCount = 0;
	public int getMyCount() { return myCount; }
	public void setMyCount(int myCount) {
		if (this.myCount == myCount) return;
		this.myCount = myCount;
		dispatchEvent(new ChangeEvent(ChangeEvent.MY_COUNT_CHANGED));
	}
	
	public FooModel() {
		super();
	}
	

}
