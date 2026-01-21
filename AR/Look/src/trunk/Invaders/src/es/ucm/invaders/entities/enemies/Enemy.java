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

import es.ucm.invaders.entities.MovableEntity;
import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.ar.math.collision.Armature;

public abstract class Enemy extends MovableEntity {

	public enum State {
		NORMAL, HURT, DESTROYING, DESTROYED;
	}
	
	private static final int MAX_TIME_HURT = 250;
	private static final int MAX_TIME_DESTROYING = 1000;

	private int health;

	private State state;

	private int timeHurt = MAX_TIME_HURT;
	private int timeDestroying = MAX_TIME_DESTROYING;

	protected Entity3D entity;

	public Enemy(String type, float x, float y, float z) {
		super(type, x, y, z);
		state = State.NORMAL;
		health = this.getMaxHealth();
	}

	public void hurt(int damage) {
		health -= damage;
		if (health <= 0) {
			state = State.DESTROYING;
			timeDestroying = MAX_TIME_DESTROYING;
		} else {
			state = State.HURT;
			timeHurt = MAX_TIME_HURT;
		}
	}

	public void update(long elapsed) {
		super.update(elapsed);
		switch (state) {
		case HURT:
			timeHurt -= elapsed;
			if (timeHurt <= 0)
				state = State.NORMAL;
			break;
		case DESTROYING:
			timeDestroying -= elapsed;
			if ( timeDestroying <= 0 )
				state = State.DESTROYED;
			break;
		}

	}

	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * @param health
	 *            the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Returns the max health for the enemy
	 * 
	 * @return the max health for the enemy
	 */
	public abstract int getMaxHealth();

	/**
	 * Returns the armature for this enemy
	 * 
	 * @return
	 */
	public abstract Armature getArmature();

}
