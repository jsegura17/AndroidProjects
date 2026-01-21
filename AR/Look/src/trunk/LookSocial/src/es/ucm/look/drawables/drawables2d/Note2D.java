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
package es.ucm.look.drawables.drawables2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import es.ucm.look.ar.ar2D.Drawable2D;
import es.ucm.look.ar.ar2D.drawables.Text2D;

public class Note2D implements Drawable2D {
	
	private Text2D text;
	
	private Bitmap sender;
	
	private RectF dst;
	
	int margin = 30;
	
	public Note2D( Bitmap sender, String text ){
		this.sender = sender;
		this.text = new Text2D( text );
		dst = new RectF( 0, 0, 40, 40 );
	}

	@Override
	public void draw(Canvas c) {
		text.draw(c);
		float x = text.getBounds().left - margin;
		float y = text.getBounds().top - margin;
		
		Paint p = new Paint( );
		p.setAlpha(200);
		c.save();
		c.translate(x, y);
		c.drawBitmap(sender, null, dst, p);
		c.restore();
		
	}

	@Override
	public void update(long elapsed) {
		
		
	}

	@Override
	public void drawTouchableArea(Canvas c, Paint p) {
		text.drawTouchableArea(c, p);
		float x = text.getBounds().left - margin;
		float y = text.getBounds().top - margin;
		c.save();
		c.translate(x, y);
		c.drawRect(dst, p);
		c.restore();
		
	}

}
