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
package es.ucm.look.subactivities.looklist;

import java.util.ArrayList;
import java.util.Collection;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.ucm.look.R;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;
import es.ucm.look.data.WorldEntity;
import es.ucm.look.entities.user.UserEntity;

public class LookList extends ListActivity {

	private static final int MENU_BLOCK_User = Menu.FIRST + 1;
	private static final int MENU_UNBLOCK_User = Menu.FIRST + 2;

	private UsersAdapter mAdapter;

	private static ArrayList<WorldEntity> mList;
	

	private void createListUsers() {
		
		mList = new ArrayList<WorldEntity>();
		
		Collection<WorldEntity> entities = LookData.getInstance().getWorld()
				.getWorldEntities();

		for (WorldEntity w : entities) {
			if (w.getType()!=null && w.getType().equals(UserEntity.TYPE_USER)) {
				mList.add((WorldEntity) w);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createListUsers();//Initialize the list
		mAdapter = new UsersAdapter(this);
		setListAdapter(mAdapter);
		registerForContextMenu(this.getListView());

	}

	// ------------MENUS-------------

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		menu.add(0, MENU_BLOCK_User, 0, "Block User");
		menu.add(0, MENU_UNBLOCK_User, 0, "Unblock User");

	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      //ignore orientation change
      super.onConfigurationChanged(newConfig);
    } 
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		UserEntity myUser = (UserEntity) mList.get(info.position);

		switch (item.getItemId()) {
		case MENU_BLOCK_User:

			myUser.setBlocked(true);

			mAdapter.notifyDataSetChanged();

			break;

		case MENU_UNBLOCK_User:

			myUser.setBlocked(false);

			mAdapter.notifyDataSetChanged();

			break;

		}

		return true;
	}

	protected static class UsersAdapter extends BaseAdapter {

		private Context mContext;

		public UsersAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.list_users, null);
			} else {
				view = convertView;
			}

			EntityData user = mList.get(position).getData();

			ImageView img = (ImageView) view.findViewById(R.id.image);
			
			//TODO por ahora establecemos las imagenes a empty
			if (user.getPropertyValue(UserEntity.PROPERTY_IMAGE) == null){
				user.setPropertyValue(UserEntity.PROPERTY_IMAGE, "empty.png");
			}
						
			img.setImageBitmap(LookData.getInstance().getFilesManager().getImageBitmap("avatars/"+user
					.getPropertyValue(UserEntity.PROPERTY_IMAGE)));

			TextView t3 = (TextView) view.findViewById(R.id.name);
			t3.setText(user.getPropertyValue(UserEntity.PROPERTY_NAME));
			 
			
			LinearLayout layoutUser = (LinearLayout) view.findViewById(R.id.rectangle_user);
			/*if (user.isBlocked())
				layoutUser.setBackgroundColor(Color.parseColor("#E95A3F"));
			else
				layoutUser.setBackgroundColor(Color.parseColor("#E5EEF9"));
				*/
				

			TextView t2 = (TextView) view.findViewById(R.id.info);
			t2.setText(user.getPropertyValue(UserEntity.PROPERTY_INFO));

			t2 = (TextView) view.findViewById(R.id.username);
			t2.setText(user.getPropertyValue(UserEntity.PROPERTY_USER));

			return view;

		}

	}

}
