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
package es.ucm.wifinn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.neuroph.nnet.MultiLayerPerceptron;

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
import android.widget.TextView;
import android.widget.Toast;
import es.ucm.wifinn.R;
import es.ucm.wifinn.Util.DateUtils;
import es.ucm.wifinn.Util.Dec2Gray;
import es.ucm.wifinn.Util.Dec2bin;
import es.ucm.wifinn.Util.DeviceReader;
import es.ucm.wifinn.Util.DeviceWriter;
import es.ucm.wifinn.Util.VectorParser;

public class Wifi extends Activity {

	public static final String ENTRENAMIENTO = "/sdcard/Entrenamiento.txt";
	public static final String APS = "/sdcard/APs.txt";
	public static final String LOG_FILE = "/sdcard/log.txt";
	public static final int MAX_COUNT = 1;
	public static final String NEURALNETWORK = "/sdcard/MLPerceptron.nn";
	public static final double UMBRAL = 0.1;
	public static final String NODOS = "/sdcard/Nodos.txt";

	public static final boolean LOG = true;

	private StringBuffer log_data;
	private DeviceWriter out;

	private boolean scan_activo = false;
	private Button btnLocalizar;

	private Timer timer = null;
	private int count = 1;
	private HashMap<String, ArrayList<Integer>> tmpData;

	private static final int REFRESH_RATE = 1; // segundos

	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;

	private HashMap<String, ArrayList<NodoWifi>> datosEntrenamiento;
	private Set<String> aps;
	private ArrayList<String> nodos;

	MultiLayerPerceptron mlPerceptron;

