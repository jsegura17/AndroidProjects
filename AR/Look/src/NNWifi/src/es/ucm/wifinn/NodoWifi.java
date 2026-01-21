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

import android.net.wifi.WifiManager;

public class NodoWifi implements Comparable {
	
	public static final int MAX_DIF = 100;

	
	private String bssid;
	private int level;
	private int id;

	
	public NodoWifi(String bssid, int level, int id) {
		this.bssid = bssid;
		this.level = level;
		this.id = id;
		
	}

	public String getBssid() {
		return bssid;
	}


	public int getLevel() {
		return level;
	}
	
	public String toString() {
		return ("Nodo: " + bssid + " " + level + " " + id);
	}
	
	
	//de 1 a 100, grado de similitud en la se�al recibida
	//signal1 en formato de 1 a 35
	public int getSignalSimilarity(int signal) {
		int signal1 = java.lang.Math.abs(level);//WifiManager.calculateSignalLevel(level,35);
		int signal2 = java.lang.Math.abs(signal);//WifiManager.calculateSignalLevel(signal,35);
		
		int dif = java.lang.Math.abs(signal2-signal1);
		int similarity = MAX_DIF-dif;
		
		int result = (Math.round(((similarity/10))));

		return result;
	}

	public int getId() {
		return id;
	}
	
	public String getKey() {
		return (Integer.toString(id));
	}
	
	public static int averageLevel(ArrayList<Integer> l) {
		int sum = 0;
		for (int i =0; i < l.size(); i++) {
			sum += l.get(i);
		}
		return (sum/l.size());
	}
	
	public static int maxLevel(ArrayList<Integer> l) {
		int max = -100;
		for (int i =0; i < l.size(); i++) {
			if (l.get(i) > max) {
				max = l.get(i);
			}
		}
		return (max);
	}
	
	
	@Override
	public int compareTo(Object o) {
		int result = 0;
		NodoWifi nodo = (NodoWifi)o;
		if (nodo.level == this.level)
			result = 0;
		else if (this.level > nodo.level)
			result = 1;
		else
			result = -1;
		
		return result;
		
	}

}
