package com.dinamalarnellai.views;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinamalarnellai.app.AppConfig;
import com.dinamalarnellai.app.AppController;
import com.dinamalarnellai.app.MyHttpConnection;
import com.dinamalarnellai.bo.NewsBO;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import app.dinamalarnellai.com.news.R;

public class NewsListActivity extends BaseActivity {

    private ArrayList<NewsBO> newsList;
    private Toolbar mToolbar;
    private TextView tvPage;
    private AppController controller;
    int categoryId;
    int currentPosition = 0;
    private String fromScreen;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String name;
    private String webURl, categoryName;
    private int pos = 0;
    private ListView lvwplist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayShowHomeEnabled(true);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        controller = (AppController) getApplicationContext();
        controller.setContext(this);
        lvwplist = (ListView) findViewById(R.id.lvwplist);
        lvwplist.setCacheColorHint(0);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            categoryId = extras.getInt("categoryid");
            categoryName = extras.getString("categoryName");
            fromScreen = extras.getString("SubCategory", "CategoryScreen");
        }
        getSupportActionBar().setTitle(categoryName);
        if (fromScreen.equals("SubCategoryScreen"))
            name = AppConfig.SUB_CATEGORY;
        else
            name = AppConfig.CATEGORY;

        newsList = new ArrayList<>();
        if (controller.isOnline()) {
            new LoadCategoryItems().execute();
        } else
            showAlert(0);



    }

     @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_news, menu);

       return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId())
       {
           case android.R.id.home:
               finish();
               break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();

    }


    class LoadCategoryItems extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialogue;
        private String result;
        HashMap<String, String> hashMap;
        String pageType;

        @Override
        protected String doInBackground(String... params) {
            try {
                if (fromScreen.equals("SubCategoryScreen"))
                    pageType = AppConfig.REQUEST_NEWS_SUB_CATEGORY;
                else
                    pageType = AppConfig.REQUEST_NEWS_CATEGORY;

                MyHttpConnection http = new MyHttpConnection();
                http.create(MyHttpConnection.POST, AppConfig.REQUEST_BASE_URL + pageType + "/" + categoryId);
                http.connectMe();
                String result = http.getResultset();

                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    NewsBO news = new NewsBO();
                    news.setTitle(jsonObj.getString("title"));
                    news.setImageUrl(jsonObj.getString("image"));
                    news.setDescription(jsonObj.getString("description"));
                    news.setDateTime(jsonObj.getString("updatedTime"));
                    news.setWebUrl(jsonObj.getString("weburl"));
                    newsList.add(news);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialogue != null)
                progressDialogue.dismiss();
            //create and attach adapter to flipper view
            if (newsList.size() > 0) {

                MyAdapter mSchedule = new MyAdapter(newsList);
                lvwplist.setAdapter(mSchedule);
                lvwplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(NewsListActivity.this, NewsActivity.class);
                        intent.putExtra("currentposition", position);
                        intent.putExtra("categoryName", categoryName);
                        controller.setNewsList(newsList);
                        Bundle bindAnimation =
                                ActivityOptions.makeCustomAnimation(NewsListActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                        startActivity(intent, bindAnimation);

                    }
                });

            } else {
                showAlert(1);
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialogue = ProgressDialog.show(NewsListActivity.this, "Please wait ...", "Downloading ...", true);
        }


    }


    private void showAlert(int id) {
        AlertDialog.Builder alertDialogBuilder;
        AlertDialog alertDialog;

        switch (id) {
            case 0:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(getResources().getString(R.string.no_network_connection));
                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                finish();
                                try {
                                    android.os.Process
                                            .killProcess(android.os.Process.myPid());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case 1:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(getResources().getString(R.string.news_not_found));
                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                finish();

                            }
                        });

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                break;
            default:
                break;
        }

    }



    class ViewHolder {
        NewsBO mNewsBO;
        TextView tvTitle;
        TextView tvDate;
        ImageView tvImage;
    }

    private class MyAdapter extends ArrayAdapter<NewsBO> {
        private ArrayList<NewsBO> items;

        public MyAdapter(ArrayList<NewsBO> mylist) {
            super(NewsListActivity.this, R.layout.row_history, mylist);
            this.items = mylist;
        }

        public NewsBO getItem(int position) {
            return items.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(NewsListActivity.this
                        .getBaseContext());
                convertView = (View) inflater.inflate(R.layout.row_history,
                        null);

                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.tv_newstitle);
                holder.tvDate = (TextView) convertView
                        .findViewById(R.id.tv_newsdate);
                holder.tvImage = (ImageView) convertView
                        .findViewById(R.id.img_news);
             /*   convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                       // String selectedServiceId = holder.mNewsBO.getId();

                        Intent intent = new Intent(NewsListActivity.this, NewsActivity.class);
                        //intent.putExtra("newslist", holder.mNewsBO);
                        intent.putExtra("currentposition", position);
                        controller.setNewsList(newsList);
                        Bundle bindAnimation =
                                ActivityOptions.makeCustomAnimation(NewsListActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
                        startActivity(intent, bindAnimation);


                    }
                });
             */   convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mNewsBO = items.get(position);

            holder.tvTitle.setText(holder.mNewsBO.getTitle());
            Picasso.with(NewsListActivity.this).load(holder.mNewsBO.getImageUrl()).into(holder.tvImage);
            holder.tvDate.setText(holder.mNewsBO.getDateTime());
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            //get current date time with Calendar()
            Calendar cal = Calendar.getInstance();
            //System.out.println(format.format(cal.getTime()));

            String dateStart = holder.mNewsBO.getDateTime().substring(0, 16);
            String dateStop = format.format(cal.getTime());

//HH converts hour in 24 hours format (0-23), day calculation
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(dateStart);
                d2 = format.parse(dateStop);
            }catch (ParseException e)
            {
                e.printStackTrace();
            }

//in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");
            if(diffDays <= 0) {
                holder.tvDate.setText(diffHours + "h");
            }else
                holder.tvDate.setText(diffDays+"d");

            return convertView;
        }
    }


}
