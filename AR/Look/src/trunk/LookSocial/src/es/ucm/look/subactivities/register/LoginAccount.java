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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import es.ucm.look.R;
import es.ucm.look.data.LookData;
import es.ucm.look.data.remote.LookProperties;

public class LoginAccount extends Activity {

	private ProgressDialog pd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_account);


		final Button cancelButton = (Button) findViewById(R.id.cancel);
		final Button LoadButton = (Button) this.findViewById(R.id.load);
		final Button signUpButton = (Button) findViewById(R.id.new_account);

		LoadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pd = ProgressDialog.show(LoginAccount.this, getResources()
						.getText(R.string.account_setup), getResources()
						.getText(R.string.attempting_configure), true, false);

				LookData.getInstance().getServiceManager().setHandler(mHandler);
				Thread action = new Thread() {
					public void run() {
						Looper.prepare();
						loadAccount();
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

		signUpButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent signupIntent = new Intent(LoginAccount.this,
						NewAccount.class);
				startActivityForResult(signupIntent, R.id.new_account);
			}
		});

	}
	
	@Override
	public void onResume() {
		super.onResume();
	
	}

	private void loadAccount() {
		// capture the entered fields *needs validation*
		EditText usernameET = (EditText) findViewById(R.id.username);
		final String username = usernameET.getText().toString().trim();
		EditText passwordET = (EditText) findViewById(R.id.password);
		final String password = passwordET.getText().toString().trim();

		if (username.equals("") || password.equals("")) {
			pd.dismiss();
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
					LoginAccount.this);
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
			
			try {
				LookData.getInstance().getLogger().doLogin(username,password);
			} catch (RemoteException e) {
				if (pd != null && pd.isShowing())
					pd.dismiss();
				// Network Error
				Toast.makeText(LoginAccount.this,
						getResources().getText(R.string.net_error),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case LookProperties.ACTION_LOGIN:

				if (pd.isShowing())
				    pd.dismiss();

				String idString = (String) msg.obj.toString();
				int id = Integer.parseInt(idString);

				// ID of my user
				if (id == -1) {
					Toast.makeText(LoginAccount.this, getResources().getText(R.string.incorrect_user),
							Toast.LENGTH_SHORT).show();
				} else if (id == -2) {
					Toast.makeText(LoginAccount.this,
							getResources().getText(R.string.incorrect_password), Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(LoginAccount.this, getResources().getText(R.string.logged_correctly),
							Toast.LENGTH_SHORT).show();

					// send the id
					Bundle bundle = new Bundle();
					bundle.putInt("id", id);
					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					LoginAccount.this.finish();
				}

				break;
			default:
			}
		}
	};
	
	/**
	 * Get the response of newAccount button and return it
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		switch (requestCode) {
		case R.id.new_account:
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				int id  = extras.getInt("id");
				// send the id
				Bundle bundle = new Bundle();
				bundle.putInt("id", id);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				LoginAccount.this.finish();

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
