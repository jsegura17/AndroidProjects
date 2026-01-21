package es.ucm.myaractivity;

import android.os.Bundle;
import android.view.ViewGroup;
import es.ucm.look.ar.LookAR;
import es.ucm.look.ar.util.LookARUtil;
import es.ucm.look.data.EntityData;
import es.ucm.look.data.LookData;

public class MyARActivity extends LookAR {

	public MyARActivity() {
		super(true, true, false, true, 100.0f, true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addHud();
		LookData.getInstance()
				.setWorldEntityFactory(new MyWorldEntityFactory());

		// Create the element data
		EntityData data = new EntityData();
		data.setLocation(10, 0, 0);
		data.setPropertyValue(MyWorldEntityFactory.NAME, "Element 1");
		data.setPropertyValue(MyWorldEntityFactory.COLOR, "green");

		EntityData data1 = new EntityData();
		data1.setLocation(10, 0, 5);
		data1.setPropertyValue(MyWorldEntityFactory.NAME, "Element 2");
		data1.setPropertyValue(MyWorldEntityFactory.COLOR, "red");

		// Add the data to the data handler
		LookData.getInstance().getDataHandler().addEntity(data);
		LookData.getInstance().getDataHandler().addEntity(data1);

		// Updates the recent added data
		LookData.getInstance().updateData();

	}

	private void addHud() {
		ViewGroup v = getHudContainer(); 
		v.addView(LookARUtil.getView(R.layout.main, null));
		
	}
}