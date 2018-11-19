package com.ruixinhua.mycoursetimetable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper{
	public static final String DB_NAME =  "course.db";
	public static final String TABLE_NAME =  "course_table";
	public static final String TABLE_SETTING =  "setting_table";
	
    public MyDBHelper(Context context) {
        super(context,DB_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_timetable="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (ID TEXT PRIMARY KEY, module_code TEXT, "
        		+ "module_name TEXT, choice TEXT, day_of_week TEXT, start_end_time TEXT, location TEXT, additional_info TEXT, notification INTEGER);";
    	//String sql_timetable="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, module TEXT, ";
        String sql_settingtable="CREATE TABLE IF NOT EXISTS "+TABLE_SETTING+" (ID TEXT PRIMARY KEY, background INTEGER, "
        		+ "font_color INTEGER, font_size INTEGER, font INTEGER, font_style INTEGER);";

    	sqLiteDatabase.execSQL(sql_timetable);
    	sqLiteDatabase.execSQL(sql_settingtable);
    }
    
    //create table if the table is not exist
    public void createTable(SQLiteDatabase sqLiteDatabase) {
    	//Log.i("database", "create");
    	onCreate(sqLiteDatabase);
	}
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        //sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);		
	}

}