package es.ucm.myaractivity.contentprovider;

import es.ucm.look.data.local.contentprovider.sql.LookSQLContentProvider;

public class MyContentProvider extends LookSQLContentProvider {

	public MyContentProvider() {
		super("mydatabase.db", "es.ucm.myaractivity.contentprovider");
	}

}
