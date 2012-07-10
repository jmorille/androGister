package eu.ttbox.androgister.database.bootstrap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.database.product.PersonDatabase;

public class PersonDbBootstrap {

	private static final String TAG = "PersonDbBootstrap";

	private final Context mHelperContext;
	private SQLiteDatabase mDatabase;

	public PersonDbBootstrap(Context mHelperContext, SQLiteDatabase mDatabase) {
		super();
		this.mHelperContext = mHelperContext;
		this.mDatabase = mDatabase;
	}

	/**
	 * Starts a thread to load the database table with words
	 */
	public void loadDictionary() {
		new Thread(new Runnable() {
			public void run() {
				try {
					loadProducts();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
	}

	private void loadProducts() throws IOException {
		Log.d(TAG, "Loading persons...");
		final Resources resources = mHelperContext.getResources();
		InputStream inputStream = resources.openRawResource(R.raw.persons);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		try {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] strings = TextUtils.split(line, "-");
				if (strings.length < 2)
					continue;
				long id = addPerson(strings[0].trim(), strings[1].trim(), strings[2].trim());
				Log.i(TAG, String.format("Add Person id %s : name=%s", id, strings[0]));
				if (id < 0) {
					Log.e(TAG, "unable to add Person : " + strings[0].trim());
				}
			}
		} finally {
			reader.close();
		}
		Log.d(TAG, "DONE loading persons.");
	}

	/**
	 * Add a word to the dictionary.
	 * 
	 * @return rowId or -1 if failed
	 */
	public long addPerson(String name, String tag, String price) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(PersonDatabase.PersonColumns.KEY_LASTNAME, name);
		initialValues.put(PersonDatabase.PersonColumns.KEY_FIRSTNAME, tag);
		initialValues.put(PersonDatabase.PersonColumns.KEY_MATRICULE, price);
		return mDatabase.insert(PersonDatabase.TABLE_PERSON_FTS, null, initialValues);
	}

}
