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

public class EditAccount extends Activity {

	private ProgressDialog pd;

	private int count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account);

		count = 0;

		pd = ProgressDialog.show(EditAccount.this,
				getResources().getText(R.string.account_setup), getResources()
						.getText(R.string.attempting_configure), true, false);

		LookData.getInstance().getServiceManager().setHandler(mHandler);
		Thread action = new Thread() {
			public void run() {
				Looper.prepare();
				getFields();
				Looper.loop();
			}
		};
		action.start();

	}

	@Override
	public void onResume() {
		super.onResume();
		
	}

	protected void configureAccount() {

		// capture the entered fields *needs validation*
		EditText emailET = (EditText) findViewById(R.id.email);
		final String email = emailET.getText().toString().trim();
		EditText usernameET = (EditText) findViewById(R.id.username);
		final String username = usernameET.getText().toString().trim();
		EditText passwordET = (EditText) findViewById(R.id.password);
		final String password = passwordET.getText().toString().trim();
		EditText nameET = (EditText) findViewById(R.id.complete_name);
		final String name = nameET.getText().toString();
		EditText statusET = (EditText) findViewById(R.id.status);
		final String status = statusET.getText().toString();

		if (email.equals("") || username.equals("") || password.equals("")) {
			if (pd.isShowing())
				pd.dismiss();

			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					EditAccount.this);
			dialogBuilder.setTitle(getResources().getText(
					R.string.required_fields));
			dialogBuilder.setMessage(getResources().getText(
					R.string.email_user_password_required));
			dialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});
			dialogBuilder.setCancelable(true);
			dialogBuilder.create().show();
		} else {
			// all except the username
			EntityData auxEntity = new EntityData();
			auxEntity.setId(LookData.getInstance().getLogger().getId());

			LookData.getInstance()
					.getDataHandler()
					.updateProperty(auxEntity,
							UserEntity.PROPERTY_PASSWORD, password);
			LookData.getInstance()
					.getDataHandler()
					.updateProperty(auxEntity, UserEntity.PROPERTY_EMAIL,
							email);
			LookData.getInstance()
					.getDataHandler()
					.updateProperty(auxEntity, UserEntity.PROPERTY_NAME,
							name);
			LookData.getInstance()
					.getDataHandler()
					.updateProperty(auxEntity, UserEntity.PROPERTY_INFO,
							status);
		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case LookProperties.ACTION_MODIFY_PROPERTY:

				count++;
				if (count == 4)
					setFinish();

				break;
			case LookProperties.ACTION_GET_PROPERTIES_USER:

				if (pd.isShowing())
					pd.dismiss();

				Bundle bundle = msg.getData();
				EntityData entityData = (EntityData) bundle
						.getSerializable("response");

				viewFields(entityData);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

	private void setFinish() {

		if (pd != null && pd.isShowing())
			pd.dismiss();

		// user created correctly
		Toast.makeText(EditAccount.this,
				getResources().getText(R.string.modified_user),
				Toast.LENGTH_SHORT).show();

		// send the id
		Bundle bundle = new Bundle();
		Intent mIntent = new Intent();
		mIntent.putExtras(bundle);
		setResult(RESULT_OK, mIntent);
		EditAccount.this.finish();
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

	private void viewFields(EntityData entityData) {

		EditText usernameET = (EditText) findViewById(R.id.username);
		usernameET.setText(entityData
				.getPropertyValue(UserEntity.PROPERTY_USER));

		EditText emailET = (EditText) findViewById(R.id.email);
		emailET.setText(entityData.getPropertyValue(UserEntity.PROPERTY_EMAIL));

		EditText passwordET = (EditText) findViewById(R.id.password);
		passwordET.setText(entityData
				.getPropertyValue(UserEntity.PROPERTY_PASSWORD));

		EditText nameET = (EditText) findViewById(R.id.complete_name);
		nameET.setText(entityData.getPropertyValue(UserEntity.PROPERTY_NAME));

		EditText statusET = (EditText) findViewById(R.id.status);
		statusET.setText(entityData.getPropertyValue(UserEntity.PROPERTY_INFO));

		final Button cancelButton = (Button) findViewById(R.id.cancel);
		final Button saveButton = (Button) findViewById(R.id.save);

		saveButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				pd = ProgressDialog.show(EditAccount.this, getResources()
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

	private void getFields() {
		try {
			LookData.getInstance().getLogger().getPropertiesUser();
		} catch (RemoteException e) {
			if (pd != null && pd.isShowing())
				pd.dismiss();
			// Network Error
			Toast.makeText(EditAccount.this,
					getResources().getText(R.string.net_error),
					Toast.LENGTH_SHORT).show();
		}
	}
}
