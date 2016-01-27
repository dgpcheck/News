package com.dinamalarnellai.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aphidmobile.flip.FlipViewController;
import com.dinamalarnellai.app.AppConfig;
import com.dinamalarnellai.app.AppController;
import com.dinamalarnellai.app.MyHttpConnection;
import com.dinamalarnellai.bo.NewsBO;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.dinamalarnellai.com.news.R;


public class NewsActivity extends BaseActivity {

    private FlipViewController flipViewController;
    private FlipperAdapter adapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        controller = (AppController) getApplicationContext();
        controller.setContext(this);

        //getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.dailymalar));
        //  mToolbar = (Toolbar)  findViewById(R.id.toolbar);
        //  setSupportActionBar(mToolbar);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
                GravityCompat.START);
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
                GravityCompat.END);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(NewsActivity.this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.mipmap.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.ok, /* "open drawer" description for accessibility */
                R.string.close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
              /* getSupportActionBar()
                        .setTitle(bmodel.mSelectedActivityName);*/
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {

               /* ((ActionBarActivity) getActivity()).getSupportActionBar()
                        .setTitle(getResources().getString(R.string.filter));*/
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.closeDrawer(GravityCompat.END);

        flipViewController = (FlipViewController) findViewById(R.id.flip_view);
        tvPage = (TextView) findViewById(R.id.tv_page1);
        Bundle extras = getIntent().getExtras();

       /* if (extras != null) {
            categoryId = extras.getInt("categoryid");
            categoryName = extras.getString("categoryName");
            fromScreen = extras.getString("SubCategory", "CategoryScreen");
        }

        if (fromScreen.equals("SubCategoryScreen"))
            name = AppConfig.SUB_CATEGORY;
        else
            name = AppConfig.CATEGORY;

        newsList = new ArrayList<>();
        if (controller.isOnline()) {
            new LoadCategoryItems().execute();
        } else
            showAlert(0);

*/
        if (extras != null) {
            categoryId = extras.getInt("currentposition");
            categoryName = extras.getString("categoryName");
            // fromScreen = extras.getString("SubCategory", "CategoryScreen");
            pos = categoryId;
        }
        if (controller.getNewsList().size() > 0) {
            adapter = new FlipperAdapter(NewsActivity.this, controller.getNewsList());
            flipViewController.setAdapter(adapter);
            flipViewController.setSelection(categoryId);

        } else {
            showAlert(1);
        }


    }

    @Override
    public void onResume() {
        super.onResume();


        flipViewController.setOnViewFlipListener(new FlipViewController.ViewFlipListener() {
            @Override
            public void onViewFlipped(View view, int position) {
                // calculateDetails(position);
                pos = position;
                //categoryId = position;

            }
        });
    }

   /* private void calculateDetails(int position) {
        int fromProduct = 0;
        int toProduct = 0;
        int startPage = 0;
        try {
            startPage = position + 1;
            currentPosition = startPage;

            tvPage.setText(startPage + " / " + newsList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

   */


   /* @Override
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
            case R.id.action_next:
                newsFilterClickedFragment();
                break;

            case R.id.menu_item_share:
                try{
                    NewsBO bb= newsList.get(pos);
                    String shareMessage = "http://dinamalarnellai.com/web/news/"+categoryId;
                    Intent shareIntent =
                            new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
         *//*   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Insert Subject Here");*//*
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                            bb.getWebUrl());
                    startActivity(Intent.createChooser(shareIntent,
                            "Share with..,"));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/

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
                adapter = new FlipperAdapter(NewsActivity.this, newsList);
                flipViewController.setAdapter(adapter);


            } else {
                showAlert(1);
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialogue = ProgressDialog.show(NewsActivity.this, "Please wait ...", "Downloading ...", true);
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

    class FlipperAdapter extends ArrayAdapter<NewsBO> {

        private AppCompatActivity appCompatActivity;
        private List<NewsBO> newsList;


        public FlipperAdapter(AppCompatActivity appCompatActivity, List<NewsBO> strings) {
            super(appCompatActivity, R.layout.item_page, strings);
            this.newsList = strings;
            this.appCompatActivity = appCompatActivity;
        }

        @Override
        public int getCount() {
            return newsList.size();
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) appCompatActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            // If holder not exist then locate all view from UI file.
            if (convertView == null) {
                // inflate UI from XML file
                convertView = inflater.inflate(R.layout.item_page, parent, false);
                // get all UI view
                holder = new ViewHolder(convertView);
                // set tag for holder
                convertView.setTag(holder);
                //  position = categoryId;
                // pos = position;
            } else {
                // if holder created, get tag from view
                holder = (ViewHolder) convertView.getTag();
            }

            holder.newsBO = newsList.get(position);

            holder.tvTitle.setText(holder.newsBO.getTitle());
            Picasso.with(appCompatActivity).load(holder.newsBO.getImageUrl()).into(holder.imgNewsImage);
            holder.tvDescription.setText(Html.fromHtml(holder.newsBO.getDescription()));
            holder.tvDateTime.setText(convertDate(holder.newsBO.getDateTime()));

            holder.tvPageNo.setText(position + 1 + " / " + newsList.size() + "");

            holder.imgViewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                }
            });
            holder.imgViewNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos1 = pos + 1;
                    if (pos1 != newsList.size()) {
                        pos = pos1;
                        holder.newsBO = newsList.get(pos1);

                        holder.tvTitle.setText(holder.newsBO.getTitle());
                        Picasso.with(appCompatActivity).load(holder.newsBO.getImageUrl()).into(holder.imgNewsImage);
                        holder.tvDescription.setText(Html.fromHtml(holder.newsBO.getDescription()));
                        holder.tvDateTime.setText(holder.newsBO.getDateTime());
                        holder.tvPageNo.setText(pos1 + 1 + " / " + newsList.size() + "");
                    } else {
                        Toast.makeText(NewsActivity.this, "No more news found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.imgViewPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos1 = pos - 1;
                    if (pos1 >= 0) {
                        pos = pos1;
                        holder.newsBO = newsList.get(pos1);

                        holder.tvTitle.setText(holder.newsBO.getTitle());
                        Picasso.with(appCompatActivity).load(holder.newsBO.getImageUrl()).into(holder.imgNewsImage);
                        holder.tvDescription.setText(Html.fromHtml(holder.newsBO.getDescription()));
                        holder.tvDateTime.setText(convertDate(holder.newsBO.getDateTime()));
                        holder.tvPageNo.setText(pos1 + 1 + " / " + newsList.size() + "");
                    } else {
                        Toast.makeText(NewsActivity.this, "No more news found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        NewsBO bb = newsList.get(pos);
                        String shareMessage = "http://dinamalarnellai.com/web/news/" + categoryId;
                        Intent shareIntent =
                                new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                       /*   shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "Insert Subject Here");*/
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                bb.getWebUrl());
                        startActivity(Intent.createChooser(shareIntent,
                                "Share with..,"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.btnFontPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.tvDescription.getTextSize() + 2F);
                        holder.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.tvTitle.getTextSize() + 2F);
                        holder.tvDateTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.tvDateTime.getTextSize() + 2F);
                        holder.tvPageNo.setTextSize(TypedValue.COMPLEX_UNIT_PX, holder.tvPageNo.getTextSize() + 2F);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.btnFontMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder.tvDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX, (holder.tvDescription.getTextSize() - 2F));
                        holder.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, (holder.tvTitle.getTextSize() - 2F));
                        holder.tvDateTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, (holder.tvDateTime.getTextSize() - 2F));
                        holder.tvPageNo.setTextSize(TypedValue.COMPLEX_UNIT_PX, (holder.tvPageNo.getTextSize() - 2F));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
           /* holder.btnFont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(NewsActivity.this);
                    builderSingle.setIcon(R.drawable.ic_launcher);
                    builderSingle.setTitle("Select Font Size");

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            NewsActivity.this,
                            android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("Small");
                    arrayAdapter.add("Medium");
                    arrayAdapter.add("Large");

                   *//* builderSingle.setNegativeButton(
                            "cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });*//*

                    builderSingle.setAdapter(
                            arrayAdapter,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String strName = arrayAdapter.getItem(which);
                                    if (strName.equals("Small")) {
                                        holder.tvDescription.setTextSize(13);
                                        holder.tvTitle.setTextSize(13);
                                        holder.tvDateTime.setTextSize(13);
                                        holder.tvPageNo.setTextSize(13);
                                    } else if (strName.equals("Medium")) {
                                        holder.tvDescription.setTextSize(15);
                                        holder.tvTitle.setTextSize(15);
                                        holder.tvDateTime.setTextSize(15);
                                        holder.tvPageNo.setTextSize(15);
                                    } else if (strName.equals("Large")) {
                                        holder.tvDescription.setTextSize(16);
                                        holder.tvTitle.setTextSize(16);
                                        holder.tvDateTime.setTextSize(16);
                                        holder.tvPageNo.setTextSize(16);
                                    }
                                }
                            });
                    builderSingle.show();
                }
            });*/
            holder.tvNewsHeading.setText(categoryName);

            return convertView;
        }

        private class ViewHolder {
            NewsBO newsBO;
            private TextView tvTitle, tvDescription, tvDateTime;
            private ImageView imgNewsImage;
            private ImageView imgViewBack;
            private TextView tvPageNo, tvCategoryName,tvNewsHeading;
            private Button btnFontPlus, btnFontMinus, btnShare, btnFavourite;
            private Button imgViewNext, imgViewPrevious;

            public ViewHolder(View v) {
                imgNewsImage = (ImageView) v.findViewById(R.id.img_image);
                tvTitle = (TextView) v.findViewById(R.id.tv_title);
                tvDescription = (TextView) v.findViewById(R.id.tv_description);
                tvDateTime = (TextView) v.findViewById(R.id.tv_date);
                imgViewBack = (ImageView) v.findViewById(R.id.imageView_back);
                imgViewNext = (Button) v.findViewById(R.id.btn_next);
                imgViewPrevious = (Button) v.findViewById(R.id.btn_previous);
                tvPageNo = (TextView) v.findViewById(R.id.tv_page1);
                btnFontPlus = (Button) v.findViewById(R.id.btn_font_plus);
                btnFontMinus = (Button) v.findViewById(R.id.btn_font_minus);
                btnShare = (Button) v.findViewById(R.id.btn_share);
                //     btnFavourite = (Button) v.findViewById(R.id.btn_favourite);
                tvCategoryName = (TextView) v.findViewById(R.id.tv_name);
                tvNewsHeading = (TextView) v.findViewById(R.id.tv_news_heading);


            }
        }
    }

    private String convertDate(String date) {
        String result = null;
        try {
            String[] array = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            String[] array1 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
            String[] getdate = date.split("-");

            for (int i = 0; i < array1.length; i++) {
                if (getdate[1].equals(array1[i])) {
                    getdate[1] = array[i];
                    break;
                }
            }

            result = getdate[0] + " " + getdate[1] + " " + getdate[2];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

