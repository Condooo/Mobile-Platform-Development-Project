package com.mpd.mpdproject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListFragment extends Fragment {

    private LinkedList<WidgetClass> resultList;
    private TextView testView;

    private ExpandableListView expandableListView;
    private List<String> listParents;
    private Map<String, List<String>> listChildren;
    private List<Float> listDurations;
    private ExpandableListAdapter listAdapter;

    private OnItemClick itemClick;

    public interface OnItemClick{
        void onItemClick(String title);
    }

    public void itemClick(String title){
        itemClick.onItemClick(title);
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null)
            resultList = (LinkedList<WidgetClass>)savedInstanceState.getSerializable("results");
        else
            resultList = new LinkedList<WidgetClass>();




        // TODO: Populate list with results
    }

//    public void setResultList(LinkedList<WidgetClass> list){
////        resultList = list;
////        String output = "";
////        for(WidgetClass item : resultList){
////            output += item.getTitle() + "\n";
////        }
////        testView.setText(output);
//    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        itemClick = (OnItemClick)context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("results", resultList);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results_list, container,false);

        if (savedInstanceState != null)
            resultList = (LinkedList<WidgetClass>) savedInstanceState.getSerializable("results");
        else
            resultList = new LinkedList<WidgetClass>();

        //testView = (TextView)v.findViewById(R.id.testView);
//        if (resultList != null)
//            setResultList(resultList);

        expandableListView = (ExpandableListView)v.findViewById(R.id.expandableListView);
        fillData(resultList);

        return v;
    }

    public void clearList(){
        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter();
        expandableListView.setAdapter(adapter);
        resultList = null;
    }

    public void fillData(LinkedList<WidgetClass> results){
        if (results != null && expandableListView != null){
            resultList = results;
            listParents = new ArrayList<>();
            listDurations = new ArrayList<>();
            listChildren = new HashMap<>();

            for (int i = 0; i < resultList.size(); i++){
                WidgetClass result = resultList.get(i);

                listParents.add(result.getTitle());
                listDurations.add(result.getDuration());

                List<String> parent = new ArrayList<>();

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm");
                String startDate = dateFormat.format(result.getStartDate());
                String endDate = dateFormat.format(result.getEndDate());

                String typeString = "";
                switch (result.getType()){
                    case ROADWORK_CURRENT:
                        typeString = "Roadwork (current)";
                        break;
                    case ROADWORK_PLANNED:
                        typeString = "Roadwork (planned)";
                        break;
                    case INCIDENT:
                        typeString = "Incident";
                        break;
                        default:
                            break;
                }

                String childContent =
                                "Duration: " + (int)result.getDuration() + " days \n" +
                                "Type: " + typeString + "\n" +
                                "Start date: " + startDate + "\n" +
                                "End date: " + endDate + "\n" +
                                "Delay information: " + result.getDelayInfo() + "\n" +
                                "Coordinates: " + result.getCoordLat() + " " + result.getCoordLng();
                parent.add(childContent);
                listChildren.put(listParents.get(i), parent);
            }

            listAdapter = new CustomExpandableListAdapter(this.getContext(), listParents, listChildren, listDurations);
            expandableListView.setAdapter(listAdapter);
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    itemClick.onItemClick(parent.getExpandableListAdapter().getGroup(groupPosition).toString());
                    Log.e("Group click", parent.getExpandableListAdapter().getGroup(groupPosition).toString());
                    return false;
                }
            });

        }
    }
}
