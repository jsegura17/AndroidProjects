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
package es.ucm.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.ucm.wifi.Util.DeviceReader;

public class Lugares {
	private static Lugares INSTANCE = new Lugares();

	public static final String FICHERO_LUGARES = "/sdcard/Lugares.txt";

	private HashMap<String, Lugar> lugares;

	private Lugares() {
		lugares = new HashMap<String, Lugar>();
		DeviceReader in = new DeviceReader(FICHERO_LUGARES);

		String linea = null;

		do {
			linea = in.readln();
			if (linea != null) {

				String[] l = linea.split(" ");
				String key = l[0];
				lugares.put(key, new Lugar(Integer.parseInt(l[1]), Integer.parseInt(l[2]), Integer.parseInt(l[3]), l[4]));
			}

		} while (linea != null);
		in.close();
	}

	public static Lugares getInstance() {
		return INSTANCE;
	}

	public Lugar getLugar(int nodo) {
		Lugar result = null;
		String key = Integer.toString(nodo);
		if (lugares.containsKey(key)) {
			result = lugares.get(key);
		} 
		return result;

	}

}
