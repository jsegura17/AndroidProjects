package com.dazaba.dto;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class dao_manager {
	//Singlenton
	private static dao_manager _instance;
	
	//Web Services Variables
	private final String NAMESPACE = "http://tempuri.org/";
    private final String URL = "http://www.w3schools.com/webservices/tempconvert.asmx";
    private final String SOAPACTION = "http://tempuri.org/FahrenheitToCelsius";
    private final String METHOD = "FahrenheitToCelsius";
    
    //DTO
    private WS_Data _ws_data;
	
	//Constructor
	private dao_manager() {
		
	}
	
	//Singlenton return
	public static dao_manager getInstance()
	{
		if(_instance == null)
		{
			_instance = new dao_manager();
		}
		return _instance;
	}
	
	//Read Web Service
	public void read_WS()
	{
		SoapObject request = new SoapObject(NAMESPACE, METHOD);
		String fahrenheit_value = "50";
		 
        PropertyInfo FahrenheitProp = new PropertyInfo();
        
        FahrenheitProp.setName("Fahrenheit");
        FahrenheitProp.setValue(fahrenheit_value);
        FahrenheitProp.setType(String.class);
        request.addProperty(FahrenheitProp);
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        try
        {
            androidHttpTransport.call(SOAPACTION, envelope);
            SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
            _ws_data = new WS_Data();
            _ws_data.set_ResponseWS(response.toString());
        }catch(Exception e)
        {
            e.printStackTrace();
            _ws_data.set_ResponseWS("Error in WebService");
        }
	}
	
	//Return DTO's
	public WS_Data get_WS_Data(){
		return _ws_data;
	}
}
