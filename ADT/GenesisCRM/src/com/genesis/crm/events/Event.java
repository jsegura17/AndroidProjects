package com.genesis.crm.events;

public interface Event {
	
	public String getType();
	public Object getSource();
	public void setSource(Object source);
}
