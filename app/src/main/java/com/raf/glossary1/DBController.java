package com.raf.glossary1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
//import android.util.Log;

public class DBController  extends SQLiteOpenHelper {
	//	private static final String LOGCAT = null;
	Context context;

	public DBController(Context context) {
		super(context, "glossary1.db", null, 1);
		// Log.d(LOGCAT, "Database created"); 
		this.context = context;
    }
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		String query;	
		query = "CREATE TABLE terms " +
				"( termID INTEGER PRIMARY KEY AUTOINCREMENT, termEnglish TEXT," +
				" termSerbian TEXT, termDescription TEXT)";
        database.execSQL(query);
        //Log.d(LOGCAT,"Terms created");
		//executeSQLScript(database, "glossary.txt");
	}		
		
	private void executeSQLScript(SQLiteDatabase database, String dbname) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = null;	
		
		
		try {
			inputStream = assetManager.open(dbname);
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
			
			String[] createScript = outputStream.toString().split("\r\n");
						
			for (int i = 0; i < createScript.length; i++) {
				String sqlStatement = createScript[i].trim();
				String[] polje = sqlStatement.toString().split("&");
				if (sqlStatement.length() > 0) {
					sqlStatement = "INSERT INTO terms (termEnglish, termSerbian," +
							" termDescription) VALUES ('" + polje[0] + "', '" + 
							polje[1] + "', '" + polje[2] + "');";
					//System.out.println(sqlStatement);
					database.execSQL(sqlStatement);
				}
			}
		} catch (IOException e) {
			// TODO Handle Script Failed to Load			
		}
	}
		
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
		String query;
		query = "DROP TABLE IF EXISTS terms";
		database.execSQL(query);
        onCreate(database);
	}

	public void populate(ArrayList<HashMap<String, String>> termListDb) {
		SQLiteDatabase database = this.getWritableDatabase();
		database.execSQL("BEGIN;");
		database.execSQL("delete from terms");

		for (int i = 0; i < termListDb.size(); i++) {
			HashMap<String, String> term = termListDb.get(i);
			ContentValues values = new ContentValues();
			values.put("termEnglish", term.get("termEnglish"));
			values.put("termSerbian", term.get("termSerbian"));
			values.put("termDescription", term.get("termDescription"));
			database.insert("terms", null, values);
		}

		database.execSQL("COMMIT;");
	}
	
	public void insertTerm(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("termEnglish", queryValues.get("termEnglish"));
		values.put("termSerbian", queryValues.get("termSerbian"));
		values.put("termDescription", queryValues.get("termDescription"));
		database.insert("terms", null, values);
		database.close();
	}
	
	public int updateTerm(HashMap<String, String> queryValues) {
		SQLiteDatabase database = this.getWritableDatabase();	 
	    ContentValues values = new ContentValues();
	    values.put("termEnglish", queryValues.get("termEnglish"));
		values.put("termSerbian", queryValues.get("termSerbian"));
		values.put("termDescription", queryValues.get("termDescription"));
	    return database.update("terms", values, "termID" + " = ?", 
	    		new String[] { queryValues.get("termID") });
	}
	
	public void deleteTerm(String id) {
//		Log.d(LOGCAT,"delete");
		SQLiteDatabase database = this.getWritableDatabase();	 
		String deleteQuery = "DELETE FROM  terms where termID='"+ id +"'";
//		Log.d("query",deleteQuery);		
		database.execSQL(deleteQuery);
	}
	
	public ArrayList<HashMap<String, String>> getAllTerms() {
		ArrayList<HashMap<String, String>> wordList;
		wordList = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT * FROM terms ORDER BY termEnglish";
		//String selectQuery = "SELECT  * FROM terms";
	    SQLiteDatabase database = this.getWritableDatabase();
	    Cursor cursor = database.rawQuery(selectQuery, null);
	    if (cursor.moveToFirst()) {
	        do {
	        	HashMap<String, String> map = new HashMap<String, String>();
	        	map.put("termID", cursor.getString(0));
	        	map.put("termEnglish", cursor.getString(1));
	        	map.put("termSerbian", cursor.getString(2));
	        	map.put("termDescription", cursor.getString(3));
                wordList.add(map);
	        } while (cursor.moveToNext());
	    }	 
	    // return contact list
	    return wordList;
	}
	
	public HashMap<String, String> getTermInfo(String id) {
		HashMap<String, String> wordList = new HashMap<String, String>();
		SQLiteDatabase database = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM terms where termID='"+id+"'";
//		Log.d(LOGCAT,selectQuery);
		
		Cursor cursor = database.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
	        do {
					//HashMap<String, String> map = new HashMap<String, String>();
	        	wordList.put("termEnglish", cursor.getString(1));
	        	wordList.put("termSerbian", cursor.getString(2));
	        	wordList.put("termDescription", cursor.getString(3));
	        	
				   //wordList.add(map);
	        } while (cursor.moveToNext());
	    }				    
	return wordList;	
	}		
}
