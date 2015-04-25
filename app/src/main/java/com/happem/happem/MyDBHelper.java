package com.happem.happem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**  SQLiteOpenHelper
  		You create a subclass implementing onCreate(SQLiteDatabase), onUpgrade(SQLiteDatabase, int, int) 
  		and optionally onOpen(SQLiteDatabase), and this class takes care of opening the database if it exists, 
  		creating it if it does not, and upgrading it as necessary. 
  		Transactions are used to make sure the database is always in a sensible state.  **/
public class MyDBHelper extends SQLiteOpenHelper{
	
	private final static String TAG_LOG = "My DB Helper";
	
	public Context context;
	public String name;
	public CursorFactory factory;
	public int version;
	
	public MyDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	
		this.context=context;
		this.name=name;
		this.factory=factory;
		this.version=version;
	
	}


		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG_LOG, "Inizio Creazione DB");
			
			String sqlRicercheRecentiCreation="CREATE TABLE RicercheRecenti(";
			sqlRicercheRecentiCreation += "_idRS INTEGER PRIMARY KEY AUTOINCREMENT,";
			sqlRicercheRecentiCreation += "Data DATETIME DEFAULT CURRENT_TIMESTAMP, Regione TEXT NOT NULL, Provincia TEXT NOT NULL, ";
			sqlRicercheRecentiCreation += "Tag TEXT NOT NULL, User TEXT NOT NULL);";
		
			db.execSQL(sqlRicercheRecentiCreation);
			Log.i(TAG_LOG, "RicercheRecentiCreation");
			
			
			String sqlMyPreferencesCreation = "CREATE TABLE MyPreferences(";
			sqlMyPreferencesCreation += "_idMP INTEGER PRIMARY KEY AUTOINCREMENT,";
			sqlMyPreferencesCreation += "Position TEXT NOT NULL,";
			sqlMyPreferencesCreation += "Firm TEXT NOT NULL,";
			sqlMyPreferencesCreation += "Description TEXT NOT NULL,";
			sqlMyPreferencesCreation += "idDBJobs INTEGER NOT NULL, DateIns DATETIME DEFAULT CURRENT_TIMESTAMP, User TEXT NOT NULL);";
	
			db.execSQL(sqlMyPreferencesCreation);	
			Log.i(TAG_LOG, "MyPreferencesCreation");
			
		
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG_LOG, "Aggiornamento non implementato");
		}
		
		

	};






