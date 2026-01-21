package es.ucm.myaractivity.listeners;

import es.ucm.look.ar.listeners.TouchListener;
import es.ucm.look.ar.math.geom.Point3;
import es.ucm.look.data.LookData;
import es.ucm.look.data.WorldEntity;

public class MyTouchListener implements TouchListener {

	@Override
	public boolean onTouchDown(WorldEntity e, float x, float y) {
		Point3 p = e.getLocation();
		LookData.getInstance().getDataHandler().updatePosition(e.getData(), p.x, p.y - 1, p.z);
		return true;
	}

	@Override
	public boolean onTouchUp(WorldEntity e, float x, float y) {
		return false;
	}

	@Override
	public boolean onTouchMove(WorldEntity e, float x, float y) {
		return false;
	}

}
