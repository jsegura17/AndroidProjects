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
package es.ucm.look.subactivities.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import es.ucm.look.R;

public class MoreOptionsAccount extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.more_options_account);

		final Button cancelButton = (Button) findViewById(R.id.cancel);
		final Button saveButton = (Button) findViewById(R.id.save);

		saveButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				// capture the entered fields 
				EditText nameET = (EditText) findViewById(R.id.name);
				String name = nameET.getText().toString();
				
				EditText statusET = (EditText) findViewById(R.id.status);
				String status = statusET.getText().toString();

				// send the captured data
				Bundle bundle = new Bundle();
				bundle.putString("name", name);
				bundle.putString("status", status);
				Intent mIntent = new Intent();
				mIntent.putExtras(bundle);
				setResult(RESULT_OK, mIntent);
				finish();
			}
		});

		cancelButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent mIntent = new Intent();
				setResult(RESULT_CANCELED, mIntent);
				finish();
			}
		});

	}

}
