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

import es.ucm.look.ar.listeners.TouchListener;
import es.ucm.look.ar.math.geom.Point3;
import es.ucm.look.data.LookData;
import es.ucm.look.data.WorldEntity;
import es.ucm.mundovirtual.data.entities.ObjectWorldEntity;

public class ObjectTouchListener implements TouchListener {

	@Override
	public boolean onTouchDown(WorldEntity e, float x, float y) {
		Point3 p = e.getLocation();
		LookData.getInstance().getDataHandler().updatePosition(e.getData(), p.x, (p.y)-1, p.z);
		((ObjectWorldEntity) e).girar();
		return true;
	}

	@Override
	public boolean onTouchUp(WorldEntity e, float x, float y) {
		return false;
	}

	@Override
	public boolean onTouchMove(WorldEntity e, float x, float y) {
		return false;
	}

}
