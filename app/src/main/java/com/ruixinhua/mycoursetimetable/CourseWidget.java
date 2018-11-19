package com.ruixinhua.mycoursetimetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

// Reference:https://www.cnblogs.com/joy99/p/6346829.html
/**
 * Implementation of App Widget functionality.
 */
public class CourseWidget extends AppWidgetProvider {

	public static int app_widget_id = -1;

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		app_widget_id = appWidgetId;
		// Construct the RemoteViews object
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.course_widget_provider);
		int[] course_id = { R.id.course0, R.id.course1, R.id.course2, R.id.course3, R.id.course4, R.id.course5 };
		ModuleService ms = new ModuleService(context);
		Calendar calendar = Calendar.getInstance();
		int current_week_day = (calendar.get(Calendar.DAY_OF_WEEK) - 2) < 0 ? 6
				: calendar.get(Calendar.DAY_OF_WEEK) - 2;
		int[] start_time = { 8 * 60, 9 * 60 + 55, 13 * 60 + 30, 15 * 60 + 25, 18 * 60 };
		int current_time_total = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
		int current_start_time = 0;
		// Log.i("current time", current_time_total+"");
		for (int i = 0; i < start_time.length; i++) {
			if(start_time[i] > current_time_total ) break;
			current_start_time = i;
		}
		Log.i("current time", current_start_time+"");
		List<Module> modules_info = new ArrayList<Module>();
		for (int i = current_start_time + 1; i < MainActivity.start_end_time.length; i++) {
			Module module = ms.getObject(MainActivity.week_day[current_week_day] + MainActivity.start_end_time[i]);
			if (module != null)
				modules_info.add(module);
		}
		for (int i = 0; i < modules_info.size(); i++) {
			String content = modules_info.get(i).getModule_name() + ": " + modules_info.get(i).getChoice().charAt(0)
					+ " at " + modules_info.get(i).getStart_end_time() + " in " + modules_info.get(i).getLocation();
			views.setTextViewText(course_id[i], content);
		}
		for (int i = modules_info.size(); i < course_id.length; i++) {
			views.setTextViewText(course_id[i], "");
		}
		// Log.i("app id", appWidgetId + "");
		// Instruct the widget manager to update the widget
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onEnabled(Context context) {
		// Enter relevant functionality for when the first widget is created
	}

	@Override
	public void onDisabled(Context context) {
		// Enter relevant functionality for when the last widget is disabled
	}
}