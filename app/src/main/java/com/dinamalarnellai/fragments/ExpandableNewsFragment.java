package com.dinamalarnellai.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.dinamalarnellai.adapter.ExpandableListAdapter;
import com.dinamalarnellai.bo.SubCategoryBO;
import com.dinamalarnellai.views.NewsActivity;
import com.dinamalarnellai.views.NewsListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.dinamalarnellai.com.news.R;

public class ExpandableNewsFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<SubCategoryBO>> expandableCategoryDetails;

    public ExpandableNewsFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     View view = inflater.inflate(R.layout.activity_main, container, false);
        expandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        try{
            expandableCategoryDetails = (HashMap) getArguments().get("serilizeContent");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        expandableListTitle = new ArrayList<String>(expandableCategoryDetails.keySet());
        expandableListAdapter = new ExpandableListAdapter(getActivity(), expandableListTitle, expandableCategoryDetails);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.expandGroup(0);
       /* expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

*/
        expandableListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
              /*  Toast.makeText(
                        getActivity().getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableCategoryDetails.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                )
                        .show();
              */  SubCategoryBO selectedID =  expandableCategoryDetails.get(
                        expandableListTitle.get(groupPosition)).get(childPosition);
               showTopic(selectedID.getSubCategoryId(), selectedID.getSubCategoryName() +": "+expandableListTitle.get(groupPosition));
                return false;
            }
        });
    }
    public void showTopic(int index,String name) {
        Intent intent = new Intent(getActivity(), NewsListActivity.class);
        intent.putExtra("categoryid", index);
        intent.putExtra("SubCategory","SubCategoryScreen");
        intent.putExtra("categoryName",","+name);
        Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        getActivity().startActivity(intent, bndlanimation);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);


    }
}