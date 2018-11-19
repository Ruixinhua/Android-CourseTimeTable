package com.ruixinhua.mycoursetimetable;

public class Module {
	private String module_code;
	private String module_name;
	private String choice;
	private String day_of_week;
	private String start_end_time;
	private String location;
	private String additional_info;
	private int notification;

	public Module(String module_code, String module_name, String choice, String day_of_week, String start_end_time,
			String location, String additional_info,int notification) {
		this.module_code = module_code;
		this.module_name = module_name;
		this.choice = choice;
		this.day_of_week = day_of_week;
		this.setStart_end_time(start_end_time);
		this.location = location;
		this.additional_info = additional_info;
		this.notification = notification;
	}

	public String getModule_code() {
		return module_code;
	}

	public void setModule_code(String module_code) {
		this.module_code = module_code;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoice(String choice) {
		this.choice = choice;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAdditional_info() {
		return additional_info;
	}

	public void setAdditional_info(String additional_info) {
		this.additional_info = additional_info;
	}

	public String getStart_end_time() {
		return start_end_time;
	}

	public void setStart_end_time(String start_end_time) {
		this.start_end_time = start_end_time;
	}

	public int getNotification() {
		return notification;
	}

	public void setNotification(int notification) {
		this.notification = notification;
	}
}