	PowerManager pm;
	PowerManager.WakeLock wl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi);
		out = new DeviceWriter(LOG_FILE);

		Runnable task = new Runnable() {
			public void run() {
				mlPerceptron = (MultiLayerPerceptron) MultiLayerPerceptron
						.load(NEURALNETWORK);

			}
		};

		Thread thread = new Thread(new ThreadGroup("Posicionamiento"), task,
				"loc", 256 * 1024);
		thread.start();
		try {
			thread.join(); // se hace en nuevo thread para aumentar la pila, aun
							// asi lo esperamos para evitar errores
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		count = 1;

		btnLocalizar = (Button) findViewById(R.id.Button03);
		btnLocalizar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (scan_activo) {
					scan_activo = false;
					timer.cancel();
					btnLocalizar.setText("Localizar");

				} else {
					btnLocalizar.setText("Cancel");

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

		out.write(log_data.toString());
		if (timer != null) {
			timer.cancel();
		}

		wl.release();

	}

	@Override
	public void onResume() {
		super.onResume();
		datosEntrenamiento = new HashMap<String, ArrayList<NodoWifi>>();
		aps = Collections.synchronizedSet(new HashSet<String>());
		tmpData = new HashMap<String, ArrayList<Integer>>();
		nodos = new ArrayList<String>();

		count = 1;

		log_data = new StringBuffer();

		leeDatosEntrenamiento();
		leeAPs();
		leeNodos();

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

				if (count == 0) {
					Toast.makeText(c, "Procesando scan...", Toast.LENGTH_SHORT)
							.show();

				}

				// new Thread(new Runnable() {
				// public void run() {

				WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> scanResult = mWifiManager.getScanResults();

				if (count == 0) {

					evaluaPosicion();

					tmpData = new HashMap<String, ArrayList<Integer>>();

					for (int i = 0; i < scanResult.size(); i++) {

						ArrayList<Integer> signals = new ArrayList<Integer>();
						signals.add(scanResult.get(i).level);
						tmpData.put(scanResult.get(i).BSSID, signals);
					}

				} else {
					for (int i = 0; i < scanResult.size(); i++) {

						if (tmpData.containsKey(scanResult.get(i).BSSID)) {
							tmpData.get(scanResult.get(i).BSSID).add(
									scanResult.get(i).level);
						} else {
							ArrayList<Integer> signals = new ArrayList<Integer>();
							signals.add(scanResult.get(i).level);
							tmpData.put(scanResult.get(i).BSSID, signals);
						}

					}
				}

				count = (count + 1) % (MAX_COUNT + 1);

			}
		}

		// }).start();

		// }
	}

	private void leeDatosEntrenamiento() {
		DeviceReader in = new DeviceReader(ENTRENAMIENTO);

		String ap = null;
		String bssid, clave;
		int id;
		int level;

		do {
			ap = in.readln();
			if (ap != null) {

				String[] data = ap.split(" ");
				bssid = data[0];
				level = Integer.parseInt(data[1]);
				id = Integer.parseInt(data[2]);

				NodoWifi nodo = new NodoWifi(bssid, level, id);

				clave = Integer.toString(id);

				if (datosEntrenamiento.containsKey(clave)) {
					datosEntrenamiento.get(clave).add(nodo);
				} else {
					ArrayList<NodoWifi> l = new ArrayList<NodoWifi>();
					l.add(nodo);
					datosEntrenamiento.put(clave, l);
				}

				System.out.println(nodo.toString());

			}

		} while (ap != null);
		in.close();
	}

	private void leeAPs() {

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

	private synchronized void evaluaPosicion() {

		StringBuffer nnInput = new StringBuffer();
		String salida;
		int level;

		int cont = 0;

		Iterator<String> it = aps.iterator();
		while (it.hasNext()) {
			String key = it.next();

			if (!tmpData.containsKey(key)) {
				level = -100;
			} else {
				level = NodoWifi.averageLevel(tmpData.get(key));
			}

			String cod = "";
			try {
				cod = Dec2bin.getDec2BinString(Dec2Gray.dec2Gray(Math
						.abs(level)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			nnInput.append(cod);
			if (cont != (aps.size() - 1)) {
				nnInput.append(" ");
			}

			cont++;
		}

		mlPerceptron
				.setInput(VectorParser.parseDoubleArray(nnInput.toString()));
		mlPerceptron.calculate();
		Vector<Double> networkOutput = mlPerceptron.getOutput();
		log("NN Input: " + nnInput + "\n");
		log("NN Output: " + networkOutput + "\n");

		double maxlikelihood = 0;
		String maxlikelihoodElement = "";
		for (int i = 0; i < networkOutput.size(); i++) {
			if (networkOutput.get(i) > maxlikelihood) {
				maxlikelihood = networkOutput.get(i);
				maxlikelihoodElement = nodos.get(i);
			}
		}

		String id = "-";

		if (maxlikelihood > UMBRAL) {
			id = maxlikelihoodElement;

			log("Nodo seleccionado: " + id + "\n");
			log("Certeza: " + maxlikelihood + "%\n");
		} else {
			log("Ningun nodo sobre pasa el umbral minimo de certeza, " + UMBRAL
					+ "\n");
		}

		final int idFinal = Integer.parseInt(id);

		runOnUiThread(new Runnable() {
			public void run() {
				((TextView) findViewById(R.id.nodo)).setText(String
						.valueOf(idFinal));

				Lugar lugar = Lugares.getInstance().getLugar(idFinal);

				if (lugar != null) {

					((TextView) findViewById(R.id.posicx)).setText(String
							.valueOf(lugar.getX()));
					((TextView) findViewById(R.id.posicy)).setText(String
							.valueOf(lugar.getY()));
					((TextView) findViewById(R.id.planta)).setText(String
							.valueOf(lugar.getPlanta()));
					((TextView) findViewById(R.id.nombre)).setText(Lugares
							.getInstance().getLugar(idFinal).getNombre());
				}
			}
		});

		log("\n");

	}

	private void log(String s) {
		log_data.append(s);
		System.out.println("WiFi log: " + s);

	}

	private void leeNodos() {
		nodos = new ArrayList<String>();

		DeviceReader in = new DeviceReader(NODOS);

		String nodo = null;

		do {
			nodo = in.readln();
			if (nodo != null) {

				nodos.add(nodo);
			}

		} while (nodo != null);
		in.close();

	}

}
