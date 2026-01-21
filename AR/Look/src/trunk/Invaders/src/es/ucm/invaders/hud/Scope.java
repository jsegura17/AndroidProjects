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
package es.ucm.invaders.hud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import es.ucm.invaders.InvadersWorld;
import es.ucm.invaders.R;
import es.ucm.look.ar.ar2D.HUDElement;
import es.ucm.look.ar.util.LookARUtil;

public class Scope implements HUDElement {
	
	private InvadersWorld world;
	private Bitmap touch;
	
	public Scope( InvadersWorld world ){
		this.world = world;
		touch = BitmapFactory.decodeResource(LookARUtil.getApp().getResources(), R.drawable.touch );
	}

	@Override
	public void draw(Canvas c) {
		int width =  c.getWidth();
		int height = c.getHeight();
		
		Paint p = new Paint( );
		
		
		p.setColor(Color.BLACK);
		p.setStyle(Style.STROKE);
		c.drawCircle( width / 2, height / 2, 9, p);
		c.drawCircle( width / 2, height / 2, 11, p);
		p.setColor(Color.WHITE);
		p.setStyle(Style.STROKE);
		c.drawCircle( width / 2, height / 2, 10, p);
		p.setAlpha(125);
		c.drawBitmap(touch, 0, height - touch.getHeight(), p);
		
		
		p.setStyle(Style.FILL_AND_STROKE);
		p.setColor(Color.WHITE);
		c.drawText("Kills: " + world.kills, 20, 20, p);
		
	}

	@Override
	public void update(long elapsed) {
		
		
	}

	@Override
	public void drawTouchableArea(Canvas c, Paint p) {
		
		
	}

	@Override
	public boolean contains(float x, float y) {
		return true;
	}

	@Override
	public boolean touch(MotionEvent motionEvent) {
		if ( motionEvent.getAction() == MotionEvent.ACTION_DOWN )
			world.shotMissile();
		return true;
	}

}
