package com.dinamalarnellai.fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinamalarnellai.bo.CategoryBO;
import com.dinamalarnellai.views.NewsActivity;
import com.dinamalarnellai.views.NewsListActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.dinamalarnellai.com.news.R;


public class HomeScreenFragment extends Fragment {


    public HomeScreenFragment() {
        // Required empty public constructor
    }

    private int mNum; // the id number assigned to this fragment
    private int mFirstImage = 0; // the index number of the first image to show
    // in the fragment
    private int mImageCount = -1; // the number of images to show in the
    // fragment
    private ArrayList<CategoryBO> mTopicList; // the list of topics used by
    private GridView gridview;
    private Activity mActivity;
    private GridImageAdapter gridAdapter;
    private TextView tvProductCount, tvAvailableCount,
            tvNotAvailableCount, tvPage, tvTotalPage, tvStartProduct;
    int avaliableCount = 0, notAvailableCount = 0;
    private int mTotalPages = 0, from = 0, to = 0;
    private int locationID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Build the view that shows the grid.
        View view = inflater
                .inflate(R.layout.fragment_home_sceen, container, false);
        gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setTag(mNum);
        mActivity = getActivity();

        return view;
    }

    /**
     * onCreate
     */

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read the arguments and check resource values for number of rows and
        // number of columns
        // so we know how many images to display on this fragment.

        Bundle args = getArguments();
        mNum = ((args != null) ? args.getInt("num") : 0);
        mTotalPages = ((args != null) ? args.getInt("totalPages") : 0);
        from = ((args != null) ? args.getInt("from") : 0);
        to = ((args != null) ? args.getInt("to") : 0);
        if (args != null) {
            mTopicList = (ArrayList<CategoryBO>) args
                    .getSerializable("topicList");
            mFirstImage = args.getInt("firstImage");
        }
        locationID = ((args != null) ? args.getInt("locationID") : 0);
        // includes the old first image. We recalculate the image count because
        // it might change
        // if we are reorienting from portrait to landscape.
        Resources res = getActivity().getResources();
        int numRows = res.getInteger(R.integer.grid_num_rows);
        int numCols = res.getInteger(R.integer.grid_num_cols);
        int numTopicsPerPage = numRows * numCols;
        mImageCount = numTopicsPerPage;
        mFirstImage = (mFirstImage / numTopicsPerPage) * numTopicsPerPage;
    }

    /**
     * onActivityCreated When the activity is created, divide the usable space
     * into columns and put a grid of images in that area.
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = getActivity();
        Resources res = mActivity.getResources();


        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // From the resource files, determine how many rows and columns are to
        // be displayed.
        final int numRows = res.getInteger(R.integer.grid_num_rows);
        final int numCols = res.getInteger(R.integer.grid_num_cols);

        // Figure out how much space is available for the N rows and M columns
        // to be displayed.
        // We start with the root view for the fragment and adjust for the
        // title, padding, etc.
        int titleHeight = res.getDimensionPixelSize(R.dimen.topic_title_height);
        int titlePadding = res
                .getDimensionPixelSize(R.dimen.topic_title_padding);
        int buttonAreaHeight = res
                .getDimensionPixelSize(R.dimen.button_area_height);
        int titleBarHeight = res
                .getDimensionPixelSize(R.dimen.title_bar_height);
        int gridHspacing = res
                .getDimensionPixelSize(R.dimen.image_grid_hspacing);
        int gridVSpacing = res
                .getDimensionPixelSize(R.dimen.image_grid_vspacing);
        int otherGridH = res.getDimensionPixelSize(R.dimen.other_grid_h);
        int otherGridW = res.getDimensionPixelSize(R.dimen.other_grid_w);
        int heightUsed = 2 * titleBarHeight + (numRows + 2) * gridVSpacing
                + (titleHeight + 2 * titlePadding) + otherGridH
                + buttonAreaHeight;

        int widthUsed = 40; // just a guess for now.
        int availableHeight = metrics.heightPixels - heightUsed;
        int availableWidth = metrics.widthPixels - widthUsed;
        int cellWidth = availableWidth / numCols;
        int cellHeight = availableHeight / numRows;


        if (gridview == null)
            Log.d("DEBUG", "Unable to locate the gridview.");
        else {
            gridAdapter = new GridImageAdapter(mActivity, mTopicList,
                    mFirstImage, mImageCount, cellWidth, cellHeight);
        /*    gridview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
           /* gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                    showTopic (mFirstImage + position);
                    return true;
                }
            });*/

            // Connect the gridview with an adapter that fills up the space.
            gridview.setAdapter(gridAdapter);
            gridAdapter.notifyDataSetChanged();
            gridview.invalidateViews();

        }
    }

    /* public void showTopic (int index) {

         Toast.makeText(getActivity (), "" + index, Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(getActivity().getApplicationContext(), NewsActivity.class);
         intent.putExtra ("index", index);
         startActivity (intent);
     }*/
    @Override
    public void onResume() {
        super.onResume();

    }


}

