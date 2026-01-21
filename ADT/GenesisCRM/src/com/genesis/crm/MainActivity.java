package com.genesis.crm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

   private EditText usernameField,passwordField;
   private TextView status,role;
   private AsyncTask signingAct;

   @Override 
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      usernameField = (EditText)findViewById(R.id.editText1);
      passwordField = (EditText)findViewById(R.id.editText2);
      status = (TextView)findViewById(R.id.textView6);
      role = (TextView)findViewById(R.id.textView7);
   }
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }
   public void login(View view){
      String username = usernameField.getText().toString();
      String password = passwordField.getText().toString();	     
      signingAct = new SigninActivity(this,status,role,0).execute(username,password);	
      signingAct.
   }   
	  
}
