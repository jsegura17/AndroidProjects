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
package es.ucm.mundovirtual.data.entities;

import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.WorldEntity;

public class ObjectWorldEntity extends WorldEntity {
	private boolean girando;
	private int giro;

	public ObjectWorldEntity(EntityData data) {
		super(data);
		girando = false;
		giro = 0;
	}

	@Override
	public void update(long elapsed) {
		
		super.update(elapsed);
		if (girando) {
			if (giro < 1000) {
				((Entity3D) this.getDrawable3D()).getMatrix().rotate(0.0f,
						(0.15f/(elapsed/2))*100, 0.0f);
				giro++;
			} else {
				giro = 0;
				girando = false;
			}
		}
		
	}
	
	public void girar() {
		if (!girando) {
			giro=0;
			girando = true;
		}
	}

}
