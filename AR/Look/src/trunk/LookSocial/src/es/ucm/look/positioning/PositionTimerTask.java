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
package es.ucm.look.positioning;

import java.util.TimerTask;

public class PositionTimerTask extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	/*private LocationProvider locationProvider;
	private Camera3D camera;
	private float factor;
	private Display display;

	public PositionTimerTask(Display d, Context c, Camera3D camera) {
		locationProvider = new LocationProvider(c);
		display = d;
		this.camera = camera;
		factor = 4.0f;
	}

	@Override
	public void run() {
		locationProvider.run();
		float[] f = LocationProvider.getPosition();
		if (f != null) {
			String s = "[";
			for (int i = 0; i < f.length; i++) {
				s += f[i] + ", ";
			}
			s += "]";

			Log.d("position", s);
		}

		synchronized (camera.eye) {
			int rotation = display.getRotation();
			switch ( rotation ){
			case Surface.ROTATION_0:
				camera.eye.set(f[0]*factor, 0.0f, -f[1]*factor);
				break;
			case Surface.ROTATION_90:
				camera.eye.set(f[1]*factor, 0.0f, f[0]*factor);
				break;
			case Surface.ROTATION_270:
				camera.eye.set(-f[1]*factor, 0.0f, -f[0]*factor);
				break;
				
			}			
		}

	}*/
}
