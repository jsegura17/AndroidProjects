package com.dazaba.gastos;

import com.dazaba.dto.WS_Data;
import com.dazaba.dto.dao_manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class menuPrincipal extends Activity {
	private dao_manager _daoMgr;
	
	//DTO
    private WS_Data _ws_data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		
		Button btnExit = (Button) findViewById(R.id.btn_menu_exit);
		Button btnAsociarCta = (Button) findViewById(R.id.btn_menu_asociar_cta);
		Button btnIncluirGasto = (Button) findViewById(R.id.btn_menu_incluir_gasto);
		Button btnGastosXCta = (Button) findViewById(R.id.btn_menu_gastos_x_cta);
		
		//Log
		TextView response_ws = (TextView)findViewById(com.dazaba.gastos.R.id.txtLogMenu);
		
		//Listening to Btn Asociar Cta in Menu
		btnAsociarCta.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Switching to Asociar Cta screen
				Intent i = new Intent(menuPrincipal.this, asociar_cta.class);
				startActivity(i);	
			}
		});
		
		//Listening to Btn Incluir Gasto in Menu
		btnIncluirGasto.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Switching to Incluir Gasto screen
				Intent i = new Intent(menuPrincipal.this, incluir_gasto.class);
				startActivity(i);	
			}
		});
		
		//Listening to Btn Gastos x Cta in Menu
		btnGastosXCta.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// Switching to Gastos x Cta screen
				Intent i = new Intent(menuPrincipal.this, gastos_x_cta.class);
				startActivity(i);	
			}
		});
		
		//Listening to Button Exit in Menu
		btnExit.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();	
			}
		});
		
		//Init Dae Manager
		_daoMgr = dao_manager.getInstance();
		//Read WS
		_daoMgr.read_WS();
		//Get WS_Data DTO
		_ws_data = _daoMgr.get_WS_Data();
		
		//Set text to LOG Label
		response_ws.setText(_ws_data.get_ResponseWS());
		
		
	}	

}
