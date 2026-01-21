package com.example.webservicesample;

import java.text.DecimalFormat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.webservicesample.dto.WS_Data;
import com.genesisnetcr.test.convertfc.R;
import com.genesisnetcr.test.convertfc.R.id;
 
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
 

public class WSMain extends Activity {
	private final String NAMESPACE = "http://tempuri.org/";
    private final String URL = "http://www.w3schools.com/webservices/tempconvert.asmx";
    private final String SOAPACTION = "http://tempuri.org/FahrenheitToCelsius";
    private final String METHOD = "FahrenheitToCelsius";
    
    private WS_Data _ws_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsmain);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wsmain, menu);
        return true;
    }
    
    public void convertFahrenheitToCelsius(View  v)
    {
        TextView response_tv = (TextView)findViewById(id.txt_response);
        TextView response_ws = (TextView)findViewById(id.editText_Data);
        final TextView input_tv = (TextView)findViewById(id.inputTemp);
        String fahrenheit_value = input_tv.getText().toString();
 
        if(fahrenheit_value == null || fahrenheit_value.length() == 0)
        {
            response_tv.setText("Error!");
            return;
        }
 
        SoapObject request = new SoapObject(NAMESPACE, METHOD);
 
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
            DecimalFormat formato = new DecimalFormat("###,###.00");
            String formatValue = formato.format(Double.valueOf(response.toString()));
            response_tv.setText(fahrenheit_value + " °F = " + formatValue + " °C");
            
            _ws_data = new WS_Data();
            _ws_data.set_ResponseWS(response.toString());
            
            response_ws.setText(_ws_data.get_ResponseWS());
        }catch(Exception e)
        {
            e.printStackTrace();
            response_tv.setText("Error in WebService");
        }
    }
    
}
