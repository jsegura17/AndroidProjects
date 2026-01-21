package es.ucm.look.entities;

import es.ucm.look.data.EntityData;
import es.ucm.look.data.WorldEntity;
import es.ucm.look.data.WorldEntityFactory;
import es.ucm.look.entities.notes.Note;
import es.ucm.look.entities.user.UserEntity;

public class LookSocialEntityFactory extends WorldEntityFactory {

	@Override
	public WorldEntity createWorldEntity(EntityData data) {
		if (data.getType().equals(UserEntity.TYPE_USER)){
			return new UserEntity(data);
		}else if (data.getType().equals(Note.TYPE_NOTE)){
			return new Note(data);
		}
		return null;
		
	}

}
