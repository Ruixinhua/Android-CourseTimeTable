package com.ruixinhua.mycoursetimetable;

import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

//Reference:https://www.cnblogs.com/xiaoluo501395377/p/3419398.html
public class AddModuleListener implements OnClickListener, OnItemClickListener {
	private Context context;
	private List<Map<String, Object>> courses;
	private SimpleAdapter adapter;
	private RadioGroup class_type;
	private RadioButton lecture;
	private RadioButton practical;
	private EditText[] module_text;
	// store the data
	private ArrayAdapter<String> day_adapter;
	private ArrayAdapter<String> time_adapter;
	private ArrayAdapter<String> noti_adapter;
	private Spinner choic_of_day;
	private Spinner choic_of_time;
	private Spinner notification_spinner;
	private ModuleService ms;
	private String type;
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

	// here is the day of a week
	private static final String[] week_day = MainActivity.week_day;
	private String day;
	// here is the time that can be chose, because it is only designed for myself.
	private static final String[] start_end_time = MainActivity.start_end_time;
	private String time;
	private static final String[] notification_text = { "0 minute", "5 minutes", "10 minutes", "15 minutes" };
	private static final int[] notification_time = { 0, 5, 10, 15 };
	private int notification;

	public AddModuleListener(Context ct, List<Map<String, Object>> c, SimpleAdapter a) {
		this.context = ct;
		this.courses = c;
		this.adapter = a;
		ms = new ModuleService(context);
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	}

	private String getText(EditText e) {
		return e.getText().toString().trim();
	}

	private EditText[] getEditTexts(View view) {
		return new EditText[] { (EditText) view.findViewById(R.id.module_code),
				(EditText) view.findViewById(R.id.full_module_name), (EditText) view.findViewById(R.id.location),
				(EditText) view.findViewById(R.id.additional_info) };
	}

	private int getStringIndex(String string, String[] strings) {
		for (int i = 0; i < strings.length; i++) {
			if (string.equals(strings[i]))
				return i;
		}
		return -1;
	}

	private long getMilliseconds(int d, int h, int m, int s, int mill) {
		long second = 1000;
		long minute = 60 * second;
		long hour = 60 * minute;
		long day_mill = 24 * hour;
		return d * day_mill + h * hour + m * minute + s * second + mill;
	}

