/*******************************************************************************
 * Look! is a Framework of Augmented Reality for Android. 
 * 
 * Copyright (C) 2011 
 * 		Sergio BellÃ³n Alcarazo
 * 		Jorge Creixell Rojo
 * 		Ã�ngel Serrano Laguna
 * 	
 * 	   Final Year Project developed to Sistemas InformÃ¡ticos 2010/2011 - Facultad de InformÃ¡tica - Universidad Complutense de Madrid - Spain
 * 	
 * 	   Project led by: Jorge J. GÃ³mez SÃ¡nz
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
package es.ucm.imagegallery;

import java.util.HashMap;

import android.os.Bundle;
import android.view.KeyEvent;
import es.ucm.imagegallery.data.ImageGalleryWorld;
import es.ucm.imagegallery.data.entities.ImageEntityFactory;
import es.ucm.imagegallery.hud.GestureHUD;
import es.ucm.look.ar.LookAR;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;

public class ImageGallery extends LookAR {

	private static HashMap<Integer, String> data;

	private static String[] text = new String[] {
			"Analizador diferencial analÃ³gico", "Amplificador operacional",
			"Analizador diferencial electrÃ³nico",
			"Resonancia en sistemas no lineales", "Diario Madrid 1954",
			"Computador AnalÃ³gico Comercial", "IBM 7090 (Computador)",
			"Consola de datos de IBM 7090",
			"Unid. de Cinta MagnÃ©tica del IBM 360/370",
			"Perforadora de Tarjetas IBM Mod. 29", "Supercomputador vectorial",
			"Procesador y Consola de Control de IEA-FI", "AVIION",
			"Unidad de memoria de Ferritas del IEA-FI",
			"Unidad de disco 317 MB", "Motorola Exorciser II",
			"Placa del HP21MX", "ONTEL OP 1", "Monitor del HP21MX",
			"CPU del HP 150", "Teclado del HP21MX",
			"Comp. para aplicaciones en tiempo real",
			"CPU + Teclado Apple II Plus", "Rockwell AIM-65", "Sinclair ZX81",
			"Monitor Memorex 1377", "Monitor del IBM 3278-2",
			"Plotter HP9872C", "Monitor de IBM 3279 2A", "Computador HP1000",
			"Estacion de trabajo HP9000",
			"Computador paralelo de mem. compartida",
			"Trazador de curvas Plotter Calcomp 563", "Impresora matricial",
			"Digital PDP 11/23", "Monitor de Digital PDP 11/23 PLUS" };

	static {
		data = new HashMap<Integer, String>();
		int i = 0;
		data.put(R.drawable.museo1, text[i++]);
		data.put(R.drawable.museo2, text[i++]);
		data.put(R.drawable.museo3, text[i++]);
		data.put(R.drawable.museo4, text[i++]);
		data.put(R.drawable.museo5, text[i++]);
		data.put(R.drawable.museo6, text[i++]);
		data.put(R.drawable.museo7, text[i++]);
		data.put(R.drawable.museo8, text[i++]);
		data.put(R.drawable.museo9, text[i++]);
		data.put(R.drawable.museo10, text[i++]);
		data.put(R.drawable.museo11, text[i++]);
		data.put(R.drawable.museo12, text[i++]);
		data.put(R.drawable.museo13, text[i++]);
		data.put(R.drawable.museo14, text[i++]);
		data.put(R.drawable.museo15, text[i++]);
		data.put(R.drawable.museo16, text[i++]);
		data.put(R.drawable.museo17, text[i++]);
		data.put(R.drawable.museo18, text[i++]);
		data.put(R.drawable.museo19, text[i++]);
		data.put(R.drawable.museo20, text[i++]);
		data.put(R.drawable.museo21, text[i++]);
		data.put(R.drawable.museo22, text[i++]);
		data.put(R.drawable.museo23, text[i++]);
		data.put(R.drawable.museo24, text[i++]);
		data.put(R.drawable.museo25, text[i++]);
		data.put(R.drawable.museo26, text[i++]);
		data.put(R.drawable.museo27, text[i++]);
		data.put(R.drawable.museo28, text[i++]);
		data.put(R.drawable.museo29, text[i++]);
		data.put(R.drawable.museo30, text[i++]);
		data.put(R.drawable.museo31, text[i++]);
		data.put(R.drawable.museo32, text[i++]);
		data.put(R.drawable.museo33, text[i++]);
		data.put(R.drawable.museo34, text[i++]);
		data.put(R.drawable.museo35, text[i++]);
		data.put(R.drawable.museo36, text[i++]);
	}

	private GestureHUD hud;

	public ImageGallery() {
		super(true, true, true, false, 15.0f, true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hud = new GestureHUD();
		ImageEntityFactory factory = new ImageEntityFactory(hud);
		ImageGalleryWorld world = new ImageGalleryWorld(factory);
		hud.setWorld(world, factory);
		LookData.getInstance().setWorld(world);
		LookData.getInstance().setWorldEntityFactory(factory);
		this.get2DLayer().getHUD().add(hud);

		// Init data
		int i = 1;
		for (Integer resource : data.keySet()) {
			EntityData e = new EntityData(i++, "image");
			e.setPropertyValue(ImageEntityFactory.IMAGE, resource + "");
			e.setPropertyValue(ImageEntityFactory.INFO, data.get(resource));
			LookData.getInstance().getDataHandler().addEntity(e);
		}

		LookData.getInstance().updateData();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean wentBack = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			wentBack = hud.goBack();
		}
		if (!wentBack)
			return super.onKeyDown(keyCode, event);
		else
			return true;
	}

}
