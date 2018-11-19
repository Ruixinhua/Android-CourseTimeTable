package com.ruixinhua.mycoursetimetable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends Activity {
	private GridView course_table;
	private CourseAdapter adapter;
	private List<Map<String, Object>> courses;
	private Button add_module_button;
	private Button preferences_button;
	private ModuleService ms;
	// here is the day of a week
	protected static final String[] week_day = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
			"Sunday" };
	// here is the time that can be chose, because it is only designed for myself.
	protected static final String[] start_end_time = { "8:00-9:35", "9:55-11:30", "13:30-15:05", "15:25-17:00",
			"18:00-19:35" };
	private final int[][] preferences = { { R.drawable.bgwhite, R.drawable.bgblue, R.drawable.bgpink },
			{ Color.BLUE, Color.BLACK, Color.RED }, { 10, 13, 16 },
			{ Typeface.BOLD, Typeface.ITALIC, Typeface.NORMAL } };
	private final Typeface[] font_styles = { Typeface.DEFAULT, Typeface.SANS_SERIF, Typeface.SERIF };
	private final int[][] preferences_radio = { { R.id.bd_white, R.id.bd_black, R.id.bd_red },
			{ R.id.fc_white, R.id.fc_black, R.id.fc_red }, { R.id.low, R.id.medium, R.id.high },
			{ R.id.bold, R.id.italic, R.id.normal }, { R.id.default_font, R.id.sans, R.id.serif } };
	private int[] current_setting;
	private Typeface font_style;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		add_module_button = (Button) findViewById(R.id.add_module);
		preferences_button = (Button) findViewById(R.id.preferences);
		course_table = (GridView) findViewById(R.id.course_table_gridview);
		ms = new ModuleService(MainActivity.this);
		// Log.i("course", "test");
		courses = new ArrayList<Map<String, Object>>();

		initData();

		// Log.i("data", "test");
		String[] from = { "module_code", "first_letter", "location" };
		int[] to = { R.id.module_code, R.id.first_letter, R.id.location };
		adapter = new CourseAdapter(MainActivity.this, courses, R.layout.course_item, from, to);
		AddModuleListener moduleListener = new AddModuleListener(this, courses, adapter);
		setPrefences();
		// Log.i("data", "test");
		course_table.setOnItemClickListener(moduleListener);
		add_module_button.setOnClickListener(moduleListener);
		preferences_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Log.i("preferences", "setting");
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("Preferences");
				// load a xml file as an object of View through LayoutInflater
				View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.preferences_layout, null);
				// set the xml file as the content of the dialog
				builder.setView(view);
				RadioGroup[] setting_type = { (RadioGroup) view.findViewById(R.id.background),
						(RadioGroup) view.findViewById(R.id.font_color), (RadioGroup) view.findViewById(R.id.font_size),
						(RadioGroup) view.findViewById(R.id.font), (RadioGroup) view.findViewById(R.id.font_style) };
				for (int i = 0; i < setting_type.length; i++) {
					setting_type[i].setOnCheckedChangeListener(new PreferencesSetting(i));
				}
				// Log.i("setting type", "setting");
				builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setPrefences();
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				builder.show();
			}
		});
		// adapter.notifyDataSetChanged();
	}

	private void setPrefences() {
		MainActivity.this.getWindow().setBackgroundDrawable(getResources().getDrawable(preferences[0][current_setting[0]]));
		course_table.setAdapter(adapter);
		ms.saveSetting(current_setting, "user");
	}

	private void initData() {
		current_setting = ms.getSetting("user");
		if (current_setting == null) {
			// default setting
			current_setting = new int[] { 1, 1, 1, 2, 0 };
		}
		font_style = font_styles[current_setting[4]];
		for (int i = 0; i < start_end_time.length + 1; i++) {
			for (int j = 0; j < week_day.length + 1; j++) {
				Map<String, Object> temp = new HashMap<String, Object>();
				if (i == 0 && j == 0) {
					// Log.i("time", "test");
					temp.put("location", "");
				} else if (i == 0 && j != 0) {
					// Log.i("day", "test");
					temp.put("location", week_day[j - 1].substring(0, 2));
				} else if (i != 0 && j == 0) {
					// Log.i("start", "test");
					String[] temp_time = start_end_time[i - 1].split("-");
					temp.put("module_code", temp_time[0]);
					temp.put("first_letter", "-");
					temp.put("location", temp_time[1]);
				} else {
					// Log.i("module", "test");
					Module module = ms.getObject(week_day[j - 1] + start_end_time[i - 1]);
					// Log.i("module", "get");
					String information = "";
					if (module == null) {
						// Log.i("module", "null");
						temp.put("module_code", information);
						temp.put("first_letter", information);
						temp.put("location", information);
					} else {
						// Log.i("module", module.getModule_code());
						// Log.i("module", module.getChoice());
						// Log.i("module", module.getLocation());
						temp.put("module_code", module.getModule_code());
						temp.put("first_letter", module.getChoice().charAt(0));
						temp.put("location", module.getLocation());
					}
				}
				courses.add(temp);
			}
		}
	}

	private class CourseAdapter extends SimpleAdapter {

		public CourseAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
				int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			TextView[] item_text = { (TextView) view.findViewById(R.id.module_code),
					(TextView) view.findViewById(R.id.first_letter), (TextView) view.findViewById(R.id.location) };
			if (position < 8) {
				item_text[0].setVisibility(View.GONE);
				item_text[1].setVisibility(View.GONE);
				item_text[2].setTextColor(preferences[1][current_setting[1]]);
				item_text[2].setTextSize(preferences[2][current_setting[2]] * 2);
				item_text[2].setTypeface(font_style, preferences[3][current_setting[3]]);
			} else {
				for (int i = 0; i < item_text.length; i++) {
					// Log.i("text size", current_setting[2]+"");
					item_text[i].setTextColor(preferences[1][current_setting[1]]);
					item_text[i].setTextSize(preferences[2][current_setting[2]]);
					item_text[i].setTypeface(font_style, preferences[3][current_setting[3]]);
				}

			}
			view.setBackground(getResources().getDrawable(preferences[0][current_setting[0]]));
			return view;
		}

	}

	private class PreferencesSetting implements OnCheckedChangeListener {
		private int index;

		public PreferencesSetting(int i) {
			index = i;
		}

		private int findIndex(int[] array, int i) {
			for (int j = 0; j < array.length; j++) {
				if (i == array[j])
					return j;
			}
			return -1;
		}

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			current_setting[index] = findIndex(preferences_radio[index], group.getCheckedRadioButtonId());
			if(index == 4) {
				font_style = font_styles[current_setting[index]];
			}
		}

	}
}
