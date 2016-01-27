package com.dinamalarnellai.app;

import android.util.Log;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Commons {
	public static int DATE_TIME = 5;
	public static int DATE = 6;
	
	public static void print(String message) {
		if (AppConfig.DEBUG)
			Log.d(AppConfig.LOG, message);
	}

	public static void print(int message) {
		print(message + "");
	}

	public static void printException(String message, Throwable e) {
		Log.e(AppConfig.LOG, message, e);
	}
	
	public static void printException(Throwable e) {
		Log.e(AppConfig.LOG, "~", e);
	}

	public static void printException(String message) {
		Log.e(AppConfig.LOG, message);
	}

	public static void printInformation(String message) {
		Log.d(AppConfig.LOG, message);
	}

	public static void printInformation(String message, Throwable e) {
		Log.d(AppConfig.LOG, message, e);
	}

	public static void printWarning(String message, Throwable e) {
		Log.w(AppConfig.LOG, message, e);
	}

	public static void printWarning(String message) {
		Log.w(AppConfig.LOG, message);
	}
	/**
	 * @param dateFormat
	 * @return formatted date
	 */
	public static String now(int dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = null;
		if (DATE_TIME == dateFormat) 
			sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
		 else if (DATE == dateFormat) 
			sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		
		return sdf.format(cal.getTime());
	}
}
