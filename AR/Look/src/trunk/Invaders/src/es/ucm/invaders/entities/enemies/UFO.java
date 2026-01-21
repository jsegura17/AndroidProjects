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
package es.ucm.invaders.entities.enemies;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import es.ucm.invaders.R;
import es.ucm.invaders.drawable2D.HealthBar;
import es.ucm.look.ar.ar2D.drawables.Image2D;
import es.ucm.look.ar.ar3D.core.Color4;
import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.ObjMesh3D;
import es.ucm.look.ar.math.collision.Armature;
import es.ucm.look.ar.math.geom.Point3;
import es.ucm.look.ar.math.geom.Vector3;
import es.ucm.look.ar.util.LookARUtil;

public class UFO extends Enemy {

	private static final String TYPE = "UFO";

	private static final int MAX_HEALTH = 5;

	private static ObjMesh3D ovni;
	private static ArrayList<Bitmap> bitmaps;
	private Entity3D entity;
	private boolean first = true;

	public UFO(float x, float y, float z) {
		super(TYPE, x, y, z);
		entity = new Entity3D(ovni);
		entity.setTexture(R.drawable.ufotexture);
		this.setDrawable3D(entity);
		setDrawable2D(new HealthBar(this));
		Vector3 v = new Vector3(-x, -y, -z);
		v.normalize();
		setDirection(v);
		setSpeed(2);
	}

	public void update(long elapsed) {
		super.update(elapsed);
		if (Vector3.getVolatileVector(this.getLocation(), new Point3(0, 0, 0))
				.module() < 10)
			this.getDirection().set(0, 0, 0);
		
		switch (this.getState()) {
		case NORMAL:
			entity.setMaterial(new Color4(0.5f, 0.5f, 0.5f));
			break;
		case HURT:
			entity.setMaterial(new Color4(1.0f, 0.5f, 0.5f));
			break;
		case DESTROYING:
			if (first) {
				this.setDrawable3D(null);
				this.setDrawable2D(new Image2D(bitmaps, 80, false));
				first = false;
			}
			break;
		}

		entity.getMatrix()
				.rotate(0, (float) ((elapsed / 1000.0f) * Math.PI), 0);

	}

	@Override
	public int getMaxHealth() {
		return MAX_HEALTH;
	}

	@Override
	public Armature getArmature() {
		return ovni.getArmarture();
	}

	public static void initResources() {
		// Images
		bitmaps = new ArrayList<Bitmap>();
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode0));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode1));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode2));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode3));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode4));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode5));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode6));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode7));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode8));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode9));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode10));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode11));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode12));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode13));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode14));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode15));
		bitmaps.add(BitmapFactory.decodeResource(LookARUtil.getApp()
				.getResources(), R.drawable.explode16));

		ovni = new ObjMesh3D(LookARUtil.getApp(), R.raw.ufo);

	}

}
