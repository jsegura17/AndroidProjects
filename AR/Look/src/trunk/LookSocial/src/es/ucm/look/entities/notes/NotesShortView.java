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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotesShortView extends FrameLayout {

	private TextView sender, message;

	private ImageView image;

	private ViewGroup hudContainer;

	private Activity activity;

	private boolean opened;

	private int imageId;

	private CharSequence senderName;

	private CharSequence messageText;

	private boolean changed = false;

	public NotesShortView(Activity activity, ViewGroup hudContainer) {
		super(activity);
		this.activity = activity;
		LinearLayout l = new LinearLayout(activity);
		l.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		image = new ImageView(activity);
		sender = new TextView(activity);
		message = new TextView(activity);
		message.setPadding(10, 0, 0, 0);
		this.hudContainer = hudContainer;
		l.addView(image);
		l.addView(sender);
		l.addView(message);
		this.addView(l);
		opened = false;
	}

	public void setAttributes(String senderName, String messageText, int imageId) {
		this.imageId = imageId;
		this.senderName = senderName;
		this.messageText = messageText;
		changed = true;
	}

	public void setVisible(boolean b) {
		if (b)
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (changed) {
						image.setImageResource(imageId);
						sender.setText(senderName);
						message.setText(messageText);
						changed = false;
					}
					if ( hudContainer.indexOfChild(NotesShortView.this) == -1 )
						hudContainer.addView(NotesShortView.this);

				}

			});
		else
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (!opened) {
						hudContainer.removeView(NotesShortView.this);
					}

				}

			});
	}

}
