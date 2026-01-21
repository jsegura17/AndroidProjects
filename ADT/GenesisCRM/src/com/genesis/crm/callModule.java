package com.genesis.crm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class callModule extends Activity{
	
	private int module = 0;
	private Context contextOrig;
	
	//flag 0 means get and 1 means post.(By default it is get.)
   public callModule(Context context, int moduleToCall) {
	   module = moduleToCall;
	   contextOrig = context;
   }
   
   public void executeModule(){
	   switch (module) {
		case 1: //MenuCRM
			// Switching to screen
			Intent i = new Intent(contextOrig, MenuCRM.class);
			startActivity(i);	
			break;	
		default:
			break;
		}
   }
}
