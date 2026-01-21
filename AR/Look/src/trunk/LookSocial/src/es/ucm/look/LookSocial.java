/*******************************************************************************
 * Look! is a Framework of Augmented Reality for Android. 
 * 
 * Copyright (C) 2011 
 * 		Sergio Bellón Alcarazo
 * 		Jorge Creixell Rojo
 * 		Ángel Serrano Laguna
 * 	
 * 	   Final Year Project developed to Sistemas Informáticos 2010/2011 - Facultad de Informática - Universidad Complutense de Madrid - Spain
 * 	
 * 	   Project led by: Jorge J. Gómez Sánz
 * 
 * 
 * ****************************************************************************
 * 
 * This file is part of Look! (http://lookar.sf.net/)
 * 
 * Look! is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/
 ******************************************************************************/
package es.ucm.look;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import es.ucm.look.data.LookData;
import es.ucm.look.data.local.contentprovider.LookContentProvider;
import es.ucm.look.data.remote.RemoteDBHandler;
import es.ucm.look.data.remote.restful.ServiceManager;
import es.ucm.look.entities.LookSocialEntityFactory;
import es.ucm.look.subactivities.LookMenu;
import es.ucm.look.subactivities.register.LoginAccount;
import es.ucm.look.subactivities.register.NewAccount;

/**
 * Main Activity for Look! Social
 * 
 * 
 */
public class LookSocial extends Activity {

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);
		
		ServiceManager servicemanager = new ServiceManager(this);
		//Start the Data Service
		RemoteDBHandler dataHandler = new RemoteDBHandler(this, servicemanager,
				"http://147.96.80.89:5000/LookServer/resources/",
				"http://www.sergiobellon.com/files/");
		LookData.getInstance().setDataHandler(dataHandler);
		// Set Database SQLlite
		LookContentProvider.getInstance().setContentResolver(
				this.getContentResolver());
		
		LookData.getInstance().setWorldEntityFactory(new LookSocialEntityFactory( ));
		
		
		
		Button newAccountButton = (Button) this
				.findViewById(R.id.new_accountOption);
		newAccountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(LookSocial.this, NewAccount.class);
				startActivityForResult(i, R.id.new_accountOption);
			}
		});

		Button loginButton = (Button) this
				.findViewById(R.id.login_accountOption);
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(LookSocial.this, LoginAccount.class);
				startActivityForResult(i, R.id.login_accountOption);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case R.id.new_accountOption:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				// save our id
				int id = extras.getInt("id");
				LookData.getInstance().getLogger().setId(id);
				// A contact was picked. Here we will just display it
				// to the user.
				startActivityForResult((new Intent(LookSocial.this,
						LookMenu.class)), 1);
				LookSocial.this.finish();
			}
		case R.id.login_accountOption:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				// save our id
				int id = extras.getInt("id");
				LookData.getInstance().getLogger().setId(id);
				// A contact was picked. Here we will just display it
				// to the user.
				startActivityForResult((new Intent(LookSocial.this,
						LookMenu.class)), 1);
				LookSocial.this.finish();
			}
		case 1: // close account
			if (resultCode == RESULT_OK) {
				// load this activity again
			}
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
