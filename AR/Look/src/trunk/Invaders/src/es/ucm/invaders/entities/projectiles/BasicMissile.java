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
package es.ucm.invaders.entities.projectiles;

import es.ucm.invaders.R;
import es.ucm.invaders.entities.MovableEntity;
import es.ucm.look.ar.ar3D.core.Color4;
import es.ucm.look.ar.ar3D.core.drawables.DrawablesDataBase;
import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.ObjMesh3D;
import es.ucm.look.ar.math.geom.Vector3;

public class BasicMissile extends MovableEntity {
	
	public static final String TYPE = "NORMAL_PROJECTILE";
	
	private Entity3D entity;
	
	private static ObjMesh3D sphere;

	/**
	 * Constructs a missile with the direction
	 * @param x
	 * @param y
	 * @param z
	 */
	public BasicMissile(Vector3 v) {
		super(TYPE, v.x, v.y, v.z);
		this.setDirection(v);
		this.setSpeed(10);
		entity = new Entity3D(sphere);
		entity.setMaterial(new Color4( 1.0f, 1.0f, 0.0f ));
		entity.getMatrix().scale(0.1f, 0.1f, 0.1f);
		setDrawable3D(entity);
	}
	
	public int getDamage( ){
		return 1;
	}
	
	public static void initResources( ){
		sphere = DrawablesDataBase.getInstance().getDrawable3D(R.raw.sphere);
	}

}
