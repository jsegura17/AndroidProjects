package es.ucm.myaractivity.listeners;

import es.ucm.look.ar.ar3D.Drawable3D;
import es.ucm.look.ar.ar3D.core.drawables.primitives.SquarePrimitive;
import es.ucm.look.ar.listeners.CameraListener;
import es.ucm.look.data.WorldEntity;

public class MyCameraListener implements CameraListener {
	
	private static SquarePrimitive square = new SquarePrimitive();
	
	private Drawable3D oldDrawable;

	@Override
	public void onCameraEntered(WorldEntity entity) {
		oldDrawable = entity.getDrawable3D();
		entity.setDrawable3D(square);

	}

	@Override
	public void onCameraExited(WorldEntity entity) {
		entity.setDrawable3D(oldDrawable);
	}

}
