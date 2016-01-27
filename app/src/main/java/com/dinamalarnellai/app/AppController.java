package com.dinamalarnellai.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.dinamalarnellai.bo.CategoryBO;
import com.dinamalarnellai.bo.NewsBO;
import com.dinamalarnellai.provider.FavouriteHelper;


public class AppController extends Application {

	private static AppController mInstance;
	public static final String TAG = AppController.class.getSimpleName();
	private Activity ctx;
	public String mName, mobilenumber, emailid;

	public String userNameTemp, passwordTemp, userTokenKey;
	public static final String JSON_MASTER_KEY = "TableName";
	public static final String JSON_FIELD_KEY = "ColumnList";
	public static final String JSON_DATA_KEY = "Data";
	public static final String JSON_NEXT_KEY = "Next";
	public static final String ERROR_CODE = "ErrorCode";
	public static final String JSON_INCORRECT_PWD_KEY = "Message";

	public static final String TAG_JSON_OBJ = "json_obj_req";
	public String mSelectedServiceName;
	public String mSelectedSurveyName;
	public String mGcmRegID;
	public static boolean checkGoolgePlayService = true;
	public FavouriteHelper favouriteHelper;
	private ArrayList<NewsBO> newsList;

	public ArrayList<NewsBO> getNewsList() {
		return newsList;
	}

	public void setNewsList(ArrayList<NewsBO> newsList) {
		this.newsList = newsList;
	}

	public AppController() {

		favouriteHelper = FavouriteHelper.getInstance(this);
	}

	public void setContext(Activity ctx) {
		this.ctx = ctx;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	/*
	 * public void showAlert(String msg, int id) { final int idd = id;
	 * AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
	 * builder.setMessage(msg); builder.setCancelable(false);
	 * builder.setPositiveButton("Ok", new OnClickListener() {
	 * 
	 * public void onClick(DialogInterface dialog, int which) { if (idd ==
	 * AppConfig.NOTIFY_NOT_USEREXIST) { Intent intent = new Intent(ctx,
	 * RegisterActivity.class); startActivity(intent); ctx.finish();
	 * 
	 * } else if (idd == 10) { ctx.finish();
	 * 
	 * } }
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub
	 * 
	 * } });
	 * 
	 * builder.show(); }
	 */

	public void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception exc) {
		}
	}

	public String getApplicationVersionNumber() {
		int versionNumber = 0;
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			versionNumber = pinfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionNumber + "";
	}

	int responceMessage, deleteInfo;
	Handler handler;

	public String downloadCategories(String url)
	{
		String data = new String();
		try
		{
			// Connection Server
			MyHttpConnection http = new MyHttpConnection();
			http.create(MyHttpConnection.POST, url);
			http.connectMe();
			String result = http.getResultset();

		}catch (Exception e){
			e.printStackTrace();
		}

		return data;
	}




	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public String getIMEINumber() {
		String deviceId = "";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) this
					.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = telephonyManager.getDeviceId();
		} catch (Exception e) {
			return "0";
		}
		if (deviceId == null)
			return "0";
		else
			return deviceId;
	}


}