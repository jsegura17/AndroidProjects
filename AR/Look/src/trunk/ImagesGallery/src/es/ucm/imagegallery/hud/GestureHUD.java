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
package es.ucm.imagegallery.hud;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import es.ucm.imagegallery.R;
import es.ucm.imagegallery.data.ImageGalleryWorld;
import es.ucm.imagegallery.data.entities.ImageEntity;
import es.ucm.imagegallery.data.entities.ImageEntityFactory;
import es.ucm.look.ar.ar2D.HUDElement;
import es.ucm.look.ar.listeners.TouchListener;
import es.ucm.look.data.WorldEntity;

public class GestureHUD implements HUDElement, TouchListener {
	
	private static final int START_OFFSET = -1000;

	private int height;

	private int width;

	private float downY;

	private float downX;

	private ImageGalleryWorld world;

	private Bitmap selectedImage;
	
	private String text;

	private ImageEntityFactory factory;

	private boolean moving = false;

	private boolean imageJustGone = false;

	private RectF dst;
	
	private ImageEntity currentImage;

	private int oldBind;
	
	private float offsetX;
	
	public GestureHUD( ){
		
	}

	@Override
	public void draw(Canvas c) {
		height = c.getHeight();
		width = c.getWidth();
		if (selectedImage != null) {
			c.save();
			c.translate(offsetX, 0);
			Paint p = new Paint( );
			
			p.setColor(Color.WHITE);
			p.setAlpha(125);
			c.save();
			c.scale(1.2f, 1.2f, width / 2, height / 2 );
			RectF rect = new RectF( 0, dst.top, dst.right, dst.bottom );
			c.drawRoundRect(rect, 10, 10, p);
			p.setAlpha(255);
			c.restore();
			c.drawBitmap(selectedImage, null, dst, null);
			
			p.setColor(Color.BLUE);
			c.drawRect(0, dst.top - 30, width * 0.99f, dst.top + 5, p );
			p.setColor(Color.WHITE);
			c.drawText(text, 10, dst.top - 10, p);
			c.restore();
			
		}
	}

	@Override
	public void update(long elapsed) {
		if ( offsetX < 0 ){
			offsetX += (float) elapsed;
			Log.i( "galeria", "offset " + offsetX );
		}
		
		if ( offsetX > 0 )
			offsetX = 0;
		
		
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
		switch (motionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (selectedImage != null) {
				selectedImage.recycle();
				selectedImage = null;
				currentImage.setTextureBind(oldBind);
				currentImage = null;
				imageJustGone = true;
				
			} else {
				downY = motionEvent.getY();
				downX = motionEvent.getX();
			}
			world.moveHorizontal(0, 0);
			return true;
		case MotionEvent.ACTION_MOVE:
			if (!imageJustGone) {
				float dy = downY - motionEvent.getY();
				float dx = downX - motionEvent.getX();
				if (Math.abs(dy) > Math.abs(dx)) {
					if (Math.abs(dy) > height / 6) {
						moving = true;
						if (dy > 0) {
							world.moveVertical(1);
						} else {
							world.moveVertical(-1);
						}
					}
				} else {
					if (Math.abs(dx) > width / 6) {
						moving = true;
						if (dx > 0) {
							world.moveHorizontal(1, dx / (float) width);
						} else
							world.moveHorizontal(-1, dx / (float) width);
					}
				}
			}

			return true;
		case MotionEvent.ACTION_UP:
			Log.i("Gallery", "Image just gone " + imageJustGone + " Moving: "
					+ moving);
			if (moving) {
				moving = false;
				return true;
			}
			if (imageJustGone) {
				imageJustGone = false;
				return true;
			}

			return false;
		}

		return false;
	}

	@Override
	public boolean onTouchDown(WorldEntity e, float x, float y) {
		return false;
	}

	@Override
	public boolean onTouchUp(WorldEntity e, float x, float y) {
		currentImage = (ImageEntity) e;
		prepareImage(factory.getImage(currentImage.getImage()));
		oldBind = currentImage.getTextureBind();
		currentImage.setImage(R.drawable.glow);
		text = currentImage.getData().getPropertyValue(ImageEntityFactory.INFO);
		return true;
	}

	private void prepareImage(Bitmap image) {
		this.selectedImage = image;
		float scaleX = (float) width / (float) image.getWidth();
		float scaleY = (float) height / (float) image.getHeight();
		float scale = scaleX < scaleY ? scaleX : scaleY;
		float margin = 0.8f; // 10 % of margin
		float newWidth = (image.getWidth() * scale) * margin;
		float newHeight = (image.getHeight() * scale) * margin;
		float x = (width - newWidth) / 2;
		float y = (height - newHeight) / 2;
		dst = new RectF(x, y, newWidth + x, newHeight + y);
		offsetX = START_OFFSET;
	}

	@Override
	public boolean onTouchMove(WorldEntity e, float x, float y) {
		return false;
	}

	public void setWorld(ImageGalleryWorld world, ImageEntityFactory factory) {
		this.world = world;
		this.factory = factory;
	}
	
	public boolean goBack() {
		boolean wentBack = selectedImage != null;
		selectedImage = null;
		if ( currentImage != null )
		currentImage.setTextureBind(oldBind);
		currentImage = null;
		imageJustGone = true;
		return wentBack;
	}

}
