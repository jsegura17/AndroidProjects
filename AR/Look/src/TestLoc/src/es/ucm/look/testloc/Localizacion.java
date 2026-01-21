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
package es.ucm.look.testloc;

import java.util.Timer;
import java.util.TimerTask;

import es.ucm.look.location.LocationManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

public class Localizacion extends Activity {

	LocationManager location = null;
	Timer timer = null;

	public static boolean wifi;
	public static boolean ins;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localizacion);

	}

	@Override
	protected void onStart() {

		location = new LocationManager(this.getApplicationContext(), ins, wifi);
		location.start();
		timer = new Timer();

		TimerTask timerTask = new TimerTask() {
			public void run() {
				 Log.i("TestLoc", location.getPosition().toString() + "  " +
				 location.isWalking());
				runOnUiThread(new Runnable() {
					public void run() {

						((TextView) findViewById(R.id.xval)).setText(String
								.valueOf(location.getPosition().x));
						((TextView) findViewById(R.id.yval)).setText(String
								.valueOf(location.getPosition().y));
						((TextView) findViewById(R.id.zval)).setText(String
								.valueOf(location.getPosition().z));

						if (location.isWalking()) {
							((TextView) findViewById(R.id.mov))
									.setText("En Movimiento");
						} else {
							((TextView) findViewById(R.id.mov))
									.setText("Parado");

						}
					}
				});
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 200);
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onPause();

		timer.cancel();
		location.stop();
		finish();

	}

}
