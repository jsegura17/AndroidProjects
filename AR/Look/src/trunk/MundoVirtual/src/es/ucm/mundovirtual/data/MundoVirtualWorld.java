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
package es.ucm.mundovirtual.data;

import android.widget.Button;
import es.ucm.look.ar.util.DeviceOrientation;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.LookData;
import es.ucm.look.data.World;

public class MundoVirtualWorld extends World {

	private static final float DISTANCE = 1.0f;

	private boolean keepUp = false;
	private Button up;

	public MundoVirtualWorld(Button up) {
		keepUp = false;
		this.up = up;

	}

	public void update(long elapsed) {
		super.update(elapsed);
		if (up.isPressed()) {
			up();
		}
	}

	public void up() {
		if (!keepUp) {
			float azimuth = DeviceOrientation.getDeviceOrientation(
					LookARUtil.getApp()).getAzimuth();
			float x = DISTANCE * (float) Math.sin(azimuth);
			float z = DISTANCE * (float) Math.cos(azimuth);
			synchronized (LookData.getInstance().locationOffset) {
				LookData.getInstance().locationOffset.x += x;
				LookData.getInstance().locationOffset.z += z;
			}
		}
	}

	public void keepUp() {
		keepUp = true;
	}

	public void stopMoving() {
		keepUp = false;
	}

}
