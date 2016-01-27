package com.dinamalarnellai.provider;

import android.content.Context;
import android.database.Cursor;

import com.dinamalarnellai.app.AppConfig;
import com.dinamalarnellai.app.DBUtil;
import com.dinamalarnellai.bo.NewsBO;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by gnanaprakasam.d on 22-12-2015.
 */
public class FavouriteHelper {
    private Context context;
    private ArrayList<NewsBO> favouriteList;

    private static FavouriteHelper instance = null;

    protected FavouriteHelper(Context context) {
        this.context = context;
    }

    public static FavouriteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FavouriteHelper(context);
        }
        return instance;
    }

    public void downloadFavouriteList() {
        try {
            NewsBO newsBO;
            DBUtil db = new DBUtil(context, AppConfig.DB_NAME,
                    AppConfig.DB_PATH);
            db.openDataBase();
            Cursor c = db.selectSQL("SELECT ID,TITLE,IMAGEURL,DESCRIPTION FROM Favourites ORDER BY ID");
            if (c != null) {
                while (c.moveToNext()) {
                    newsBO = new NewsBO();
                    newsBO.setId(c.getInt(0));
                    newsBO.setTitle(c.getString(1));
                    newsBO.setImageUrl(c.getString(2));
                    newsBO.setDescription(c.getString(3));
                    getFavouriteList().add(newsBO);
                }
                c.close();
            }
            db.closeDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean saveFavouriteDetails(int id, String title, String url, String description) {
        try {
            DBUtil db = new DBUtil(context, AppConfig.DB_NAME,
                    AppConfig.DB_PATH);
            db.openDataBase();
            String columns = "ID,TITLE,IMAGEURL,DESCRIPTION";
            String values = id + ","
                    + QT(title) + ","
                    + QT(url) + ","
                    + QT(description);

            db.insertSQL("Favourites", columns, values);
            db.closeDB();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    public boolean checkAlreadyExist(int id) {
        int count = 0;
        try {
            DBUtil db = new DBUtil(context, AppConfig.DB_NAME,
                    AppConfig.DB_PATH);
            db.openDataBase();
            Cursor c = db.selectSQL("Select count(TITLE) From Favourites where ID = "+id);
            if (c != null) {
                while (c.moveToNext()) {
                    count = c.getInt(0);
                    if (count > 0) {
                        c.close();
                        db.closeDB();
                        return true;
                    }
                }
            }
            c.close();
            db.closeDB();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public String QT(String data) {
        return "'" + data + "'";
    }

    public ArrayList<NewsBO> getFavouriteList() {
        return favouriteList;
    }

    public void setFavouriteList(ArrayList<NewsBO> favouriteList) {
        this.favouriteList = favouriteList;
    }
}
