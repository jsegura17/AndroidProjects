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
package es.ucm.invaders;

import android.os.Bundle;
import es.ucm.invaders.entities.enemies.UFO;
import es.ucm.invaders.entities.projectiles.BasicMissile;
import es.ucm.invaders.hud.Scope;
import es.ucm.look.ar.LookAR;
import es.ucm.look.data.LookData;

public class Invaders extends LookAR {
	
	public Invaders( ){
		super( false, true, true, true, 100.0f );
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InvadersWorld w = new InvadersWorld( );
		get2DLayer().getHUD().add(new Scope( w ));
		LookData.getInstance().setWorldEntityFactory(null);
		UFO.initResources();
		BasicMissile.initResources();
		LookData.getInstance().setWorld(w);
		
					
	}

}
