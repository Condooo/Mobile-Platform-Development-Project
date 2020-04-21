package com.mpd.mpdproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    Context context;
    List<String> parents;
    List<Float> durations;
    Map<String, List<String>> children;

    public CustomExpandableListAdapter(Context context, List<String> parents, Map<String, List<String>> children, List<Float> durations) {
        this.context = context;
        this.parents = parents;
        this.children = children;
        this.durations = durations;
    }

    public CustomExpandableListAdapter(){
        this.parents = new List<String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public String get(int index) {
                return null;
            }

            @Override
            public String set(int index, String element) {
                return null;
            }

            @Override
            public void add(int index, String element) {

            }

            @Override
            public String remove(int index) {
                return null;
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<String> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<String> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
        this.children = new Map<String, List<String>>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(@Nullable Object key) {
                return false;
            }

            @Override
            public boolean containsValue(@Nullable Object value) {
                return false;
            }

            @Nullable
            @Override
            public List<String> get(@Nullable Object key) {
                return null;
            }

            @Nullable
            @Override
            public List<String> put(String key, List<String> value) {
                return null;
            }

            @Nullable
            @Override
            public List<String> remove(@Nullable Object key) {
                return null;
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ? extends List<String>> m) {

            }

            @Override
            public void clear() {

            }

            @NonNull
            @Override
            public Set<String> keySet() {
                return null;
            }

            @NonNull
            @Override
            public Collection<List<String>> values() {
                return null;
            }

            @NonNull
            @Override
            public Set<Entry<String, List<String>>> entrySet() {
                return null;
            }
        };
        this.durations = new List<Float>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(@Nullable Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<Float> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(Float aFloat) {
                return false;
            }

            @Override
            public boolean remove(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends Float> c) {
                return false;
            }

            @Override
            public boolean addAll(int index, @NonNull Collection<? extends Float> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public Float get(int index) {
                return null;
            }

            @Override
            public Float set(int index, Float element) {
                return null;
            }

            @Override
            public void add(int index, Float element) {

            }

            @Override
            public Float remove(int index) {
                return null;
            }

            @Override
            public int indexOf(@Nullable Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(@Nullable Object o) {
                return 0;
            }

            @NonNull
            @Override
            public ListIterator<Float> listIterator() {
                return null;
            }

            @NonNull
            @Override
            public ListIterator<Float> listIterator(int index) {
                return null;
            }

            @NonNull
            @Override
            public List<Float> subList(int fromIndex, int toIndex) {
                return null;
            }
        };
    }

    @Override
    public int getGroupCount() {
        return parents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(parents.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parents.get(groupPosition);
    }

    public Object getDuration(int groupPosition){
        return durations.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(parents.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String parentTitle = (String)getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_parent, null);
        }

        float duration = ((Float)getDuration(groupPosition));


        TextView txtParent = (TextView)convertView.findViewById(R.id.txtParent);
        float redVal = clamp((1.0f -((182-duration)/182)), 0.0f ,1.0f);
        float grnVal = clamp(2*((365-duration)/365), 0.0f ,1.0f);
        txtParent.setTextColor(Color.WHITE);
        txtParent.setBackgroundColor(Color.rgb((int)(100+(100*redVal)), (int)(100+(100*grnVal)), 100));
        txtParent.setText(parentTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childContent = (String)getChild(groupPosition, childPosition);
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_child, null);
        }

        TextView txtChild = (TextView)convertView.findViewById(R.id.txtChild);
        txtChild.setText(childContent);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
