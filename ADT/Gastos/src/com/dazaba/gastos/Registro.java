package com.dazaba.gastos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Registro extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registro);
		
		TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        
        //Listening to register new account link
		loginScreen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Switching to Login Screen/closing register screen
				finish();				
			}
		});
	}	
}
