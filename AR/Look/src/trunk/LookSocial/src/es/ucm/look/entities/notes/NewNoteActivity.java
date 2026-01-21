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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import es.ucm.look.R;

public class NewNoteActivity extends Activity {
	
	private EditText editText;
	
	@Override
	public void onCreate( Bundle savedInstance ){
		super.onCreate(savedInstance);
		this.setContentView(R.layout.newnote);
		editText = (EditText) this.findViewById(R.id.editNote);
		
		Button ok = (Button) this.findViewById(R.id.ButtonOKNote);
		ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent( NewNoteActivity.this, NewNoteActivity.class );
				i.putExtra("text", editText.getText());
				setResult( Activity.RESULT_OK, i );
				
				NewNoteActivity.this.finish();
				//LookARUtil.getApp().startActivity(new Intent( NewNoteActivity.this, LookAugmentedReality.class));
				
			}
			
		});
		
		Button cancel = (Button) this.findViewById(R.id.ButtonCancelNote);
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i = new Intent( NewNoteActivity.this, NewNoteActivity.class );
				i.putExtra("text", editText.getText());
				setResult( Activity.RESULT_CANCELED, i );
				NewNoteActivity.this.finish();
				//LookARUtil.getApp().startActivity(new Intent( NewNoteActivity.this, LookAugmentedReality.class));
			}
			
		});
	}

}
