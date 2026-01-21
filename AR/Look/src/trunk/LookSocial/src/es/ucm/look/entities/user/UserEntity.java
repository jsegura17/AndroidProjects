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
package es.ucm.look.entities.user;

import java.util.Map;
import java.util.Properties;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import es.ucm.look.R;
import es.ucm.look.ar.ar2D.drawables.Text2D;
import es.ucm.look.ar.listeners.TouchListener;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.WorldEntity;

/**
 * Represents an avatar
 * 
 * @author Ángel Serrano Laguna
 * 
 */
public class UserEntity extends WorldEntity {

	/** 
	 * Value of property 'type'
	 */
	public static final String TYPE_USER = "user";	
	
	//USER PROPERTIES
	
	/**
	 * User name
	 */
	public static final String PROPERTY_USER = "user";
	
	/**
	 * User e-mail
	 */
	public static final String PROPERTY_EMAIL = "email";
	
	/**
	 * Real name for the user
	 */
	public static final String PROPERTY_NAME = "name";
	
	/**
	 * User image
	 */
	public static final String PROPERTY_IMAGE = "image";
	
	/**
	 * User info
	 */
	public static final String PROPERTY_INFO = "info";// status
	
	/**
	 * User password
	 */
	public static final String PROPERTY_PASSWORD = "password";
	
	
	/**
	 * This variable is only local, each one can have blocked somebody
	 */
	private boolean blocked;
	
	

	
	public UserEntity(EntityData entityData ) {
		super(entityData);		
		this.addTouchListener(new UserEntityTouchListener( ));
		init2D();
	}
	
	
	public void init2D( ){

		this.setDrawable2D(new Text2D(getData().getPropertyValue(PROPERTY_USER)));
	}
	
	public class UserEntityTouchListener implements TouchListener {

		@Override
		public boolean onTouchDown(WorldEntity e, float x, float y) {
			final String s = getData().getPropertyValue(PROPERTY_INFO);
			
			LookARUtil.getApp().runOnUiThread( new Runnable( ){

				@Override
				public void run() {
					ViewGroup v = LookARUtil.getApp().getHudContainer();
					v.removeAllViews();
					View l = LookARUtil.getView(R.layout.text_hud, v);
					TextView textView = (TextView) l.findViewById(R.id.text_hud_view);
					textView.setText(s);
					
				}
				
			});
			return true;

			
		}

		@Override
		public boolean onTouchUp(WorldEntity e, float x, float y) {
			return false;
			
		}

		@Override
		public boolean onTouchMove(WorldEntity e, float x, float y) {
			return false;
			
		}
		
	}


	public void setBlocked(boolean b) {
		this.blocked = b;
	}

	public boolean isBlocked() {
		return blocked;
	}

}
