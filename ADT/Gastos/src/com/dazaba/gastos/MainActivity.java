package com.dazaba.gastos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
		Button btnLoginOk = (Button) findViewById(R.id.btnLogin);		
        
        //Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(MainActivity.this, Registro.class);
				startActivity(i);				
			}
		});
        
        //Listening to Button Login in Menu
        btnLoginOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(MainActivity.this, menuPrincipal.class);				
				startActivity(i);	
				//Close this Activity
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
