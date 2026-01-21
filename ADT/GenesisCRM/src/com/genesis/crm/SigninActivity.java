package com.genesis.crm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.genesis.crm.events.SimpleEvent;

import android.R.bool;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.TextView;

public class SigninActivity  extends AsyncTask<String,Void,String>{
	
	public static class LoginEvent extends SimpleEvent {
		public static final String LOGIN_OK = "loginOK";
		public static final String LOGIN_FAIL = "loginFail";
		
		public LoginEvent(String type) {
			super(type);
		}
	}

   private TextView statusField,roleField;
   private Context context;
   private int byGetOrPost = 0; 
   //flag 0 means get and 1 means post.(By default it is get.)
   public SigninActivity(Context context,TextView statusField,
   TextView roleField,int flag) {
      this.context = context;
      this.statusField = statusField;
      this.roleField = roleField;
      byGetOrPost = flag;
   }

   protected void onPreExecute(){

   }
   @Override
   protected String doInBackground(String... arg0) {
      if(byGetOrPost == 0){ //means by Get Method
         try{
            String username = (String)arg0[0].trim();
            String password = (String)arg0[1].trim();
            String link = "http://genesisnetcr.260mb.net/crm/login.php?username="
            +username+"&password="+password;
            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader
           (new InputStreamReader(response.getEntity().getContent()));

           StringBuffer sb = new StringBuffer("");
           String line="";
           while ((line = in.readLine()) != null) {
              sb.append(line);
              break;
            }
            in.close();
            return sb.toString();
	      }catch(Exception e){
	         return new String("Exception: " + e.getMessage());
	      }
      }
      else{
         //POST disable
    	  return new String("POST Disable: ");
      }
   }
   @Override
   protected void onPostExecute(String result){
      this.statusField.setText("Login Successful");
      this.roleField.setText(result);
      
      //callModule runMod = new callModule(this.context,1);
      //runMod.executeModule();
   } 
}