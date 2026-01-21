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
package es.ucm.look.subactivities.look3d;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import es.ucm.look.R;
import es.ucm.look.ar.LookAR;
import es.ucm.look.ar.ar3D.core.camera.Camera3D;
import es.ucm.look.ar.math.geom.Vector3;
import es.ucm.look.ar.util.DeviceOrientation;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;
import es.ucm.look.data.remote.LookProperties;
import es.ucm.look.entities.notes.NewNoteActivity;
import es.ucm.look.entities.notes.Note;

public class LookAugmentedReality extends LookAR {

	private ProgressDialog pd;

	public LookAugmentedReality() {
		super(true, false, true, true, 50.0f);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setWorld(LookData.getInstance().getWorld());
		super.onCreate(savedInstanceState);

		LookData.getInstance().startLocation(100, true, false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.armenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.add_note:
			Intent i = new Intent( this, NewNoteActivity.class );
			this.startActivityForResult(i, 1);
			return true;
		default:
			updateWorld();
			return true;
		}
	}

	private void updateWorld() {
		pd = ProgressDialog
				.show(LookAugmentedReality.this,
						getResources().getText(R.string.updating),
						getResources().getText(R.string.attempting_update),
						true, false);

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
			case LookProperties.UPDATE_DB:
				if (pd.isShowing())
					pd.dismiss();

				// world updated
				Toast.makeText(LookAugmentedReality.this,
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

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( requestCode == 1 && resultCode == Activity.RESULT_OK ){
			String text = data.getExtras().get("text").toString();
			Vector3 v = new Vector3( Camera3D.NORTH );
			v.rotateY(DeviceOrientation.getDeviceOrientation(this).getAzimuth());
			
			final EntityData entityData = new EntityData( Note.TYPE_NOTE );
			entityData.getLocation().set( LookData.getInstance().getLocation() );
			entityData.getLocation().subtract(v);
			entityData.setPropertyValue(Note.PROPERTY_INFO, text);
			entityData.setPropertyValue(Note.PROPERTY_SENDER_ID, "" + LookData.getInstance().getLogger().getId() );
			Log.i("data", entityData.getLocation().toString() );
			
			new Thread(){
				public void run( ){
					Looper.prepare();
					LookData.getInstance().getDataHandler().addEntity(entityData);
					LookData.getInstance().updateData();
					Looper.loop();
				}
			}.start();
			
			
			
		}
	}
	
	

}
