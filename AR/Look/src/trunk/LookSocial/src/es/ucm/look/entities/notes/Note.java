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
package es.ucm.look.entities.notes;

import android.graphics.Bitmap;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.WorldEntity;
import es.ucm.look.data.filesManager.LookFilesManager;
import es.ucm.look.drawables.drawables2d.Note2D;

public class Note extends WorldEntity {

	//Value of property 'type'
	public static final String TYPE_NOTE = "note";
	
	//NOTE PROPERTIES
	public static final String PROPERTY_SENDER_ID = "sender_id";
	public static final String PROPERTY_DATE = "date";
	public static final String PROPERTY_INFO = "info";//text


	public Note( EntityData entityData){
		super( entityData);
		Bitmap bitmap = LookFilesManager.getFileManager().getImageBitmap("/avatars/empty.png");
		setDrawable2D(new Note2D( bitmap, entityData.getPropertyValue(PROPERTY_INFO)));
	}

}
