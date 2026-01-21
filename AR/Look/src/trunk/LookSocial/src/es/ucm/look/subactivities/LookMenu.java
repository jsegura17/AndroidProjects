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
package es.ucm.look.subactivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import es.ucm.look.LookSocial;
import es.ucm.look.R;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;
import es.ucm.look.data.remote.LookProperties;
import es.ucm.look.entities.user.UserEntity;
import es.ucm.look.subactivities.look3d.LookAugmentedReality;
import es.ucm.look.subactivities.looklist.LookList;
import es.ucm.look.subactivities.register.EditAccount;

public class LookMenu extends Activity {

	// we need to save our ID when a activity is restored to avoid the login
	private final String SAVE_ID = "SAVE_STATE_ID";

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_menu);


		// restored our user ID
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(SAVE_ID)) {
			// initialize all we need to start the application here

			LookData.getInstance().getLogger()
					.setId(savedInstanceState.getInt(SAVE_ID));

		}

		// TODO poner la imagen del usuario
		// TextView btn_name = (TextView) this.findViewById(R.id.name);
		// TODO ImageView btn_image = (ImageView) this.findViewById(R.id.image);

		this.getUserData();

	}

	private void getUserData() {
		// retrieve the user name by the ID
		pd = ProgressDialog.show(LookMenu.this,
				getResources().getText(R.string.account_setup), getResources()
						.getText(R.string.attempting_configure), true, false);

		LookData.getInstance().getServiceManager().setHandler(mHandler);
		Thread action = new Thread() {
			public void run() {
				Looper.prepare();

				try {
					LookData.getInstance().getLogger().getPropertiesUser();
				} catch (RemoteException e) {
					if (pd != null && pd.isShowing())
						pd.dismiss();
					// Network Error
					Toast.makeText(LookMenu.this,
							getResources().getText(R.string.net_error),
							Toast.LENGTH_SHORT).show();
				}

				Looper.loop();
			}
		};
		action.start();
	}

	private void showName(EntityData entityData) {
		String name = entityData.getPropertyValue(UserEntity.PROPERTY_NAME);

		// show the name of login user
		final TextView welcomeTV = (TextView) this.findViewById(R.id.welcome);
		welcomeTV.setText(this.getString(R.string.welcome) + ": " + name);

		final Button userListButton = (Button) findViewById(R.id.user_list);
		userListButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent settings = new Intent(LookMenu.this, LookList.class);
				startActivityForResult(settings, R.id.user_list);
			}
		});

		final Button augmentedRealityButton = (Button) findViewById(R.id.augmented_reality);
		augmentedRealityButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent settings = new Intent(LookMenu.this,
						LookAugmentedReality.class);
				startActivityForResult(settings, R.id.augmented_reality);
			}
		});

		// update World
		this.updateWorld();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("LookSocial", "Selected item menu: " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.edit_user:
			Intent settings = new Intent(LookMenu.this, EditAccount.class);
			startActivityForResult(settings, R.id.edit_user);

			break;
		case R.id.close_account:

			// create again the activity LookSocial
			Intent in = new Intent(LookMenu.this, LookSocial.class);
			startActivity(in);

			// finish this activity
			Bundle bundle = new Bundle();
			bundle.putString("returnStatus", "CANCEL");
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_CANCELED, mIntent);
			finish();

			break;
		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		switch (requestCode) {
		case R.id.edit_user:
			if (resultCode == RESULT_OK) {
				getUserData();

			}
			break;
		case R.id.user_list:
			if (resultCode == RESULT_OK) {
				getUserData();

			}
			break;
		case R.id.augmented_reality:
			if (resultCode == RESULT_OK) {
				getUserData();

			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle state) {
		state.putInt(SAVE_ID, LookData.getInstance().getLogger().getId());
		super.onSaveInstanceState(state);
	}

	@Override
	public void onDestroy() {
		LookData.getInstance().getServiceManager().destroy();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int i, KeyEvent event) {

		// only intercept back button press
		if (i == KeyEvent.KEYCODE_BACK) {
			// doing nothing
		}
		return false; // propagate this keyevent
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	private void updateWorld() {
		pd = ProgressDialog.show(LookMenu.this,
				getResources().getText(R.string.updating), getResources()
						.getText(R.string.attempting_update), true, false);
		LookData.getInstance().getServiceManager().setHandler(mHandler);
		Thread action = new Thread() {
			public void run() {
				Looper.prepare();

				LookData.getInstance().updateData();

				Looper.loop();
			}
		};
		action.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.i("LookData", "Update realizado");
			switch (msg.what) {
			case LookProperties.ACTION_GET_PROPERTIES_USER:

				if (pd != null && pd.isShowing())
					pd.dismiss();

				EntityData entityData = LookData.getInstance().getLogger()
						.getUser();

				showName(entityData);
				break;
			case LookProperties.UPDATE_DB:
				if (pd != null && pd.isShowing())
					pd.dismiss();

				// world updated
				Toast.makeText(LookMenu.this,
						getResources().getText(R.string.world_updated),
						Toast.LENGTH_SHORT).show();

				break;
			default:
				super.handleMessage(msg);
				break;
			}

		}

	};

	@Override
	public void onResume() {
		super.onResume();
	}

}
