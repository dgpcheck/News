package com.dinamalarnellai.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinamalarnellai.adapter.ExpandableListAdapter;
import com.dinamalarnellai.app.AppConfig;
import com.dinamalarnellai.app.AppController;
import com.dinamalarnellai.app.Commons;
import com.dinamalarnellai.app.MyHttpConnection;
import com.dinamalarnellai.bo.SubCategoryBO;

import org.json.JSONArray;
import org.json.JSONObject;

import app.dinamalarnellai.com.news.R;


public class NavigationActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    Fragment fragment = null;
    ExpandableListView expListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<SubCategoryBO>> expandableCategoryDetails = new HashMap<String, List<SubCategoryBO>>();
    ;
    private AppController controller;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (AppController) getApplicationContext();
        controller.setContext(this);

        setContentView(R.layout.activity_navigation);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        setSupportActionBar(mToolbar);
        if (controller.isOnline()) {
            new LoadCategoryAndSubCategory().execute();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
          //      GravityCompat.START);
        //mDrawerLayout.setDrawerShadow(R.mipmap.drawer_shadow,
        //		GravityCompat.END);

//		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(NavigationActivity.this, /* host Activity */
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
        //mDrawerLayout.closeDrawer(GravityCompat.END);



        setUpDrawer();

    }

    /**
     * Get the names and icons references to build the drawer menu...
     */
    private void setUpDrawer() {

        expandableListTitle = new ArrayList<String>(expandableCategoryDetails.keySet());
        expandableListAdapter = new ExpandableListAdapter(this, expandableListTitle, expandableCategoryDetails);
        expListView.setAdapter(expandableListAdapter);
        //	expListView.expandGroup(0);
        //	listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        //	expListView.setAdapter(listAdapter);
        //	fragment = new MercuryFragment();
        //	getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        // mDrawerLayout.closeDrawer(expListView);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* *
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(expListView);
        //  menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }


    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> expandableListTitle;
        private HashMap<String, List<SubCategoryBO>> expandableListDetail;

        public ExpandableListAdapter(Context context, List<String> expandableListTitle,
                                     HashMap<String, List<SubCategoryBO>> expandableListDetail) {
            this._context = context;
            this.expandableListTitle = expandableListTitle;
            this.expandableListDetail = expandableListDetail;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final SubCategoryBO expandedListText = (SubCategoryBO) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_item, null);
            }

		/*	TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.expandedListItem);*/

            TextView expandedListTextView = (TextView) convertView
                    .findViewById(R.id.expandedListItem);
            expandedListTextView.setText(expandedListText.getSubCategoryName());
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.expandableListDetail.get(this.expandableListTitle.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.expandableListTitle.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this.expandableListTitle.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group, null);
            }

            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
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
            progressDialogue = ProgressDialog.show(NavigationActivity.this, "Please wait ...", "", true);
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


}
