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
import es.ucm.look.ar.ar3D.core.drawables.DrawablesDataBase;
import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.ObjMesh3D;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.R;
import es.ucm.look.data.WorldEntity;

public class ImageEntity extends WorldEntity {

	private static ObjMesh3D square = DrawablesDataBase.getInstance().getDrawable3D(R.raw.square);

	private Entity3D entity;

	public ImageEntity(EntityData data) {
		super(data);
		entity = new Entity3D(square);
		entity.setLighted(false);
		setImage(Integer.parseInt(data.getPropertyValue(ImageEntityFactory.IMAGE)));
		this.setDrawable3D(entity);
		this.setDrawable2D(new ImageArea());
	}
	
	public void setImage( int resoruce ){
		entity.setTexture(resoruce);
	}

	public void setTextureBind(int bind) {
		entity.setTextureBind(bind);
	}
	
	public int getTextureBind( ){
		return entity.getTextureBind();
	}

	public void setImage(String uri) {
		entity.setTexture(uri);
	}
	
	public int getImage( ){
		return Integer.parseInt(getData().getPropertyValue(ImageEntityFactory.IMAGE));
	}
	
	public void setRotation( float rad ){
		entity.getMatrix().rotate(0, (float) (rad + Math.PI), 0);
	}

	public void setImage(Bitmap bitmap) {
		entity.setTexture(bitmap);
		
	}

}
