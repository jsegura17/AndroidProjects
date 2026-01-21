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
package es.ucm.imagegallery.data;

import es.ucm.imagegallery.data.entities.ImageEntityFactory;
import es.ucm.look.ar.util.DeviceOrientation;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.World;

public class ImageGalleryWorld extends World {

	private boolean movingVertical;
	
	private static final int TIME_MOVING = 3000;
	
	private float offset = 0;

	private int dir;

	private float accDy;
	
	private int currentRow;
	
	private int timeToBeMoving = 0;
	
	private int direction;
	
	private ImageEntityFactory factory;

	public ImageGalleryWorld( ImageEntityFactory factory ) {
		this.factory = factory;
	}

	public void moveVertical(int direction) {
		if (!movingVertical && currentRow + direction >= 0
				&& currentRow + direction <= factory.getMaxRow() ) {
			movingVertical = true;
			accDy = factory.getDy();
			dir = direction;
		}
	}

	public void update(long elapsed) {
		super.update(elapsed);
		if (movingVertical) {
			float advance = factory.getDy() * (float) elapsed / 500.0f;
			if (advance >= accDy) {
				advance = accDy;
				movingVertical = false;
				currentRow += dir;
			}
			accDy -= advance;
			synchronized (this.getLocation()) {
				this.getLocation().y += dir * advance;
			}
		}
		
		if ( timeToBeMoving  > 0 ){
			timeToBeMoving -= elapsed;
			offset += (float) direction * (float) elapsed * timeToBeMoving / 1000000.0f ;
			DeviceOrientation.getDeviceOrientation(LookARUtil.getApp()).setAzimuthOffset(offset);
		}
	}

	public void moveHorizontal(int direction, float percentage ) {
		timeToBeMoving = (int) (Math.abs(direction * TIME_MOVING * percentage));
		this.direction = direction;
	}

}
