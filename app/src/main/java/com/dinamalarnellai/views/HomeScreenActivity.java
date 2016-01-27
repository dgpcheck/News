package com.dinamalarnellai.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinamalarnellai.adapter.GridViewAdapter;
import com.dinamalarnellai.app.AppConfig;
import com.dinamalarnellai.app.AppController;
import com.dinamalarnellai.app.Commons;
import com.dinamalarnellai.app.MyHttpConnection;
import com.dinamalarnellai.bo.CategoryBO;
import com.dinamalarnellai.bo.SubCategoryBO;
import com.dinamalarnellai.fragments.ExpandableNewsFragment;
import com.dinamalarnellai.fragments.HomeScreenFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.dinamalarnellai.com.news.R;


public class HomeScreenActivity extends BaseActivity {
    private Toolbar mToolbar;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private MyAdapter mAdapter;
    private ArrayList<CategoryBO> categoryItems = new ArrayList<CategoryBO>();
    private Resources res;
    static final int DEFAULT_NUM_FRAGMENTS = 4;
    static final int DEFAULT_NUM_ITEMS = 4;
    int mNumFragments = 0; // total number of fragments
    int mNumItems = 0;
    private ViewPager mPager;
    private TextView tvPage, tvFlashNews;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private AppController controller;
    private HashMap<String, List<SubCategoryBO>> expandableCategoryDetails = new HashMap<String, List<SubCategoryBO>>();
    private ArrayList<CategoryBO> categoryItem = new ArrayList<CategoryBO>();
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "586771477295";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging gcm;
    private int dotsCount;
    private ImageView[] dots;
    private LinearLayout pager_indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        res = getResources();
        controller = (AppController) getApplicationContext();
        controller.setContext(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer
        // opens
        tvFlashNews = (TextView) findViewById(R.id.tv_flashnews);
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
                GravityCompat.START);
        mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
                GravityCompat.END);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(HomeScreenActivity.this, /* host Activity */
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


        mPager = (ViewPager) findViewById(R.id.pager11);
        tvPage = (TextView) findViewById(R.id.tv_page);

        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        // "bmodel.configurationMasterHelper.SHOW_GCM_NOTIF" to Enable & disable
        // GCM
        if (AppController.checkGoolgePlayService) {
            if (checkPlayServices()) {
                Commons.print("play true");
                gcm = GoogleCloudMessaging.getInstance(this);
                controller.mGcmRegID = getRegistrationId(this);
                Commons.printInformation("REG ID IS : " + controller.mGcmRegID);
                if (controller.mGcmRegID.isEmpty()) {
                    registerInBackground();
                }
            } else {
                Commons.printInformation("No valid Google Play Services APK found.");
            }
        }

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String resortedText = prefs.getString("flag", "failure");

        if (controller.isOnline()) {
            if (!resortedText.equals("success"))
                new SendGCMDetails().execute();

            new LoadCategoryAndSubCategory().execute();
            new LoadCategory().execute();
            new LoadFlasNews().execute();
        } else
            showAlert(0);

        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
    }


    @Override
    public void onResume() {
        super.onResume();

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                calculateDetails(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void calculateDetails(int position) {
        int startPage = 0;
        try {
            startPage = position + 1;
           // tvPage.setText(startPage + " / " + mNumFragments);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }


    public class MyAdapter extends FragmentStatePagerAdapter {

        private ArrayList<CategoryBO> mList;
        private int mNumItems = 0;
        private int mNumFragments = 0;

        /**
         * Return a new adapter.
         */

        public MyAdapter(FragmentManager fm, ArrayList<CategoryBO> db,
                         Resources res) {
            super(fm);
            mList = db;
            setup(db, res);
        }

        /**
         * Get the number of fragments to be displayed in the ViewPager.
         */

        @Override
        public int getCount() {
            return mNumFragments;
        }

        /**
         * Return a new GridFragment that is used to display n items at the
         * position given.
         *
         * @param position int - the position of the fragement; 0..numFragments-1
         */

        @Override
        public Fragment getItem(int position) {
            // Create a new Fragment and supply the fragment number, image
            // position, and image count as arguments.
            // (This was how arguments were handled in the original pager
            // example.)
            Bundle args = new Bundle();
            args.putInt("num", position + 1);
            args.putInt("firstImage", position * mNumItems);
            // The last page might not have the full number of items.
            int imageCount = mNumItems;
            if (position == (mNumFragments - 1)) {
                int numTopics = categoryItems.size();
                int rem = numTopics % mNumItems;
                if (rem > 0)
                    imageCount = rem;
            }
            args.putInt("imageCount", imageCount);
            args.putSerializable("topicList", mList);
            args.putInt("totalPages", mNumFragments);

            // Return a new GridFragment object.
            HomeScreenFragment f = new HomeScreenFragment();
            f.setArguments(args);

            return f;
        }

        void setup(ArrayList<CategoryBO> tlist, Resources res) {
            mList = tlist;

            if ((tlist == null) || (res == null)) {
                mNumItems = DEFAULT_NUM_ITEMS;
                mNumFragments = DEFAULT_NUM_FRAGMENTS;
            } else {
                int numTopics = tlist.size();
                int numRows = res.getInteger(R.integer.grid_num_rows);
                int numCols = res.getInteger(R.integer.grid_num_cols);
                int numTopicsPerPage = numRows * numCols;
                int numFragments = numTopics / numTopicsPerPage;
                if (numTopics % numTopicsPerPage != 0)
                    numFragments++; // Add one if there is a partial page

                mNumFragments = numFragments;
                mNumItems = numTopicsPerPage;

            }


        } // end setup
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                newsFilterClickedFragment();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void newsFilterClickedFragment() {
        try {

            mDrawerLayout.openDrawer(GravityCompat.END);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ExpandableNewsFragment frag = (ExpandableNewsFragment) fm
                    .findFragmentByTag("filter");
            android.support.v4.app.FragmentTransaction ft = fm
                    .beginTransaction();
            if (frag != null)
                ft.detach(frag);
            Bundle bundle = new Bundle();
            bundle.putSerializable("serilizeContent",
                    expandableCategoryDetails);

            ExpandableNewsFragment fragobj = new ExpandableNewsFragment();
            fragobj.setArguments(bundle);
            ft.add(R.id.right_drawer, fragobj, "filter");
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class LoadCategory extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialogue;

        @Override
        protected String doInBackground(String... params) {
            try {
                MyHttpConnection http = new MyHttpConnection();
                Commons.print(AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_CATEGORY);
                http.create(MyHttpConnection.POST, AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_CATEGORY);
                http.connectMe();
                String result = http.getResultset();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    CategoryBO categoryBO = new CategoryBO();
                    categoryBO.setCategoryId(jsonObj.getInt("categoryid"));
                    categoryBO.setCategoryName(jsonObj.getString("category_name"));
                    categoryItems.add(categoryBO);
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
            int numTopics = categoryItems.size();

            int numRows = res.getInteger(R.integer.grid_num_rows);
            int numCols = res.getInteger(R.integer.grid_num_cols);
            int numTopicsPerPage = numRows * numCols;
            int numFragments = numTopics / numTopicsPerPage;
            if (numTopics % numTopicsPerPage != 0)
                numFragments++; // Add one if there is a partial page
            mNumFragments = numFragments;
            mNumItems = numTopicsPerPage;
            mAdapter = new MyAdapter(getSupportFragmentManager(),
                    categoryItems, res);

            mPager.setAdapter(mAdapter);
            mPager.invalidate();
            mAdapter.notifyDataSetChanged();
            setUiPageViewController();
            calculateDetails(0);

        }

        @Override
        protected void onPreExecute() {
            progressDialogue = ProgressDialog.show(HomeScreenActivity.this, "Please wait ...", "", true);
        }


    }
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    class LoadCategoryAndSubCategory extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialogue;
        private ArrayList<SubCategoryBO> subCategoryItems = new ArrayList<SubCategoryBO>();

        @Override
        protected String doInBackground(String... params) {
            try {
                MyHttpConnection http = new MyHttpConnection();
                http.create(MyHttpConnection.POST, AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_SUB_CATEGORY);
                Commons.print(AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_SUB_CATEGORY);
                http.connectMe();
                String result = http.getResultset();
                Commons.print(result);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    String categoryName = jsonObj.getString("categoryname");
                    String downloadDate = jsonObj.getString("subcategories");
                    JSONArray jsonArray1 = new JSONArray(downloadDate);
                    subCategoryItems = new ArrayList<SubCategoryBO>();
                    for (int j = 0; j < jsonArray1.length(); ++j) {
                        JSONObject jsonObj1 = (JSONObject) jsonArray1.get(j);
                        SubCategoryBO subcategoryBO = new SubCategoryBO();
                        subcategoryBO.setSubCategoryId(jsonObj1.getInt("subcategoryid"));
                        subcategoryBO.setSubCategoryName(jsonObj1.getString("subcategoryname"));
                        subCategoryItems.add(subcategoryBO);
                    }
                    expandableCategoryDetails.put(categoryName, subCategoryItems);
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

        }

        @Override
        protected void onPreExecute() {
            progressDialogue = ProgressDialog.show(HomeScreenActivity.this, "Please wait ...", "", true);
        }


    }

    class LoadFlasNews extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialogue;
        private String flashnews;

        @Override
        protected String doInBackground(String... params) {
            try {
                MyHttpConnection http = new MyHttpConnection();
                http.create(MyHttpConnection.POST, AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_FLASH_NEWS);
                Commons.print(AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_FLASH_NEWS);
                http.connectMe();
                String result = http.getResultset();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObj = (JSONObject) jsonArray.get(i);
                    flashnews = jsonObj.getString("news_title");
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
            tvFlashNews.setText(flashnews);

        }

        @Override
        protected void onPreExecute() {
            progressDialogue = ProgressDialog.show(HomeScreenActivity.this, "Please wait ...", "", true);
        }


    }

    class SendGCMDetails extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialogue;

        @Override
        protected String doInBackground(String... params) {
            try {
                String data = controller.mGcmRegID + "/" + String.valueOf(controller.getIMEINumber());
                Commons.print(data);
                MyHttpConnection http = new MyHttpConnection();
                http.create(MyHttpConnection.POST, AppConfig.REQUEST_BASE_URL + AppConfig.REQUEST_INSTALL + "/" + data.toString());
                http.connectMe();
                String result = http.getResultset().replace("{", "").replace("}", "");
                Log.d("DGP", result);
                String[] array = result.split(":");
                String resudata = array[1].replace("\"", "");
                Log.d("DGP", resudata);
                SharedPreferences.Editor shared = getPreferences(Context.MODE_PRIVATE).edit();
                shared.putString("flag", resudata);
                shared.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialogue != null)
                progressDialogue.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialogue = ProgressDialog.show(HomeScreenActivity.this, "Please wait ...", " ", true);
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
                alertDialogBuilder.setMessage(getResources().getString(R.string.want_to_exit));
                alertDialogBuilder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {

                                finish();
                                try {
                                    android.os.Process
                                            .killProcess(android.os.Process.myPid());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                alertDialogBuilder.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {

                                dialog.cancel();
                            }
                        });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

            default:
                break;
        }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showAlert(1);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        Commons.print("check play service");
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Commons.printInformation("This device is not supported.");
                Toast.makeText(this, "This device is not supported.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Commons.printInformation("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
                Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Commons.printInformation("App version changed.");
            return "";
        }
        Commons.print(registrationId);
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return this.getSharedPreferences(HomeScreenActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging
                                .getInstance(HomeScreenActivity.this);
                    }
                    controller.mGcmRegID = gcm.register(SENDER_ID);
                    msg = "Device registered, re" +
                            "gistration ID="
                            + controller.mGcmRegID;
                    System.out.println("RegID: " + controller.mGcmRegID);

                    // You should send the registration ID to your server over
                    // HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the
                    // device will send
                    // upstream messages to a server that echo back the message
                    // using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(HomeScreenActivity.this,
                            controller.mGcmRegID);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Commons.printException(ex);
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Commons.print("Google Cloud Registration Message:" + msg);
                // Toast.makeText(LoginScreen.this, msg,
                // Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Commons.printInformation("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }
}
