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
package es.ucm.imagegallery.data.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import es.ucm.imagegallery.hud.GestureHUD;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.WorldEntity;
import es.ucm.look.data.WorldEntityFactory;

public class ImageEntityFactory extends WorldEntityFactory {
	
	public static String IMAGE = "image";
	public static String INFO = "info";
	
	private GestureHUD gestureHud;
	
	private int maxImagePerColumns = 7;

	private float angle;

	private float radius;

	private float accAngle;

	private float imageInColumn;
	
	private int maxRow;
	
	private float dy = 2.5f;
	
	private float y = 0.0f;
	
	public ImageEntityFactory( GestureHUD gestureHud ){
		this.gestureHud = gestureHud;
		angle = (float) (2 * Math.PI / (float) maxImagePerColumns);
		radius = 3.0f;
		accAngle = 0.0f;
		imageInColumn = 0.0f;
		maxRow = 0;
	}
	
	@Override
	public WorldEntity createWorldEntity(EntityData data) {
		ImageEntity image = new ImageEntity( data );
		image.addTouchListener(gestureHud);
		float x = radius * (float) Math.sin(accAngle);
		float z = radius * (float) Math.cos(accAngle);
		int resource = Integer.parseInt( image.getData().getPropertyValue(IMAGE) );
		image.setImage(resource);		
		image.getData().setLocation(x, y, z);
		image.setRotation(accAngle);
		
		if (imageInColumn == maxImagePerColumns) {
			imageInColumn = 0;
			accAngle = 0;
			y += dy;
			maxRow++;
		}

		accAngle += angle;
		imageInColumn++;
		
		return image;
	}
	
	public int getMaxRow( ){
		return maxRow;
	}
	
	public float getDy( ){
		return dy;
	}

	public Bitmap getImage(int resource) {
		return BitmapFactory.decodeResource(LookARUtil.getApp().getResources(), resource);
	}
	
	

}
