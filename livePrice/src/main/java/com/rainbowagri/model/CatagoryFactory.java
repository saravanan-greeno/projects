/**
 * Copyright (C) Greeno Tech Solutions Pvt. Ltd. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by Saravanan <saravanan.s@greeno.in>, 20 November 2014
 */
package com.rainbowagri.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class CatagoryFactory extends SQLiteOpenHelper {

	public static String DATABASE_NAME = "liveprice.db";
	public static int DATABASE_VERSION = 1;
	public static String TABLE_NAME = "CATEGORY_TABLE";
	public static String COL_ID = "ID";
	public static String COL_CATEGORY_NAME = "CATEGORY_NAME";
	public static String COL_MAINCATEGORY_NAME = "MAINCATEGORY_NAME";
	public static String COL_CORDINATOR_ID = "CORDINATOR_ID";

	public static String[] allColumns = { COL_ID,COL_CATEGORY_NAME,COL_MAINCATEGORY_NAME,
			COL_CORDINATOR_ID};
	public static String[] allColumn1 = { COL_CATEGORY_NAME,
		  };

	public CatagoryFactory(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * create the table
	 */
	public void onCreate() {
		String results=null;
		try {
		SQLiteDatabase db = this.getWritableDatabase();
		String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
				+ COL_ID + " INT PRIMARY KEY, " + COL_CATEGORY_NAME + " TEXT, "
				+ COL_CORDINATOR_ID + " TEXT," + COL_MAINCATEGORY_NAME + " TEXT)" + "";
		db.execSQL(createQuery);
		db.close();
		results="Success";
		
		} catch (Exception e) {
			results="Failure";
		}
		 
		 
	}
	/**
	 * Save the name and id in table
	 */
	public long onSave(String categoryName,String coordinatorId,String mainCategory) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(COL_CATEGORY_NAME,categoryName);
		contentValues.put(COL_CORDINATOR_ID,coordinatorId);
		contentValues.put(COL_MAINCATEGORY_NAME,mainCategory);
		
		
		System.out.println("inserted success");
		return db.insert(TABLE_NAME, null, contentValues);
	}

	/**
	 * For getting category name from table using name and id
	 */
	public ArrayList<HashMap<String, Object>> getCategoryName(
			String categoryName, String coordinatorRefId) {
	//	onCreate();
		
		System.out.println("the passign id is----"+coordinatorRefId);
		
		List<String> arrayList = new ArrayList<String>();
		ArrayList<HashMap<String, Object>> arrayMapList = new ArrayList<HashMap<String, Object>>();
		SQLiteDatabase db = this.getReadableDatabase();
		if (categoryName.equals("")) {
			
			
			
			Cursor cursor = db.query(TABLE_NAME, allColumns, COL_CORDINATOR_ID
					+ "='" + coordinatorRefId + "'", null, null, null, null);
			// cursor.moveToFirst();
			while (cursor.moveToNext()) {
				arrayList.add(cursor.getString(cursor
						.getColumnIndex(COL_CATEGORY_NAME)));
			}
		} else {
			/*Cursor cursor = db.query(TABLE_NAME, allColumns, COL_CORDINATOR_ID
					+ "='" + coordinatorRefId + "' and " + COL_CATEGORY_NAME
					+ " like '%" + categoryName + "%'", null, null, null, null);
			while (cursor.moveToNext()) {
				arrayList.add(cursor.getString(cursor
						.getColumnIndex(COL_CATEGORY_NAME)));*/
			
			//String[] columnNames = new String[] {TIMETABLE_ROWID, TIMETABLE_MODULECODE, TIMETABLE_MODULENAME, TIMETABLE_ROOM, TIMETABLE_LECTURER, TIMETABLE_TIME};
		  //  String whereClause = "TIMETABLE_MODULECODE=123"; 

		  //  return mDb.query(DATABASE_TABLE_TIMETABLE, columnNames, whereClause, null, null, null, null); 
			 //Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
			
			//Cursor cursor = db.rawQuery("SELECT allColumns FROM TABLE_NAME WHERE COL_MAINCATEGORY_NAME=?", allColumns);
			 
			/*  public Cursor getSubCategoriesQuerybyCidandBid(String Cid, int Bid) {
			        this.openDataBase();
			        Cursor c;
			         String[] asColumnsToReturn = new String[] {SECOND_COLUMN_ID, SECOND_COLUMN_IDENTITY, SECOND_COLUMN_SUBCATEGORIES};
			         c =dbSqlite.query(false, TABLE_NAME, asColumnsToReturn, COLUMN_BID + "=" + Bid + COLUMN_CID + "=" + Cid , null, null, null, null, null);
			         return c;
			         
			         COLUMN_BID + "=" + Bid + " AND " + COLUMN_CID + "=" + Cid.
			    } */
			Cursor cursor = db.query(TABLE_NAME, allColumns, COL_CORDINATOR_ID
					+ "=" + coordinatorRefId +" AND "+ COL_MAINCATEGORY_NAME
					+ "='" + categoryName+"'", null, null, null, null); 
			while (cursor.moveToNext()) {
				arrayList.add(cursor.getString(cursor
						.getColumnIndex(COL_CATEGORY_NAME)));
			}

		}

		LinkedHashSet<String> linkedHashSet = new LinkedHashSet<String>(
				arrayList);
		arrayList = new ArrayList<String>(linkedHashSet);
		Collections.sort(arrayList);
		for (int i = 0; i < arrayList.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(COL_CATEGORY_NAME, arrayList.get(i).toString());
			arrayMapList.add(map);
		}
		
		System.out.println("inserted success values"+arrayMapList);
		return arrayMapList;
	}
	
	public void deleteTable(){
		try {

		    SQLiteDatabase db = this.getWritableDatabase();
			// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		  db.delete(TABLE_NAME, null, null);
			//context.deleteDatabase(DATABASE_NAME);
		    db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
 
	public int getRecordsCount() {
		System.out.println("total values from the table===");
 	   String countQuery = "SELECT * FROM " + TABLE_NAME;
 	   SQLiteDatabase db = this.getReadableDatabase();
 	   Cursor cursor = db.rawQuery(countQuery, null);
 	   int count = 0;
 	   try {
 	      if (cursor.moveToFirst()) {
 	         count = cursor.getCount();
 	      }
 	     System.out.println("total values from the table==="+count);
 	      return count;
 	   }
 	   finally {
 	      if (cursor != null) {
 	         cursor.close();
 	      }
 	   }
 	}
 
	
	
	/**
	 * Update the category using new category and id
	 */
	public String updateCategory(String newCategory, String oldCategory,
			String coordinatorId) {
		String status;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(COL_CATEGORY_NAME, newCategory);
			db.update(TABLE_NAME, contentValues, COL_CATEGORY_NAME + "='"
					+ oldCategory + "' and " + COL_CORDINATOR_ID + "='"
					+ coordinatorId + "'", null);
			status = "Success";

		} catch (Exception e) {
			status = "Failure";
		}
		return status;
	}
	
	public ArrayList<Cursor> getData(String Query){
		//get writable database
		SQLiteDatabase sqlDB = this.getWritableDatabase();
		String[] columns = new String[] { "mesage" };
		//an array list of cursor to save two cursors one has results from the query 
		//other cursor stores error message if any errors are triggered
		ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
		MatrixCursor Cursor2= new MatrixCursor(columns);
		alc.add(null);
		alc.add(null);
		
		
		try{
			String maxQuery = Query ;
			//execute the query results will be save in Cursor c
			Cursor c = sqlDB.rawQuery(maxQuery, null);
			

			//add value to cursor2
			Cursor2.addRow(new Object[] { "Success" });
			
			alc.set(1,Cursor2);
			if (null != c && c.getCount() > 0) {

				
				alc.set(0,c);
				c.moveToFirst();
				
				return alc ;
			}
			return alc;
		} catch(SQLException sqlEx){
			Log.d("printing exception", sqlEx.getMessage());
			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		} catch(Exception ex){

			Log.d("printing exception", ex.getMessage());

			//if any exceptions are triggered save the error message to cursor an return the arraylist
			Cursor2.addRow(new Object[] { ""+ex.getMessage() });
			alc.set(1,Cursor2);
			return alc;
		}

		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
