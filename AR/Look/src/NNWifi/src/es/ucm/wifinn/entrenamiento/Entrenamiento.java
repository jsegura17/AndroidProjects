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
package es.ucm.wifinn.entrenamiento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import es.ucm.wifinn.R;
import es.ucm.wifinn.NodoWifi;
import es.ucm.wifinn.Wifi;
import es.ucm.wifinn.Util.DeviceReader;
import es.ucm.wifinn.Util.DeviceWriter;
import es.ucm.wifinn.buscaAps.BuscaAPS;
import es.ucm.wifinn.rneuronal.EntrenarRed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class Entrenamiento extends Activity implements OnClickListener {

	public static final String ARCHIVO = "/sdcard/Entrenamiento.txt";
	public static final int MENU_BUSCAR_APS = Menu.FIRST + 1;
	public static final int MENU_ENTRENAR_RED = Menu.FIRST + 2;
	public static final int MENU_LOCALIZAR = Menu.FIRST + 3;
	// private static final int TIEMPO_ENTRENAMIENTO = 15; // segundos
	public static final int MAX_COUNT = 1;
	public static final int NUM_SCANS = 5;
	private int count = 1;
	private int scan_count = 1;
	private Timer timer = null;
	private static final int REFRESH_RATE = 1; // segundos

	public static final String APS = "/sdcard/APs.txt";
	public static final String NNDATA = "/sdcard/nndata.txt"; // neural network
																// training data

	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;

	private HashMap<String, ArrayList<Integer>> scanNodoResult;
	private long timezero = 0;

	private DeviceWriter out;

	private Button btnEntrenar;
	private Button btnGuardar;

	private EditText editNodo;

	private TextView result;
	private ProgressDialog progress;

	private boolean entrenando = false;

	private StringBuffer data;

	private Set<String> aps = null;

	private int id;
	
	PowerManager pm;
	PowerManager.WakeLock wl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		count = 1;
		scan_count=1;

		data = new StringBuffer();

		((Button) findViewById(R.id.Button01)).setText("Entrenar");
		((Button) findViewById(R.id.Button02)).setText("Guardar");

		((EditText) findViewById(R.id.EditID)).setText("Id");

		btnEntrenar = (Button) findViewById(R.id.Button01);
		btnEntrenar.setOnClickListener(this);
		btnGuardar = (Button) findViewById(R.id.Button02);
		btnGuardar.setOnClickListener(this);
		editNodo = ((EditText) findViewById(R.id.EditID));

		result = ((TextView) findViewById(R.id.TextView01));
		result.setText("");

		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		mWifiReceiver = new WifiReceiver();
		mWifiManager.setWifiEnabled(true);
		mWifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY,
				"Scan Only");

		registerReceiver(mWifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

		scanNodoResult = new HashMap<String, ArrayList<Integer>>();

	}

	@Override
	public void onPause() {
		super.onPause();
		
		wl.release();

	}

	@Override
	public void onResume() {
		super.onResume();
		leeAPs();
		count = 1;
		scan_count =1;
		
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
			if (entrenando) {
				WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

				System.out.println("wifi: evento recibido");
				List<ScanResult> lista = mWifiManager.getScanResults();
				for (int i = 0; i < lista.size(); i++) {

					if (scanNodoResult.containsKey(lista.get(i).BSSID)) {
						scanNodoResult.get(lista.get(i).BSSID).add(
								lista.get(i).level);
					} else {
						ArrayList<Integer> l = new ArrayList<Integer>();
						l.add(lista.get(i).level);
						scanNodoResult.put(lista.get(i).BSSID, l);
					}
				}

				if (count != 0) {
					mWifiManager.startScan();

				} else {
					Set<String> keys = scanNodoResult.keySet();
					Iterator<String> it = keys.iterator();

					HashMap<String, NodoWifi> nodos = new HashMap<String, NodoWifi>();

					while (it.hasNext()) {
						int maxLevel = -100;
						int total = 0;
						String index = it.next();

						for (int i = 0; i < scanNodoResult.get(index).size(); i++) {
							total += scanNodoResult.get(index).get(i);
							if (scanNodoResult.get(index).get(i) > maxLevel) {
								maxLevel = scanNodoResult.get(index).get(i);
							}
						}

						// int level = maxLevel;
						int level = (total / scanNodoResult.get(index).size());

						nodos.put(index, new NodoWifi(index, level, id));
					}

					Iterator<String> it2 = aps.iterator();
					while (it2.hasNext()) {
						String bssid = it2.next();
						if (nodos.containsKey(bssid)) {
							NodoWifi nodo = nodos.get(bssid);
							System.out.println("wifi: " + nodo.getBssid() + " "
									+ nodo.getLevel());

							data.append(nodo.getBssid() + " " + nodo.getLevel()
									+ " " + nodo.getId() + "\n");

							result.append(nodo.getBssid() + " "
									+ nodo.getLevel() + " " + nodo.getId()
									+ "\n");
						} else {
							System.out.println("wifi: " + bssid + " " + -100);

							data.append(bssid + " " + -100 + " " + id + "\n");

							result.append(bssid + " " + -100 + " " + id + "\n");
						}
					}
					
					scan_count = (scan_count + 1) % (NUM_SCANS + 1);
					
					if (scan_count == 0) {
						btnEntrenar.setVisibility(View.VISIBLE);
						btnGuardar.setVisibility(View.VISIBLE);

						progress.dismiss();

						entrenando = false;
					}
					else {
						count = 1;
						mWifiManager.startScan();
					}


				}
				count = (count + 1) % (MAX_COUNT + 1);

			}

		}

	}

	@Override
	public void onClick(View v) {
		if (v == btnEntrenar) {
			if (!entrenando) {
				if (editNodo.getText().length() == 0) {
					Toast.makeText(this, "Introduce valores correctos",
							Toast.LENGTH_SHORT).show();
				} else {
					try {
						id = Integer.parseInt(editNodo.getText().toString());

					} catch (NumberFormatException e) {
						Toast.makeText(this, "Introduce valores validos",
								Toast.LENGTH_SHORT).show();
						return;
					}

					/*
					 * Toast.makeText(this, "Iniciando...", Toast.LENGTH_SHORT)
					 * .show();
					 */
					timezero = System.currentTimeMillis();
					System.out.println("wifi: Lanzando entrenamiento");

					entrenando = true;

					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(editNodo.getWindowToken(), 0);

					btnEntrenar.setVisibility(View.GONE);
					btnGuardar.setVisibility(View.GONE);

					progress = ProgressDialog.show(this, "Entrenando",
							"Un momento...", true, true);

					timer = new Timer();
					timer.schedule(new TimerTask() {
						public void run() {
							mWifiManager.startScan();
						}

					}, 0, REFRESH_RATE * 1000);
				}
			}
		}

		else if (v == btnGuardar) {
			out = new DeviceWriter(ARCHIVO);
			out.write(data.toString());
			out.close();

			Toast.makeText(this, "Guardado con exito", Toast.LENGTH_SHORT)
					.show();

		}

	}

	private void leeAPs() {
		aps = Collections.synchronizedSet(new HashSet<String>());

		DeviceReader in = new DeviceReader(APS);

		String bssid = null;

		do {
			bssid = in.readln();
			if (bssid != null) {

				aps.add(bssid);
			}

		} while (bssid != null);
		in.close();

		Iterator<String> it = aps.iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println("prueba: " + key);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.setQwertyMode(true);
		menu.add(0, MENU_BUSCAR_APS, 0, "Buscar APs");
		menu.add(0, MENU_ENTRENAR_RED, 0, "Entrenar Red");
		menu.add(0, MENU_LOCALIZAR, 1, "Localizar");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * This function will be called when a menu button option is selected. If
	 * the option is "My Location", the program start the MapActivity and
	 * initialize the sensors and WiFi scanning.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_LOCALIZAR:
			startActivity(new Intent(this, Wifi.class));
			break;

		case MENU_ENTRENAR_RED:
			startActivity(new Intent(this, EntrenarRed.class));
			break;

		case MENU_BUSCAR_APS:
			startActivity(new Intent(this, BuscaAPS.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
