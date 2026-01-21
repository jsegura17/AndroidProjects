package com.dazaba.dto;

public class WS_Data {
	private String ResponseWS;
	
	public WS_Data() {
		ResponseWS = "NULL";
	}
	
	public void set_ResponseWS(String dataWS){
		ResponseWS = dataWS;
	}
	
	public String get_ResponseWS(){
		return ResponseWS;
	}

}
