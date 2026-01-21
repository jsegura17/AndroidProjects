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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import es.ucm.look.R;
import es.ucm.look.ar.ar2D.AR2D;
import es.ucm.look.ar.ar2D.Drawable2D;
import es.ucm.look.ar.math.geom.Point3;
import es.ucm.look.ar.util.LookARUtil;

public class EntityTracker implements Drawable2D {

	/**
	 * The point to be tracked
	 */
	private Point3 p;

	private AR2D view2D;

	private Bitmap arrow;

	private Matrix matrix;

	/**
	 * Creates an entity tracker
	 * 
	 * @param p
	 *            the point to be tracked
	 */
	public EntityTracker(Point3 p) {
		this.p = p;
		view2D = LookARUtil.getApp().get2DLayer();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = true;
		arrow = BitmapFactory.decodeResource(LookARUtil.getApp().getResources(), R.drawable.arrow, options);
		matrix = new Matrix();
	}

	@Override
	public void draw(Canvas c) {
		Paint p = new Paint();
		p.setColor(Color.RED);
		c.save();
		c.concat(matrix);
		c.drawBitmap(arrow, null, new RectF(0, 0, 40, 40), null);
		c.restore();
	}

	@Override
	public void update(long elapsed) {
		Point3 prj = new Point3(p);
		view2D.projectPoint(prj);

		float x = prj.x;
		float y = prj.y;

		if (prj.z < 0.0f) {
			x = view2D.getWidth() / 2.0f;
			y = view2D.getHeight() - 40.0f;
		} else {

			if (x < 0)
				x = 40;
			else if (x > view2D.getWidth())
				x = view2D.getWidth() - 40;

			if (y < 0)
				y = 0;
			else if (y > view2D.getWidth())
				y = view2D.getHeight() - 40;
		}

		matrix.reset();
		matrix.preTranslate(x, y);

		if (prj.z > 0.0f) {
			if (prj.x < 0) {
				matrix.preRotate(90);
			} else if (prj.x > view2D.getWidth()) {
				matrix.preRotate(270);
			}
		}

	}

	@Override
	public void drawTouchableArea(Canvas c, Paint p) {
		// Do nothing

	}

}
