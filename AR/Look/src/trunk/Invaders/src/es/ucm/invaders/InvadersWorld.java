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
package es.ucm.invaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Vibrator;
import es.ucm.invaders.entities.InvadersEntity;
import es.ucm.invaders.entities.MovableEntity;
import es.ucm.invaders.entities.enemies.Enemy;
import es.ucm.invaders.entities.enemies.Enemy.State;
import es.ucm.invaders.entities.enemies.UFO;
import es.ucm.invaders.entities.projectiles.BasicMissile;
import es.ucm.look.ar.ar3D.core.Color4;
import es.ucm.look.ar.ar3D.core.camera.Camera3D;
import es.ucm.look.ar.ar3D.core.drawables.DrawablesDataBase;
import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.ObjMesh3D;
import es.ucm.look.ar.math.geom.Point3;
import es.ucm.look.ar.math.geom.Vector3;
import es.ucm.look.ar.util.DeviceOrientation;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.World;
import es.ucm.look.data.WorldEntity;

public class InvadersWorld extends World {

	public static Integer lockP = 1;
	public static Integer lockE = 1;
	
	private static final int TIME_BETWEEN_ENEMIES = 5000;
	
	private int timeToEnemy;

	private List<Enemy> enemies;
	private List<BasicMissile> projectiles;
	private List<MovableEntity> auxListE;
	private List<MovableEntity> auxListP;
	private Point3 heroePosition;
	private int maxDistance;
	private Vibrator vibrator;
	
	public int kills = 0;

	public InvadersWorld() {

		timeToEnemy = 0;
		auxListE = new ArrayList<MovableEntity>();
		auxListP = new ArrayList<MovableEntity>();
		enemies = new ArrayList<Enemy>();
		projectiles = new ArrayList<BasicMissile>();
		heroePosition = new Point3(0, 0, 0);
		maxDistance = 100;
		LookARUtil.getApp();
		vibrator = (Vibrator) LookARUtil.getApp().getSystemService(Context.VIBRATOR_SERVICE);

		ObjMesh3D skyBackground = DrawablesDataBase.getInstance()
				.getDrawable3D(R.raw.sphere);
		Entity3D e = new Entity3D(skyBackground);
		e.setMaterial(new Color4(1.0f, 1.0f, 1.0f));
		e.setTexture(R.drawable.skyblue);
		e.getMatrix().scale(50, 50, 50);
		e.setLighted(false);

		WorldEntity sky = new InvadersEntity("none", 0, 0, 0);
		sky.setDrawable3D(e);

		this.addEntity(sky);
	}

	public void update(long elapsed) {
		super.update(elapsed);
		auxListE.clear();
		auxListP.clear();
		synchronized (lockE) {
			for (Enemy enemy : enemies) {
				auxListP.clear();
				if (enemy.getState() == State.DESTROYED) {
					auxListE.add(enemy);
				} else {
					synchronized (lockP) {
						for (BasicMissile missile : projectiles) {
							Point3 p = new Point3(missile.getLocation());
							p.subtract(enemy.getLocation());
							if (enemy.getArmature().contains(p)) {
								enemy.hurt(missile.getDamage());
								if ( enemy.getState() == Enemy.State.DESTROYING )
									vibrator.vibrate(300);
								auxListP.add(missile);
							} else if (Vector3.getVolatileVector(
									missile.getLocation(), heroePosition)
									.module2() >= maxDistance * maxDistance) {
								auxListP.add(missile);
							}
						}

						for (MovableEntity m : auxListP) {
							projectiles.remove(m);
							removeEntity(m.getId());
						}
					}
				}
			}
			for (MovableEntity m : auxListE) {
				enemies.remove(m);
				removeEntity(m.getId());
				kills++;
			}
		}
		
		timeToEnemy -= elapsed;
		if (timeToEnemy <= 0 ){
			timeToEnemy = TIME_BETWEEN_ENEMIES;
			addEnemy();
		}

	}

	private void addEnemy() {
		Vector3 v = new Vector3(0, 0, 1 );
		float angle = (float) (Math.random() * 2 * Math.PI);
		v.rotateY(angle);
		
		float y = (float) (5.0f - Math.random() * 10.0f);
		UFO u = new UFO( v.x * 50.0f, y, v.z * 50.0f);
		enemies.add(u);
		this.addEntity(u);
		
	}

	public void shotMissile() {
		vibrator.vibrate(50);
		Vector3 v = new Vector3(Camera3D.NORTH);
		v.rotateX(-DeviceOrientation.getDeviceOrientation(LookARUtil.getApp()).getPitch());
		v.rotateY(DeviceOrientation.getDeviceOrientation(LookARUtil.getApp()).getAzimuth());
		BasicMissile missile = new BasicMissile(v);
		this.addEntity(missile);
		synchronized (lockP) {
			projectiles.add(missile);
		}
	}
}
