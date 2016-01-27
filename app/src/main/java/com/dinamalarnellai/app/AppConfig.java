package com.dinamalarnellai.app;

public class AppConfig {
	public static final boolean DEBUG = Boolean.parseBoolean("true");
	public static String LANGUAGE = "ta";
	public static String DB_NAME = "News.sqlite";
	public static String DB_PATH = "/data/data/com.servezzibu.views/databases/";
	public static String CLIENT = "DinaMalar";
	public static String LOG = "com.app.DinaMalar";
	/** The App Name */
	public static String SD = "DinaMalar";
	/** The Constant REQUEST_BASE_URL. */
	public static final String REQUEST_BASE_URL = "http://dinamalarnellai.com/api/";
	/** The Constant REQUEST_BASE_METHOD. */
	public static final String REQUEST_INSTALL ="install";
	public static final String REQUEST_FLASH_NEWS="flashnews";
	public static final String REQUEST_CATEGORY = "category";
	public static final String REQUEST_SUB_CATEGORY =  "subcategories";
	public static final String REQUEST_NEWS_CATEGORY =  "get_news_category";
	public static final String REQUEST_NEWS_SUB_CATEGORY = "get_news_subcategory";
	public static final String CATEGORY = "category/";
	public static final String SUB_CATEGORY ="subcategory/";

}
