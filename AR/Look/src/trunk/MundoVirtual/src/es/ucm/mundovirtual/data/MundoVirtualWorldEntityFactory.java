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
package es.ucm.mundovirtual.data;

import es.ucm.look.ar.ar3D.core.Color4;
import es.ucm.look.ar.ar3D.core.drawables.DrawablesDataBase;
import es.ucm.look.ar.ar3D.core.drawables.Entity3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.Cube;
import es.ucm.look.ar.ar3D.core.drawables.primitives.ObjMesh3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.SquarePrimitive;
import es.ucm.look.ar.ar3D.core.drawables.primitives.extra.ImagePrimitive;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.WorldEntity;
import es.ucm.look.data.WorldEntityFactory;
import es.ucm.look.mundovirtual.R;
import es.ucm.mundovirtual.data.entities.ObjectWorldEntity;

public class MundoVirtualWorldEntityFactory extends WorldEntityFactory {

	public static final String NAME = "name";
	public static final String COLOR = "color";
	public static final String MESSAGE = "message";

	private ObjectTouchListener touchListener = new ObjectTouchListener();

	private ObjectCameraListener cameraListener = new ObjectCameraListener();

	private ObjMesh3D ufo = DrawablesDataBase.getInstance().getDrawable3D(
			R.raw.ufo);
	private ObjMesh3D monkey = DrawablesDataBase.getInstance().getDrawable3D(
			R.raw.monkey_obj);
	private ObjMesh3D ucm = DrawablesDataBase.getInstance().getDrawable3D(
			R.raw.ucm);
	private ObjMesh3D maya = DrawablesDataBase.getInstance().getDrawable3D(
			R.raw.mayantemple);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.ucm.look.data.WorldEntityFactory#createWorldEntity(es.ucm.look.data
	 * .EntityData)
	 */
	@Override
	public WorldEntity createWorldEntity(EntityData data) {

		WorldEntity we = new ObjectWorldEntity(data);
		Entity3D drawable3d = null;

		if (data.getType().equals("cube")) {
			drawable3d = new Entity3D(new Cube());

		} else if (data.getType().equals("ufo")) {
			drawable3d = new Entity3D(ufo);
		} else if (data.getType().equals("monkey")) {
			drawable3d = new Entity3D(monkey);
			drawable3d.getMatrix().rotate((float) Math.PI, 0.0f, 0.0f);
		} else if (data.getType().equals("ground")) {
			drawable3d = new Entity3D(new ImagePrimitive());
			drawable3d.getMatrix().scale(100, 100, 100);
			drawable3d.getMatrix().rotate(0, 0, (float) (Math.PI / 2));
			drawable3d.setTexture(R.drawable.ucm);
			we.setEnable(false);
		} else if (data.getType().equals("ucm")) {
			drawable3d = new Entity3D(ucm);
			drawable3d.setMaterial(new Color4(1.0f, 1.0f, 1.0f));
			drawable3d.getMatrix().rotate((float) Math.PI, 0.0f, 0.0f);
			drawable3d.setTexture(R.drawable.ucm);
		} else if (data.getType().equals("maya")) {
			drawable3d = new Entity3D(maya);
			drawable3d.setMaterial(new Color4(1.0f, 1.0f, 0.8f));
			drawable3d.getMatrix().rotate((float) Math.PI, 0.0f, 0.0f);
			drawable3d.getMatrix().scale(5, 5, 5);
			drawable3d.setTexture(R.drawable.ucm);
		} else {
			drawable3d = new Entity3D(new Cube());
		}

		String color = data.getPropertyValue(COLOR);

		if (color != null)
			if (color.equals("red")) {
				drawable3d.setMaterial(new Color4(1.0f, 0.0f, 0.0f));
			} else if (color.equals("blue")) {
				drawable3d.setMaterial(new Color4(0.0f, 0.0f, 1.0f));
			} else if (color.equals("green")) {
				drawable3d.setMaterial(new Color4(0.0f, 1.0f, 0.0f));
			} else if (color.equals("white")) {
				drawable3d.setMaterial(new Color4(1.0f, 1.0f, 1.0f));
			} else if (color.equals("yellow")) {
				drawable3d.setMaterial(new Color4(1.0f, 1.0f, 0.0f));
			} else if (color.equals("black")) {
				drawable3d.setMaterial(new Color4(0.5f, 0.5f, 0.5f));
			} else {
				drawable3d.setMaterial(new Color4(1.0f, 1.0f, 1.0f));
			} 
		we.setDrawable3D(drawable3d);
		we.addTouchListener(touchListener);
		we.addCameraListener(cameraListener);

		return we;
	}
}
