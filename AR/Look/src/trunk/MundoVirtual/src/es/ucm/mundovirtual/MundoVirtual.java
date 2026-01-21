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
package es.ucm.mundovirtual;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.ViewGroup;
import android.widget.Button;
import es.ucm.look.ar.LookAR;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;
import es.ucm.look.mundovirtual.R;
import es.ucm.mundovirtual.data.MundoVirtualWorld;
import es.ucm.mundovirtual.data.MundoVirtualWorldEntityFactory;

public class MundoVirtual extends LookAR {

	public MundoVirtual() {
		super(true, true, true, true, 100.0f, true);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initUI();

		LookData.getInstance().setWorldEntityFactory(
				new MundoVirtualWorldEntityFactory());
		LookData.getInstance().setWorld(
				new MundoVirtualWorld((Button) findViewById(R.id.up)));
		MundoVirtualWorld w = (MundoVirtualWorld) LookData.getInstance()
				.getWorld();
		this.setWorld(w);

		EntityData data = new EntityData("cube");
		data.setLocation(10, 0, 0);
		data.setPropertyValue(MundoVirtualWorldEntityFactory.NAME, "cubo");
		data.setPropertyValue(MundoVirtualWorldEntityFactory.COLOR, "red");
		data.setPropertyValue(MundoVirtualWorldEntityFactory.MESSAGE,
				"Un simple cubo volador");

		EntityData ufo = new EntityData("ufo");
		ufo.setLocation(10, 0, 10);
		ufo.setPropertyValue(MundoVirtualWorldEntityFactory.NAME, "ufo");
		ufo.setPropertyValue(MundoVirtualWorldEntityFactory.COLOR, "blue");
		ufo.setPropertyValue(MundoVirtualWorldEntityFactory.MESSAGE,
				"¡Ya vienen!");

		EntityData grid = new EntityData("ground");
		grid.setLocation(0, 20, 0);
		grid.setPropertyValue(MundoVirtualWorldEntityFactory.NAME, "ground");
		grid.setPropertyValue(MundoVirtualWorldEntityFactory.COLOR, "black");

		EntityData monkey = new EntityData("monkey");
		monkey.setLocation(0, 0, 10);
		monkey.setPropertyValue(MundoVirtualWorldEntityFactory.NAME, "monkey");
		monkey.setPropertyValue(MundoVirtualWorldEntityFactory.COLOR, "yellow");
		monkey.setPropertyValue(MundoVirtualWorldEntityFactory.MESSAGE,
				"El Gran Mono te saluda");

		EntityData ucm = new EntityData("ucm");
		ucm.setLocation(25, 0, 15);
		ucm.setPropertyValue(MundoVirtualWorldEntityFactory.MESSAGE, "Tócame!");

		EntityData maya = new EntityData("maya");
		maya.setLocation(-50, 18, -50);
		maya.setPropertyValue(MundoVirtualWorldEntityFactory.MESSAGE,
				"Un templo Maya!");

		LookData.getInstance().getDataHandler().addEntity(data);
		LookData.getInstance().getDataHandler().addEntity(grid);
		LookData.getInstance().getDataHandler().addEntity(ufo);
		LookData.getInstance().getDataHandler().addEntity(monkey);
		LookData.getInstance().getDataHandler().addEntity(ucm);
		LookData.getInstance().getDataHandler().addEntity(maya);

		LookData.getInstance().updateData();

		LookData.getInstance().startLocation(100, true, false);

	}

	private void initUI() {
		ViewGroup vg = LookARUtil.getApp().getHudContainer();
		LookARUtil.getView(R.layout.layout, vg);

	}
}
