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
package es.ucm.wifinn.rneuronal;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.core.transfer.TransferFunction;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.NeuralNetworkFactory;
import org.neuroph.nnet.learning.LMS;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import es.ucm.wifinn.R;
import es.ucm.wifinn.NodoWifi;
import es.ucm.wifinn.Util.Dec2Gray;
import es.ucm.wifinn.Util.Dec2bin;
import es.ucm.wifinn.Util.DeviceReader;
import es.ucm.wifinn.Util.DeviceWriter;

public class EntrenarRed extends Activity {

	public static final String ENTRENAMIENTO = "/sdcard/Entrenamiento.txt";
	public static final String APS = "/sdcard/APs.txt";
	public static final String NEURALNETWORK = "/sdcard/MLPerceptron.nn";
	public static final String NODOS = "/sdcard/Nodos.txt";
	public static final String POSTPROCESSED = "/sdcard/PostProcess.txt";

	// private HashMap<String, ArrayList<NodoWifi>> datosEntrenamiento;
	private ArrayList<String> nodos;
	private ArrayList<NodoEntrenamiento> conjuntoEntrenamiento;

	private int numAps;
	private int numNodos;

	Button btnEntrenar;
	ProgressBar progress;
	Context context;
	
	PowerManager pm;
	PowerManager.WakeLock wl;

	private DeviceWriter out;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trainnet);

		numAps = getNumAps();
		try {
			preProcess();
		} catch (Exception e) {
			e.printStackTrace();
		}

		btnEntrenar = (Button) findViewById(R.id.ButtonTrain);
		btnEntrenar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				btnEntrenar.setEnabled(false);
				progress.setVisibility(View.VISIBLE);

				Runnable task = new Runnable() {
					public void run() {
						trainNetwork();

					}
				};

				Thread thread = new Thread(new ThreadGroup("Entrenamiento"),
						task, "train", 256 * 1024);
				thread.start();
			}

		});

		progress = (ProgressBar) findViewById(R.id.ProgressTrain);
		progress.setVisibility(View.INVISIBLE);

		context = this;
	}

	private void preProcess() throws Exception {
		procesaNodos();

		conjuntoEntrenamiento = new ArrayList<NodoEntrenamiento>();
		DeviceReader in = new DeviceReader(ENTRENAMIENTO);

		String[] ap = new String[numAps];
		StringBuffer nnInput;
		StringBuffer nnOutput;

		String bssid, clave;
		int level;

		ap[0] = in.readln();
		do {
			nnInput = new StringBuffer();
			nnOutput = new StringBuffer();

			if (ap != null) {

				String[] cadenaTmp = ap[0].split(" ");
				int id = Integer.parseInt(cadenaTmp[2]);

				for (int i = 1; i < numAps; i++) { // rest of aps of the
													// training set
					ap[i] = in.readln();
					if (ap[i] == null)
						throw new Exception("Malformed training data");
				}

				for (int i = 0; i < numAps; i++) {
					String[] tmp = ap[i].split(" ");
					level = Integer.parseInt(tmp[1]);
					String cod = Dec2bin.getDec2BinString(Dec2Gray.dec2Gray(Math.abs(level)));
					nnInput.append(cod);
					if (i != (numAps - 1)) {
						nnInput.append(" ");
					}
				}

				int ordenNodo = nodos.indexOf(Integer.toString(id));
				if (ordenNodo == -1) {
					throw new Exception("Node not found");
				}

				for (int i = 0; i < numNodos; i++) {
					if (i == ordenNodo) {
						nnOutput.append("1");
					} else {
						nnOutput.append("0");
					}
					if (i != (numNodos - 1)) {
						nnOutput.append(" ");
					}
				}

				System.out.println("NEURAL: " + nnInput);
				conjuntoEntrenamiento.add(new NodoEntrenamiento(nnInput
						.toString(), nnOutput.toString()));

				ap[0] = in.readln();

			}

		} while (ap[0] != null);
		in.close();

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < conjuntoEntrenamiento.size(); i++) {
			buffer.append(conjuntoEntrenamiento.get(i).getEntrada() + " "
					+ conjuntoEntrenamiento.get(i).getSalida() + "\n");
		}
		DeviceWriter out;
		out = new DeviceWriter(POSTPROCESSED);
		out.write(buffer.toString());
		out.close();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onPause() {
		super.onPause();
		
		wl.release();

	}

	@Override
	public void onResume() {
		super.onResume();
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "CNWIFI");
		wl.acquire();

	}

	private void procesaNodos() {
		nodos = new ArrayList<String>();
		int nNodos = 0;

		DeviceReader in = new DeviceReader(ENTRENAMIENTO);

		String ap = null;
		String bssid, clave;
		int id;
		int level;

		do {
			ap = in.readln();
			if (ap != null) {

				String[] data = ap.split(" ");
				id = Integer.parseInt(data[2]);

				String codNodo = Integer.toString(id);
				if (!nodos.contains(codNodo)) {
					nodos.add(codNodo);
					nNodos++;
				}
			}

		} while (ap != null);
		in.close();

		this.numNodos = nNodos;

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < nodos.size(); i++) {
			buffer.append(nodos.get(i) + "\n");
		}

		DeviceWriter out;
		out = new DeviceWriter(NODOS);
		out.write(buffer.toString());
		out.close();

	}

	private int getNumAps() {

		DeviceReader in = new DeviceReader(APS);
		int numAps = 0;

		String bssid = null;

		do {
			bssid = in.readln();
			if (bssid != null) {

				numAps++;
			}

		} while (bssid != null);
		in.close();

		return numAps;

	}

	private void trainNetwork() {

		TrainingSet trainingSet = new TrainingSet();

		int entradas = numAps * 7;
		int salidas = numNodos;
		int hiddenLayer = 50;

		for (int i = 0; i < conjuntoEntrenamiento.size(); i++) {
			trainingSet.addElement(new SupervisedTrainingElement(
					conjuntoEntrenamiento.get(i).getEntrada(),
					conjuntoEntrenamiento.get(i).getSalida()));
		}

		MultiLayerPerceptron mlPerceptron = NeuralNetworkFactory
				.createMLPerceptron(new String(entradas + " " + hiddenLayer
						+ " " + salidas), TransferFunctionType.SIGMOID,
						MomentumBackpropagation.class, true, false);

		LMS lms = (LMS) mlPerceptron.getLearningRule();
		lms.setLearningRate(0.5);
		lms.setMaxError(0.01);
		// lms.setMaxIterations(maxIterations);

		mlPerceptron.learnInSameThread(trainingSet);

		mlPerceptron.save(NEURALNETWORK);

		System.out.println("NN Testing trained neural network");
		calculate(mlPerceptron, trainingSet);

		runOnUiThread(new Runnable() {
			public void run() {
				btnEntrenar.setEnabled(true);
				progress.setVisibility(View.INVISIBLE);

				Toast.makeText(context, "Entrenamiento Completado",
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	public static void calculate(NeuralNetwork nnet, TrainingSet tset) {

		for (TrainingElement trainingElement : tset.trainingElements()) {

			nnet.setInput(trainingElement.getInput());
			nnet.calculate();
			Vector<Double> networkOutput = nnet.getOutput();
			System.out.print("NN Input: " + trainingElement.getInput());
			System.out.println("NN Output: " + networkOutput);

		}

	}

}
