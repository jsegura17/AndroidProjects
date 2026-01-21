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
package es.ucm.wifinn.Util;

public class Dec2bin {
	
	
	//devuelve una codificacion en binario separada por espacios de tam 7
	public static String getDec2BinString(int n) throws Exception {
		
		if ((n<0) || (n>128)) {
			throw new Exception("dec2Bin out of range");
		}
		
		String cadena= Integer.toBinaryString(n); 
		
		int[] elems = new int[7];
		int i=0;
		for(i=0; i < (7-cadena.length()); i++) {
			elems[i] = 0;
		}
		for (int j=i; j < 7; j++) {
			elems[j] = Integer.parseInt(Character.toString(cadena.charAt(j-i)));
		}
		
		StringBuffer cadenaFinal = new StringBuffer();
		
		for (int k = 0; k < 7; k++) {
			cadenaFinal.append(elems[k]);
			if (k != 6) {
				cadenaFinal.append(" ");
			}
		}
		
		return cadenaFinal.toString();
	}

}
