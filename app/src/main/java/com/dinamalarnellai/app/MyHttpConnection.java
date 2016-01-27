package com.dinamalarnellai.app;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyHttpConnection {

	public static final int GET = 0;
	public static final int POST = 1;
	public static final int PUT = 2;
	public static final int DELETE = 3;
	public static final int BITMAP = 4;
	private String url = null;
	private int method;
	private JSONObject data;
	private String resultset;

	public void setResult(StringBuffer result) {
		this.result = result;
	}

	private StringBuffer result;
	private String resultdata;

	public MyHttpConnection() {
	}

	public void create(int method, String url, JSONObject data) {
		this.method = method;
		this.url = url;
		this.data = data;

	}
	public void create(int method, String url) {
		this.method = method;
		this.url = url;


	}




	public void get(String url) {
		create(GET, url, null);
	}

	public void post(String url, JSONObject data) {
		create(POST, url, data);
	}

	public void put(String url, JSONObject data) {
		create(PUT, url, data);
	}

	public void delete(String url) {
		create(DELETE, url, null);
	}

	public void bitmap(String url) {
		create(BITMAP, url, null);
	}

	public void connectMe() {
		try {
			String u = url;
			URL url = new URL(u);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(15000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			conn.setConnectTimeout(15000);
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.flush();
			writer.close();
			os.close();
			int responseCode=conn.getResponseCode();
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				resultdata = convertStreamToString(conn.getInputStream());
			}
			setResultset(resultdata.toString());


		} catch(MalformedURLException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
			setResult(null);
		}
	}

	public String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}

		return sb.toString();
	}







	public String getResultset() {
		return resultset;
	}

	public void setResultset(String result) {
		this.resultset = result;
	}

}
