package com.ruixinhua.mycoursetimetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//Reference:https://blog.csdn.net/shb2058/article/details/52413771
public class ModuleService {
	Context context;
	MyDBHelper dbhelper;

	public ModuleService(Context context) {
		dbhelper = new MyDBHelper(context);
	}

	public ContentValues moduleToContentValues(Module module) {
		ContentValues contentValues = new ContentValues();
		String id = module.getDay_of_week() + module.getStart_end_time();
		contentValues.put("ID", id);
		contentValues.put("module_code", module.getModule_code());
		contentValues.put("module_name", module.getModule_name());
		contentValues.put("choice", module.getChoice());
		contentValues.put("day_of_week", module.getDay_of_week());
		contentValues.put("start_end_time", module.getStart_end_time());
		contentValues.put("location", module.getLocation());
		contentValues.put("additional_info", module.getAdditional_info());
		contentValues.put("notification",module.getNotification());
		return contentValues;
	}

	public ContentValues settingToContentValues(int []setting, String id) {
		ContentValues contentValues = new ContentValues();
		String []content = {"background","font_color","font_size","font","font_style"};
		contentValues.put("ID", id);
		for (int i = 0; i < content.length; i++) {
			contentValues.put(content[i], setting[i]);
		}
		return contentValues;
	}
	// save the module object
	public void saveObject(Module module) {
		SQLiteDatabase database = dbhelper.getWritableDatabase();
		ContentValues values = moduleToContentValues(module);
		dbhelper.createTable(database);
		String id = module.getDay_of_week() + module.getStart_end_time();
		Cursor cursor = database.query(MyDBHelper.TABLE_NAME, null, "ID=?", new String[] { id }, null, null, null);
		if (cursor.getCount() == 0)
			database.insert(MyDBHelper.TABLE_NAME, null, values);
		else
			database.update(MyDBHelper.TABLE_NAME, values, "ID=?", new String[] { id });
		// database.execSQL("insert into timetable
		// (module_code,module_name,choice,day_of_week,start_time,end_time,location,"
		// + "additional_info) values(?,?,?,?,?,?,?,?)", );
		database.close();
	}
	
	public int[] getSetting(String id) {
		SQLiteDatabase database = dbhelper.getWritableDatabase();
		dbhelper.createTable(database);
		Cursor cursor = database.query(MyDBHelper.TABLE_SETTING, null, "ID=?", new String[] { id }, null, null, null);
		if (cursor.moveToFirst()) {
			cursor.move(0);
			return new int[] {cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5)};
		}
		return null;
	}
	
	public void saveSetting(int []setting,String id) {
		SQLiteDatabase database = dbhelper.getWritableDatabase();
		ContentValues values = settingToContentValues(setting,id);
		dbhelper.createTable(database);
		Cursor cursor = database.query(MyDBHelper.TABLE_SETTING, null, "ID=?", new String[] { id }, null, null, null);
		if (cursor.getCount() == 0)
			database.insert(MyDBHelper.TABLE_SETTING, null, values);
		else
			database.update(MyDBHelper.TABLE_SETTING, values, "ID=?", new String[] { id });
		// database.execSQL("insert into timetable
		// (module_code,module_name,choice,day_of_week,start_time,end_time,location,"
		// + "additional_info) values(?,?,?,?,?,?,?,?)", );
		database.close();
	}
	
	// get the module object
	public Module getObject(String id) {
		SQLiteDatabase database = dbhelper.getWritableDatabase();
		dbhelper.createTable(database);
		Cursor cursor = database.query(MyDBHelper.TABLE_NAME, null, "ID=?", new String[] { id }, null, null, null);
		if (cursor.moveToFirst()) {
			cursor.move(0);
			return new Module(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getInt(8));
		}
		return null;
	}
	
	// remove the module object
	public void removeObject(String id) {
		SQLiteDatabase database = dbhelper.getWritableDatabase();
		dbhelper.createTable(database);
		database.delete(MyDBHelper.TABLE_NAME, "ID=?", new String[] { id });
	}
}