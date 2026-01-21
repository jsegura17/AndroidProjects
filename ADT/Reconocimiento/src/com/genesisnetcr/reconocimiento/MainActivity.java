package com.genesisnetcr.reconocimiento;

import java.util.ArrayList;
import java.util.Vector;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
	private Button bt_start;
	private Vector<String> nombres;
	private Vector<String> telefonos;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Relacionamos el boton con el XML 
		  bt_start = (Button)findViewById(R.id.button1); 
		  bt_start.setOnClickListener(new OnClickListener() {
		   @Override
		   public void onClick(View v) {
		    //Lanzamos el reconoimiento de voz
		    startVoiceRecognitionActivity();
		   }
		  });
		  //Recogemos todos los telefonos y nombre en los
		  //vetores: nombres y telefonos
		  getNameNumber();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void startVoiceRecognitionActivity() {
	  // Definición del intent para realizar en análisis del mensaje
	  Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	  // Indicamos el modelo de lenguaje para el intent
	  intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	  // Definimos el mensaje que aparecerá 
	  intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Diga, Llamar a ...");
	  // Lanzamos la actividad esperando resultados
	  startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	 }
	
	@Override
	 //Recogemos los resultados del reconocimiento de voz
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  //Si el reconocimiento a sido bueno
	  if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
	  //El intent nos envia un ArrayList aunque en este caso solo 
	   //utilizaremos la pos.0
	    ArrayList<String> matches = data.getStringArrayListExtra
	      (RecognizerIntent.EXTRA_RESULTS);
	    //Separo el texto en palabras.
	    String [ ] palabras = matches.get(0).toString().split(" ");
	    //Si la primera palabra es LLAMAR
	    if(palabras[0].equals("llamar")){
	    for(int a=0;a<nombres.size();a++){
	    //Busco el nombre que es la tercera posicion (LLAMAR A LORENA)
	    if(nombres.get(a).equals(palabras[2])){
	    //Si la encuentra recojo el numero telf en el otro
	    //vector que coincidira con la posicion ya que
	    //los hemos rellenado a la vez.
	    Intent callIntent = new Intent(Intent.ACTION_CALL);
	    callIntent.setData(Uri.parse("tel:"+telefonos.get(a)));
	    //Realizo la llamada
	    startActivity(callIntent);
	    break;
	    }
	    }
	    }
	  }
	}
	
	//Con el getNameNumber lo que hago es recoger los nombres 
	 //de la SIM en un vector
	 //Y los numeros de telefonos en otro vector, eso sí tienen que coincidir
	 //las posiciones de uno y de otro, por eso los relleno a la vez.
	 private void getNameNumber(){ 
	 nombres = new Vector<String>();
	 telefonos = new Vector<String>(); 
	        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	        ContentResolver cr = getContentResolver();
	        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
	       null, null, null, null);
	        String[] projection = new String[] {
	       ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	              ContactsContract.CommonDataKinds.Phone.NUMBER };
	        Cursor names = getContentResolver().query(
	       uri, projection, null, null, null);
	        int indexName = names.getColumnIndex(
	       ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
	        int indexNumber = names.getColumnIndex(
	       ContactsContract.CommonDataKinds.Phone.NUMBER);
	        names.moveToFirst();
	        do {
	           //Aquí relleno los dos
	           String name   = names.getString(indexName);
	           nombres.add(name);
	           String number = names.getString(indexNumber);
	           telefonos.add(number);
	        } while (names.moveToNext());
	    }

}
