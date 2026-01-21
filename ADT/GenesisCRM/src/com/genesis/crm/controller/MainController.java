package com.genesis.crm.controller;

import java.util.EventObject;

public class MainController extends EventObject {
	
	public static final String LOGIN_OK = "loginOk";
	
	public MainController(Object source){
		super(source);
	}
}
