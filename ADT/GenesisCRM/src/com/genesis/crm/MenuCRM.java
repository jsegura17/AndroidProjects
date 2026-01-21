package com.genesis.crm;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MenuCRM extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_crm);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_crm, menu);
		return true;
	}

}
