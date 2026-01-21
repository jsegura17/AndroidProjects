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
package es.ucm.invaders.drawable2D;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import es.ucm.invaders.entities.enemies.Enemy;
import es.ucm.look.ar.ar2D.Drawable2D;

public class HealthBar implements Drawable2D {
	
	private Enemy enemy;
	
	public HealthBar( Enemy enemy ){
		this.enemy = enemy;
	}

	@Override
	public void draw(Canvas c) {
		float maxWidth = c.getWidth() / 16.0f;
		float barHeight = c.getHeight() / 40.0f;
		float healthWidth = enemy.getHealth() * maxWidth / enemy.getMaxHealth();
		
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Style.FILL_AND_STROKE);
		
		c.drawRect(0, 0, maxWidth, barHeight, p);
		
		int color = Color.GREEN;
		if ( enemy.getHealth() <= enemy.getMaxHealth() / 4 ){
			color = Color.RED;
		}
		else if ( enemy.getHealth() <= enemy.getMaxHealth() / 2){
			color = Color.YELLOW;
		}
		p.setColor(color);
		p.setStyle(Style.FILL);
		c.drawRect(0, 0, healthWidth, barHeight, p);
		
	}

	@Override
	public void update(long elapsed) {
		
	}

	@Override
	public void drawTouchableArea(Canvas c, Paint p) {
		
		
	}

}
