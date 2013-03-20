package eu.ttbox.androgister.test.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.test.ProviderTestCase2;
import eu.ttbox.androgister.database.UserProvider;
import eu.ttbox.androgister.database.user.UserDatabase.UserColumns;

/**
 * {link http://developer.android.com/tools/testing/index.html}
 *  
 */
public class UserProviderTest extends ProviderTestCase2<UserProvider> {

	ContentResolver contentResolver;
	
	public UserProviderTest() {
		super(UserProvider.class, UserProvider.Constants.AUTHORITY);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver =getContext().getContentResolver();
//				getMockContentResolver() .acquireContentProviderClient(UserProvider.Constants.AUTHORITY)
//	            .getLocalContentProvider(); 
		
	}

	// public void testQuery(){
	// ContentProvider provider = getProvider();
	// Uri uri = UserProvider.Constants.CONTENT_URI;
	// Cursor cursor = provider.query(uri, null, null, null, null);
	// assertNotNull(cursor);
	// System.out.println("Cursor result size : " + cursor.getCount());
	//
	// }

	public void testInsertUser() throws OperationApplicationException {
//		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//		// Insert One
//		ops.add(ContentProviderOperation.newInsert(UserProvider.Constants.CONTENT_URI)
//		.withValue(UserColumns.KEY_FIRSTNAME, "Jame") 
//		.withValue(UserColumns.KEY_LASTNAME, "Bond") 
//		.withValue(UserColumns.KEY_MATRICULE, "007") 
//		.build());
		// Do Insert
		ContentValues values = new ContentValues();
		values.put(UserColumns.KEY_FIRSTNAME, "Jame");
		values.put(UserColumns.KEY_LASTNAME, "Bond");
		values.put(UserColumns.KEY_MATRICULE, "007");
		Uri insertUri = contentResolver.insert(UserProvider.Constants.CONTENT_URI, values);
		assertNotNull(insertUri);
		System.out.println("Result insert Uri " + insertUri);
	}
}