class GridImageAdapter extends BaseAdapter {

    public static final int DEFAULT_CELL_SIZE = 220;
    private Context mContext;
    private ArrayList<CategoryBO> mTopicList;
    private int mImageOffset = 0; // the index offset into the list of
    // images
    private int mImageCount = -1; // -1 means that we display all images
    private int mNumTopics = 0; // the total number of topics in the topics
    // collection
    private int mCellWidth = DEFAULT_CELL_SIZE;
    private int mCellHeight = DEFAULT_CELL_SIZE;


    private Button btnProductName = null;
    private View childView = null;
    private int j = 0;
    ImageView imageView = null;
    TextView textView = null;

    public GridImageAdapter(Context c, ArrayList<CategoryBO> tlist,
                            int imageOffset, int imageCount) {
        mContext = c;
        mImageOffset = imageOffset;
        mImageCount = imageCount;
        mTopicList = tlist;
        mNumTopics = (tlist == null) ? 0 : mTopicList.size();

    }

    public GridImageAdapter(Context c, ArrayList<CategoryBO> tlist,
                            int imageOffset, int imageCount, int cellWidth, int cellHeight) {
        this(c, tlist, imageOffset, imageCount);
        mCellWidth = cellWidth;
        mCellHeight = cellHeight;
        mTopicList = tlist;

    }

    public void updateResults(ArrayList<CategoryBO> tlist) {
        mTopicList = tlist;
        // Triggers the list update
        // gridAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public int getCount() {
        // If we are on the last page and there are no more images, the
        // count is how many are being
        // displayed.
        int count = mImageCount;
        if (mImageOffset + mImageCount >= mNumTopics)
            count = mNumTopics - mImageOffset;
        return count;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return mImageOffset + position;
    }


    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {


        int numTopics = mTopicList.size();

        if (convertView == null) { // if it's not recycled, initialize some
            // attributes

            int layoutId = R.layout.grid_item_layout;
            LayoutInflater li = ((Activity) mContext).getLayoutInflater();
            childView = li.inflate(layoutId, null);
        } else {
            childView = convertView;
        }

        if (childView != null) {
            // Set the width and height of the child view.
            childView.setLayoutParams(new GridView.LayoutParams(mCellWidth,
                    mCellHeight));

            j = position + mImageOffset;
            if (j < 0)
                j = 0;
            if (j >= numTopics)
                j = numTopics - 1;
            //calculateAvailability(j);
            imageView = (ImageView) childView.findViewById(R.id.imgName);
            if (imageView != null) {
                Resources res = mContext.getResources();
                int imagePadding = res.getDimensionPixelSize(R.dimen.grid_image_padding);

                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //  imageView.setBackgroundResource(R.color.background_grid1_cell);
                imageView.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
                // imageView.setImageBitmap(mTopicList.get(j).getImage());
                Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
                //   imageView.setImageResource(mTopicList.get(j).getImage()));
                imageView.setTag(new Integer(j));
                imageView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showTopic((Integer) v.getTag());

                    }
                });
            }
            textView = (TextView) childView.findViewById(R.id.tvName);
            if (textView != null) {
                textView.setText(mTopicList.get(j).getCategoryName());
                textView.setTag(new Integer(j));


            }
        }
        return childView;
    }

    public void showTopic(int index) {
        Intent intent = new Intent(mContext.getApplicationContext(), NewsListActivity.class);
        intent.putExtra("categoryid", mTopicList.get(index).getCategoryId());
        intent.putExtra("categoryName", mTopicList.get(index).getCategoryName());
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(mContext, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        mContext.startActivity(intent, bndlanimation);

    }

} // end class GridFragment
