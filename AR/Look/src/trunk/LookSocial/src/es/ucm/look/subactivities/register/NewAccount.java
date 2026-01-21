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
package es.ucm.look.subactivities.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import es.ucm.look.R;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;
import es.ucm.look.data.remote.LookProperties;
import es.ucm.look.entities.user.UserEntity;

public class NewAccount extends Activity {

	private ProgressDialog pd;

	private String name = "";

	private String status = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_account);

		final Button moreOptionsButton = (Button) findViewById(R.id.moreOptionsButton);
		moreOptionsButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent settings = new Intent(NewAccount.this,
						MoreOptionsAccount.class);
				startActivityForResult(settings, R.id.moreOptionsButton);
			}
		});

		final Button cancelButton = (Button) findViewById(R.id.cancel);
		final Button saveButton = (Button) findViewById(R.id.save);

		saveButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				pd = ProgressDialog.show(NewAccount.this, getResources()
						.getText(R.string.account_setup), getResources()
						.getText(R.string.attempting_configure), true, false);

				LookData.getInstance().getServiceManager().setHandler(mHandler);
				Thread action = new Thread() {
					public void run() {
						Looper.prepare();
						configureAccount();
						Looper.loop();
					}
				};
				action.start();

			}
		});

		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				Bundle bundle = new Bundle();

				bundle.putString("returnStatus", "CANCEL");
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_CANCELED, mIntent);
				finish();
			}
		});

	}

	protected void configureAccount() {

		// capture the entered fields *needs validation*
		EditText emailET = (EditText) findViewById(R.id.email);
		final String email = emailET.getText().toString().trim();
		EditText usernameET = (EditText) findViewById(R.id.username);
		final String username = usernameET.getText().toString().trim();
		EditText passwordET = (EditText) findViewById(R.id.password);
		final String password = passwordET.getText().toString().trim();

		if (email.equals("") || username.equals("") || password.equals("")) {
			if (pd.isShowing())
				pd.dismiss();
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					NewAccount.this);
			dialogBuilder.setTitle(getResources().getText(
					R.string.required_fields));
			dialogBuilder.setMessage(getResources().getText(
					R.string.allfields_required));
			dialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});
			dialogBuilder.setCancelable(true);
			dialogBuilder.create().show();
		} else {
			// the properties are created
			EntityData userData = new EntityData(UserEntity.TYPE_USER);

			userData.setPropertyValue(UserEntity.PROPERTY_PASSWORD, password);
			userData.setPropertyValue(UserEntity.PROPERTY_USER, username);
			userData.setPropertyValue(UserEntity.PROPERTY_EMAIL, email);
			userData.setPropertyValue(UserEntity.PROPERTY_NAME, name);
			userData.setPropertyValue(UserEntity.PROPERTY_INFO, status);

			LookData.getInstance().getDataHandler().addEntity(userData);

		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case LookProperties.ACTION_ADD_ELEMENT:

				if (pd != null && pd.isShowing())
					pd.dismiss();

				String idString = (String) msg.obj.toString();
				int id = Integer.parseInt(idString);

				// ID of new user
				if (id != -1) {
					// user created correctly
					Toast.makeText(NewAccount.this,
							getResources().getText(R.string.user_created),
							Toast.LENGTH_SHORT).show();

					// send the id
					Bundle bundle = new Bundle();
					bundle.putInt("id", id);
					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					NewAccount.this.finish();

				} else {

					Toast.makeText(
							NewAccount.this,
							getResources().getText(R.string.user_already_exits),
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		switch (requestCode) {
		case R.id.moreOptionsButton:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				name = extras.getString("name");
				status = extras.getString("status");
			}
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int i, KeyEvent event) {

		// only intercept back button press
		if (i == KeyEvent.KEYCODE_BACK) {
			Bundle bundle = new Bundle();

			bundle.putString("returnStatus", "CANCEL");
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_CANCELED, mIntent);
			finish();
		}

		return false; // propagate this keyevent
	}

}
