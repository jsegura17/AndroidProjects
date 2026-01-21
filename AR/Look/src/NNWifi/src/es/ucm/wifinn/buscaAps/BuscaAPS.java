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
package es.ucm.wifinn.buscaAps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import es.ucm.wifinn.R;
import es.ucm.wifinn.Util.DateUtils;
import es.ucm.wifinn.Util.DeviceReader;
import es.ucm.wifinn.Util.DeviceWriter;

public class BuscaAPS extends Activity {

	public static final String APS = "/sdcard/APs.txt";

	private DeviceWriter out;

	private boolean scan_activo = false;
	private Button btnBuscar;

	private Timer timer = null;
	private HashSet<String> encontrados;

	private static final int REFRESH_RATE = 1; // segundos

	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;

	private StringBuffer data;

	protected Context context;
	
	PowerManager pm;
	PowerManager.WakeLock wl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buscaaps);
		context = this;

		btnBuscar = (Button) findViewById(R.id.Buscarbtn);

		data = new StringBuffer();

		btnBuscar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (scan_activo) {
					scan_activo = false;
					timer.cancel();
					btnBuscar.setText("Buscar");

					out = new DeviceWriter(APS);
					out.write(data.toString());
					out.close();

					Toast.makeText(context, "Guardado con exito",
							Toast.LENGTH_SHORT).show();

				} else {
					btnBuscar.setText("Stop");

					scan_activo = true;
					timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							mWifiManager.startScan();
						}

					}, 0, REFRESH_RATE * 1000);

				}

			}

		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		out.close();

	}

	@Override
	public void onPause() {
		super.onPause();

		if (timer != null) {
			timer.cancel();
		}
		
		wl.release();

	}

	@Override
	public void onResume() {
		super.onResume();
		encontrados = new HashSet<String>();

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiManager.setWifiEnabled(true);
		mWifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY,
				"Scan Only");
		mWifiReceiver = new WifiReceiver();

		registerReceiver(mWifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "CNWIFI");
		wl.acquire();

	}

	/**
	 * This class contains the WiFi thread that will be called when a signal is
	 * received from the hardware
	 */
	private class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			if (scan_activo) {

				WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> scanResult = mWifiManager.getScanResults();

				for (int i = 0; i < scanResult.size(); i++) {

					 if ((scanResult.get(i).SSID.equals("eduroam"))
					 || (scanResult.get(i).SSID.equals("UCM"))
					 || (scanResult.get(i).SSID.equals("UCM-CONGRESO"))) {

					if (!encontrados.contains(scanResult.get(i).BSSID)) {
						encontrados.add(scanResult.get(i).BSSID);
						((TextView) findViewById(R.id.Buscarresult))
								.append(scanResult.get(i).BSSID + "\n");
						data.append(scanResult.get(i).BSSID + "\n");
					}
					 }
				}
			}
		}

	}
}