	private long getDiffTime(int day_in_week, String start_time, int notify) {
		long week_time = getMilliseconds(7, 0, 0, 0, 0);
		Calendar calendar = Calendar.getInstance();
		int current_week_day = (calendar.get(Calendar.DAY_OF_WEEK) - 2) < 0 ? 6
				: calendar.get(Calendar.DAY_OF_WEEK) - 2;
		long current_milli_time = getMilliseconds(current_week_day, calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
		int alarm_hour = Integer.parseInt(start_time.split(":")[0]);
		int alarm_minute = Integer.parseInt(start_time.split(":")[1]) - notify;
		long alarm_milli_time = getMilliseconds(day_in_week, alarm_hour, alarm_minute, 0, 0);
		long diff_time = (alarm_milli_time - current_milli_time) > 0 ? alarm_milli_time - current_milli_time
				: alarm_milli_time + week_time - current_milli_time;
		return diff_time;
	}

	private void showItem(View view, final int position, Module module) {
		module_text = getEditTexts(view);
		class_type = (RadioGroup) view.findViewById(R.id.lecture_or_practical);
		lecture = (RadioButton) view.findViewById(R.id.lecture);
		practical = (RadioButton) view.findViewById(R.id.practical);
		if (module != null) {
			// Log.i(module_text[0].getText().toString(), module_text[1].toString());
			String[] module_detail = { module.getModule_code(), module.getModule_name(), module.getLocation(),
					module.getAdditional_info() };
			for (int i = 0; i < module_text.length; i++) {
				// Log.i(module_text[i].getText().toString(), "text");
				// Log.i(module_detail[i], "detail");
				module_text[i].setText(module_text[i].getText().toString() + module_detail[i]);
			}
			type = module.getChoice();
			if (type.equals("Lecture"))
				class_type.check(lecture.getId());
			else
				class_type.check(practical.getId());
		} else {
			class_type.check(lecture.getId());
			type = "Lecture";
		}
		choic_of_day = (Spinner) view.findViewById(R.id.day_of_week);
		choic_of_time = (Spinner) view.findViewById(R.id.time);
		notification_spinner = (Spinner) view.findViewById(R.id.notification);
		// Reference:https://blog.csdn.net/u010078640/article/details/27359183
		// make the content connect to the adapter
		day_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, week_day);
		time_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, start_end_time);
		noti_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, notification_text);
		// add adapter
		choic_of_day.setAdapter(day_adapter);
		choic_of_time.setAdapter(time_adapter);
		notification_spinner.setAdapter(noti_adapter);
		// set the current time
		if (module != null) {
			int d = getStringIndex(module.getDay_of_week(), week_day);
			int t = getStringIndex(module.getStart_end_time(), start_end_time);
			choic_of_day.setSelection(d);
			choic_of_time.setSelection(t);
			notification_spinner.setSelection(module.getNotification() / 5);
		} else {
			choic_of_day.setSelection(position % 8);
			choic_of_time.setSelection(position / 8);
			notification_spinner.setSelection(0);
			notification = notification_time[0];
		}
		// add class type listener
		class_type.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (group.getCheckedRadioButtonId()) {
				case R.id.lecture:
					type = lecture.getText().toString();
					break;
				case R.id.practical:
					type = practical.getText().toString();
					break;
				default:
					break;
				}
			}
		});
		// add spinner listener of day
		choic_of_day.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				day = week_day[arg2];
				// set the current choice
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (position == 0) {
					// default value is the first element
					day = week_day[0];
					// set the current choice
					arg0.setVisibility(View.VISIBLE);
				}

			}

		});
		// add spinner listener of time
		choic_of_time.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				time = start_end_time[arg2];
				// set the current choice
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if (position == 0) {
					// default value is the first element
					time = start_end_time[0];
					// set the current choice
					arg0.setVisibility(View.VISIBLE);
				}
			}

		});
		// add spinner listener of notification
		notification_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				notification = notification_time[arg2];
				// set the current choice
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				notification = 0;
				arg0.setVisibility(View.VISIBLE);
			}

		});

	}

	private void updateCourseWidget() {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
		if (CourseWidget.app_widget_id != -1)
			CourseWidget.updateAppWidget(context, appWidgetManager, CourseWidget.app_widget_id);
	}

	private void saveModule() {
		Module module = new Module(getText(module_text[0]), getText(module_text[1]), type, day, time,
				getText(module_text[2]), getText(module_text[3]), notification);
		ms.saveObject(module);
		int d = getStringIndex(module.getDay_of_week(), week_day) + 1;
		int t = getStringIndex(module.getStart_end_time(), start_end_time) + 1;
		int position = d + t * 8;
		courses.remove(position);
		Module m = ms.getObject(day + time);
		Map<String, Object> temp = new HashMap<String, Object>();
		temp.put("module_code", module.getModule_code());
		temp.put("first_letter", module.getChoice().charAt(0));
		temp.put("location", module.getLocation());
		courses.add(position, temp);
		adapter.notifyDataSetChanged();
		updateCourseWidget();
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("id", module.getDay_of_week() + module.getStart_end_time());
		intent.putExtra("position", position);
		alarmIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		long diff_time = getDiffTime(d - 1, module.getStart_end_time().split("-")[0], module.getNotification());
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + diff_time,
				getMilliseconds(7, 0, 0, 0, 0), alarmIntent);
		// TODO test, remove
		/*
		 * Toast.makeText(context, "diff day: " + d + "Time: " + diff_time,
		 * Toast.LENGTH_SHORT).show(); Calendar calendar = Calendar.getInstance(); Date
		 * currentTime = calendar.getTime(); int current_week_day =
		 * (calendar.get(Calendar.DAY_OF_WEEK) - 2) < 0 ? 6 :
		 * calendar.get(Calendar.DAY_OF_WEEK) - 2; Log.i("data",
		 * currentTime.toString()); Log.i("week", current_week_day+"");
		 */
	}

	private void removeModule(int position, String ID) {
		courses.remove(position);
		Map<String, Object> temp = new HashMap<String, Object>();
		courses.add(position, temp);
		ms.removeObject(ID);
		adapter.notifyDataSetChanged();
		updateCourseWidget();
		Intent intent = new Intent(context, AlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmMgr.cancel(alarmIntent);

	}

	private void addItem(View view, final int position) {
		// Log.i("item", "add");
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("Add Module");
		// load a xml file as an object of View through LayoutInflater
		view = LayoutInflater.from(context).inflate(R.layout.course_detail, null);
		// set the xml file as the content of the dialog
		builder.setView(view);
		showItem(view, position, null);
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Add the information to the database
				saveModule();
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}

	@Override
	public void onClick(View v) {
		addItem(v, 0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
		//Log.i("item", "click");
		if (position < 8 || position % 8 == 0)
			return;
		final String ID = week_day[position % 8 - 1] + start_end_time[position / 8 - 1];
		final Module module = ms.getObject(ID);
		if (module != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle("Module Details");
			view = LayoutInflater.from(context).inflate(R.layout.course_detail, null);
			builder.setView(view);
			showItem(view, position, module);
			// Log.i("location", week_day[position % 8 - 1] + start_end_time[position / 8 -
			// 1]);
			builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeModule(position, ID);
					saveModule();
				}
			});
			builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlertDialog.Builder remove_confirm = new AlertDialog.Builder(context);
					remove_confirm.setIcon(R.drawable.ic_launcher);
					remove_confirm.setTitle("Confirm Remove");
					remove_confirm.setMessage("Are you sure remove the module " + module.getModule_code());
					remove_confirm.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeModule(position, ID);
						}
					});
					remove_confirm.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					remove_confirm.show();
				}
			});
			builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.show();
		} else {
			addItem(view, position - 9);
		}
	}
}
