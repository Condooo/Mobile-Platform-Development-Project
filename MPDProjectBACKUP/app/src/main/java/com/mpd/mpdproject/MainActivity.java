package com.mpd.mpdproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ResultFragment.OnDataPass, OnMapReadyCallback, DatePickerFragment.DateCallBack, ListFragment.OnItemClick {
    private static final String FRAGMENT_TAG = "result_fragment";
    private static final String MAP_TAG = "map_fragment";
    private static final String LIST_TAG_A = "list_fragmentA";
    private static final String LIST_TAG_B = "list_fragmentB";
    private ResultFragment resultFragment;

    private Geocoder geocoder;
    private LatLng locationMarker;

    private ViewSwitcher viewSwitcher;
    private ImageButton buttonBack;

    private RadioGroup radioGroup;
    private RadioButton rbLocation;
    private RadioButton rbRange;

    private EditText origin;

    private CheckBox cbWorkCurrent;
    private CheckBox cbWorkPlanned;
    private CheckBox cbIncident;

    private SeekBar barRange;
    private TextView txtRange;

    private EditText editDateStart;
    private Date dateStart;
    private EditText editDateEnd;
    private Date dateEnd;
    private EditText selectedDate;
    private TextView locationHint;

    private Button buttonSubmit;
    private Button buttonClear;


    private LinkedList<WidgetClass> resultList;

    private ProgressBar progressBar;
    private TextView progressText;

    private ImageButton buttonSearch;
    private ImageButton buttonExtents;
    private ImageButton buttonMapView;
    private ImageButton buttonListView;
    private ImageButton selectedView;
    private ImageButton buttonResultClear;

    private SupportMapFragment mMapFragment;
    private ListFragment mListFragment;

    private ArrayList<Marker> markers;
    private String selectedListItem;

    private GoogleMap map;

    @Override
    public void onDateSet(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String txtDate = dateFormat.format(date);

        if (selectedDate == editDateStart) {
            if (dateEnd != null && date.after(dateEnd)) {
                dateEnd = date;
                editDateEnd.setText(txtDate);
                Toast.makeText(this, "End date adjusted", Toast.LENGTH_LONG).show();
            }
            dateStart = date;
            editDateStart.setText(txtDate);
        } else if (selectedDate == editDateEnd) {
            if (dateStart != null && date.before(dateStart)) {
                dateStart = date;
                editDateStart.setText(txtDate);
                Toast.makeText(this, "Start date adjusted", Toast.LENGTH_LONG).show();
            }
            dateEnd = date;
            editDateEnd.setText(txtDate);
        }

    }

    @Override
    public void onItemClick(String title) {
        selectedListItem = title;
        Marker selectedMarker = null;
        for (int i = 0; i < markers.size(); i++) {
            String markerTitle = markers.get(i).getTitle();
            if (title.matches(markerTitle))
                selectedMarker = markers.get(i);
        }
        if (selectedMarker != null && selectedView == buttonMapView){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 11));
            selectedMarker.showInfoWindow();
        }


    }


//    private String getDirectionsUrl(LatLng origin, LatLng dest){
//        String str_origin = "origin="+origin.latitude+","+origin.longitude;
//        String str_dest = "destination="+dest.latitude+","+dest.longitude;
//        String key = "key="
//    }


    public enum descType {START, END, DELAY};

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //addMapMarkers(map);
        setMap();
    }

    private void zoomMarkerExtents(GoogleMap map){
        if (resultList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (WidgetClass item : resultList) {
                LatLng event = new LatLng(item.getCoordLat(), item.getCoordLng());
                builder.include(event);
            }
            if (locationMarker != null)
                builder.include(locationMarker);
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            map.animateCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
            map.animateCamera(cu);
        }
    }

    private void addMapMarkers(GoogleMap map) {
        if (resultList != null && resultList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (WidgetClass item : resultList) {
                LatLng event = new LatLng(item.getCoordLat(), item.getCoordLng());
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
                String startDate = dateFormat.format(item.getStartDate());
                String endDate = dateFormat.format(item.getEndDate());
                float markerColour = 0;
                switch (item.getType()){
                    case ROADWORK_CURRENT:
                        markerColour = BitmapDescriptorFactory.HUE_GREEN;
                        break;
                    case ROADWORK_PLANNED:
                        markerColour = BitmapDescriptorFactory.HUE_AZURE;
                        break;
                    case INCIDENT:
                        markerColour = BitmapDescriptorFactory.HUE_RED;
                        break;
                    default:
                        break;
                }
                Marker marker = map.addMarker(new MarkerOptions().position(event).title(item.getTitle()).snippet(startDate + " - " + endDate).icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
                markers.add(marker);
                builder.include(event);
            }

            if (locationMarker != null)
                builder.include(locationMarker);
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            map.moveCamera(CameraUpdateFactory.newLatLng(bounds.getCenter()));
            map.animateCamera(cu);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("results", resultList);
        outState.putInt("viewSwitchState", viewSwitcher.getDisplayedChild());
        outState.putSerializable("markers", markers);
        super.onSaveInstanceState(outState);
        if (resultList != null && resultList.size() > 0)
            saveResultsToFile();
    }

    private void revealAllFragments(){
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().show(fm.findFragmentByTag(LIST_TAG_A)).commit();
        fm.beginTransaction().show(fm.findFragmentByTag(LIST_TAG_B)).commit();
        fm.beginTransaction().show(fm.findFragmentByTag(MAP_TAG)).commit();
    }

    @Override
    public void onDataPass(LinkedList<WidgetClass> list) {
        resultList = list;
        if (resultList.size() <= 0)
            Toast.makeText(this, "No results found", Toast.LENGTH_LONG).show();

        if (map != null)
            addMapMarkers(map);
        else
            Log.e("MapTag", "Map null while attempting to add markers");

        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG_A);
        mListFragment.fillData(resultList);
        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG_B);
        mListFragment.fillData(resultList);
    }

    public ProgressBar getProgressBar(){
        return progressBar;
    }
    public TextView getProgressText(){
        return progressText;
    }

    private void clearSearchItems(){
        radioGroup.clearCheck();
        origin.setText("");
        origin.setHint("Search a specific road");
        cbWorkCurrent.setChecked(false);
        cbWorkPlanned.setChecked(false);
        rbLocation.setChecked(true);
        rbRange.setChecked(false);
        LinearLayout tileRange = findViewById(R.id.tileRange);
        tileRange.setVisibility(View.GONE);
        cbIncident.setChecked(false);
        barRange.setProgress(50);
        txtRange.setText(Integer.toString(barRange.getProgress()) + " km");
        editDateEnd.setText("");
        editDateStart.setText("");
        dateStart = null;
        dateEnd = null;
        locationHint.setVisibility(View.VISIBLE);

    }

    private void saveResultsToFile(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < resultList.size(); i++){
            editor.putString("title"+i, resultList.get(i).getTitle());
            editor.putString("delayInfo"+i, resultList.get(i).getDelayInfo());
            editor.putFloat("lng"+i, resultList.get(i).getCoordLng());
            editor.putFloat("lat"+i, resultList.get(i).getCoordLat());
            editor.putFloat("duration"+i, resultList.get(i).getDuration());
            editor.putInt("type"+i, resultList.get(i).getType().ordinal());
            editor.putLong("startDate"+i, resultList.get(i).getStartDate().getTime());
            editor.putLong("endDate"+i, resultList.get(i).getEndDate().getTime());
        }
        editor.putInt("valueCount", resultList.size());
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

        if (savedInstanceState != null) {
            resultList = (LinkedList<WidgetClass>) savedInstanceState.getSerializable("results");
            markers = (ArrayList<Marker>)savedInstanceState.getSerializable("markers");
        } else {
            markers = new ArrayList<>();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getInt("valueCount", 0) > 0) {
                Log.e("Results detected", "true");
                resultList = new LinkedList<WidgetClass>();
                int valueCount = prefs.getInt("valueCount",0);
                for (int i = 0; i < valueCount; i++){
                    WidgetClass result = new WidgetClass();
                    result.setTitle(prefs.getString("title"+1, null));
                    result.setDelayInfo(prefs.getString("delayInfo"+i, null));
                    Float lat = prefs.getFloat("lat"+i, -1);
                    Float lng = prefs.getFloat("lng"+i, -1);
                    result.setCoordinates(lat, lng);
                    result.setDuration(prefs.getFloat("duration"+i, -1));
                    WidgetClass.TYPE type = WidgetClass.TYPE.values()[prefs.getInt("type"+i, 0)];
                    result.setType(type);
                    Date startDate = new Date(prefs.getLong("startDate"+i, 0));
                    result.setStartDate(startDate);
                    Date endDate = new Date(prefs.getLong("endDate"+i, 0));
                    result.setStartDate(endDate);
                    resultList.add(result);
                }
                Toast.makeText(this,resultList.size() + " results loaded from file", Toast.LENGTH_LONG).show();
            }
        }

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(MAP_TAG) != null) {
            SupportMapFragment mapFrag = (SupportMapFragment) fm.findFragmentByTag(MAP_TAG);
            mapFrag.getMapAsync(this);
        }
        setContentView(R.layout.activity_main);


        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);
        if (savedInstanceState != null)
            viewSwitcher.setDisplayedChild(savedInstanceState.getInt("viewSwitchState"));

        buttonBack = (ImageButton)findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);

        origin = (EditText)findViewById(R.id.editLocStart);

        editDateStart = (EditText)findViewById(R.id.dateStart);
        editDateStart.setOnClickListener(this);
        editDateEnd = (EditText)findViewById(R.id.dateEnd);
        editDateEnd.setOnClickListener(this);

        locationHint = (TextView)findViewById(R.id.locationHint);

        barRange = (SeekBar)findViewById(R.id.barRange);
        barRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() == seekBar.getMax())
                    txtRange.setText("ALL");
                else
                    txtRange.setText(barRange.getProgress() + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        txtRange = (TextView)findViewById(R.id.txtRange);
        txtRange.setText(Integer.toString(barRange.getProgress()) + " km");

        cbWorkCurrent = (CheckBox)findViewById(R.id.cbRwCur);
        cbWorkPlanned = (CheckBox)findViewById(R.id.cbRwPl);
        cbIncident = (CheckBox)findViewById(R.id.cbInc);

        radioGroup = (RadioGroup)findViewById(R.id.rgType);
        rbLocation = (RadioButton)findViewById(R.id.rbLocation);
        rbRange = (RadioButton)findViewById(R.id.rbRange);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedButton = (RadioButton)findViewById(checkedId);
                if (checkedButton == rbLocation){
                        LinearLayout tileRange = findViewById(R.id.tileRange);
                        tileRange.setVisibility(View.GONE);
                        origin.setHint("Search a specific road");
                        locationHint.setVisibility(View.VISIBLE);
                    }
                if (checkedButton == rbRange){
                    LinearLayout tileRange = findViewById(R.id.tileRange);
                    tileRange.setVisibility(View.VISIBLE);
                    origin.setHint("Enter a location to search within range");
                    locationHint.setVisibility(View.GONE);
                    }

                }
        });

        buttonClear = (Button)findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(this);
        buttonSubmit = (Button)findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);


        setUI();

        // Create worker fragment
        resultFragment = (ResultFragment)fm.findFragmentByTag(FRAGMENT_TAG);
        if (resultFragment == null){
            resultFragment = new ResultFragment();
            fm.beginTransaction().add(resultFragment, FRAGMENT_TAG).commit();

        }

        if (fm.findFragmentByTag(MAP_TAG) == null) {
            // Create map fragment: assign to container B
            mMapFragment = SupportMapFragment.newInstance();
            mMapFragment.setRetainInstance(true);
            fm.beginTransaction().add(R.id.containerB, mMapFragment, MAP_TAG).commit();
            mMapFragment.getMapAsync(this);

        }

        if (fm.findFragmentByTag(LIST_TAG_A) == null) {
            mListFragment = new ListFragment();
                mListFragment.fillData(resultList);
            fm.beginTransaction().add(R.id.containerA, mListFragment, LIST_TAG_A).commit();
        }
        if (fm.findFragmentByTag(LIST_TAG_B) == null) {
            mListFragment = new ListFragment();
                mListFragment.fillData(resultList);
            fm.beginTransaction().add(R.id.containerB, mListFragment, LIST_TAG_B).commit();
        }
    }

    private void showDatePickerDialog(){
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setUI(){
        buttonExtents = (ImageButton) findViewById(R.id.buttonExtents);
        buttonExtents.setOnClickListener(this);
        buttonSearch = (ImageButton)findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
        buttonMapView = (ImageButton)findViewById(R.id.buttonMapView);
        buttonListView = (ImageButton)findViewById(R.id.buttonListView);
        if (buttonMapView != null)
            buttonMapView.setOnClickListener(this);
        if (buttonListView != null)
            buttonListView.setOnClickListener(this);

        progressBar = (ProgressBar)findViewById(R.id.progress);
        progressText = (TextView)findViewById(R.id.textProgress);
        buttonResultClear = (ImageButton)findViewById(R.id.buttonResultClear);
        buttonResultClear.setOnClickListener(this);

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        revealAllFragments();
        super.onConfigurationChanged(newConfig);
        setUI();
        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG_A);
        mListFragment.fillData(resultList);
        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG_B);
        mListFragment.fillData(resultList);
    }



    private void setMap(){
        if (buttonMapView != null)
            buttonMapView.setAlpha(1.0f);
        if (buttonListView != null)
            buttonListView.setAlpha(0.5f);
        if (buttonExtents != null)
            buttonExtents.setVisibility(View.VISIBLE);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(LIST_TAG_A) != null && getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE)
            fm.beginTransaction().show(fm.findFragmentByTag(LIST_TAG_A)).commit();
        if (fm.findFragmentByTag(LIST_TAG_B) != null)
            fm.beginTransaction().hide(fm.findFragmentByTag(LIST_TAG_B)).commit();
        if (fm.findFragmentByTag(MAP_TAG) != null)
            fm.beginTransaction().show(fm.findFragmentByTag(MAP_TAG)).commit();


        selectedView = buttonMapView;
    }

    private void setList(){
        if (buttonMapView != null)
            buttonMapView.setAlpha(0.5f);
        if (buttonListView != null)
            buttonListView.setAlpha(1.0f);
        if (buttonExtents != null)
            buttonExtents.setVisibility(View.INVISIBLE);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(MAP_TAG) != null)
            fm.beginTransaction().hide(fm.findFragmentByTag(MAP_TAG)).commit();
        if (fm.findFragmentByTag(LIST_TAG_A) != null)
            fm.beginTransaction().show(fm.findFragmentByTag(LIST_TAG_A)).commit();
        if (fm.findFragmentByTag(LIST_TAG_B) != null)
            fm.beginTransaction().show(fm.findFragmentByTag(LIST_TAG_B)).commit();


        selectedView = buttonListView;
    }

    public void onClick(View v) {
        if (v == buttonMapView && selectedView != buttonMapView) {
            // Select map view
            setMap();

        } else if (v == buttonListView && selectedView != buttonListView) {
            // Select list view
            setList();
        }

        if (v == buttonExtents && buttonExtents.getVisibility() == View.VISIBLE) {
            if (map != null && resultList != null)
                zoomMarkerExtents(map);
        }


        if (v == buttonSearch)
            viewSwitcher.showNext();
        if (v == buttonBack) {
            clearSearchItems();
            viewSwitcher.showPrevious();
        }

        if (v == editDateStart) {
            selectedDate = editDateStart;
            showDatePickerDialog();
        }
        if (v == editDateEnd) {
            selectedDate = editDateEnd;
            showDatePickerDialog();
        }

        if (v == buttonResultClear) {
            clearResults();
        }

        if (v == buttonClear)
            clearSearchItems();

        if (v == buttonSubmit) {
            Boolean viableSearch = true;
            if (!cbIncident.isChecked() && !cbWorkPlanned.isChecked() && !cbWorkCurrent.isChecked()) {
                Toast.makeText(this, "Please select what data to display", Toast.LENGTH_LONG).show();
                viableSearch = false;
            }
            if (rbRange.isChecked()) {
                String input = origin.getText().toString();
                if (input.matches("")) {
                    Toast.makeText(this, "Please enter a location", Toast.LENGTH_LONG).show();
                    viableSearch = false;
                }
            }
            if (viableSearch) {
                clearResults();
                Location org = new Location("origin");
                if (!origin.getText().toString().matches("")) {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(origin.getText().toString(), 20);
                        if (addresses.size() > 0) {
                            setLocationPoint(addresses, org);

                            int range = barRange.getProgress();
                            boolean checkRange = rbRange.isChecked();
                            boolean searchAll = false;
                            if (barRange.getProgress() == barRange.getMax())
                                searchAll = true;
                            resultFragment.setSearchParameters(dateStart, dateEnd, cbWorkCurrent.isChecked(), cbWorkPlanned.isChecked(), cbIncident.isChecked(), org, checkRange, range, searchAll, origin.getText().toString());
                            resultFragment.PerformResultTask();
                            viewSwitcher.showPrevious();
                            clearSearchItems();
                        } else if (rbLocation.isChecked()) {
                            int range = barRange.getProgress();
                            boolean checkRange = rbRange.isChecked();
                            boolean searchAll = false;
                            if (barRange.getProgress() == barRange.getMax())
                                searchAll = true;
                            resultFragment.setSearchParameters(dateStart, dateEnd, cbWorkCurrent.isChecked(), cbWorkPlanned.isChecked(), cbIncident.isChecked(), org, checkRange, range, searchAll, origin.getText().toString());
                            resultFragment.PerformResultTask();
                            viewSwitcher.showPrevious();
                            clearSearchItems();
                        } else {
                            Toast.makeText(this, "Location not found: please try again", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    int range = barRange.getProgress();
                    boolean checkRange = rbRange.isChecked();
                    boolean searchAll = false;
                    if (barRange.getProgress() == barRange.getMax())
                        searchAll = true;
                    resultFragment.setSearchParameters(dateStart, dateEnd, cbWorkCurrent.isChecked(), cbWorkPlanned.isChecked(), cbIncident.isChecked(), org, checkRange, range, searchAll, origin.getText().toString());
                    resultFragment.PerformResultTask();
                    viewSwitcher.showPrevious();
                    clearSearchItems();
                }
            }
        }
    }

    private void setLocationPoint(List<Address> addresses, Location org){
        Address address = addresses.get(0);
        org.setLatitude(address.getLatitude());
        org.setLongitude(address.getLongitude());

        LatLng position = new LatLng(org.getLatitude(), org.getLongitude());

        String addressString = "";
        if (address.getSubThoroughfare() != null)
            addressString += address.getSubThoroughfare() + ", ";
        if (address.getThoroughfare() != null)
            addressString += address.getThoroughfare() + ", ";
        if (address.getLocality() != null)
            addressString += address.getLocality() + ", ";
        if (address.getCountryName() != null)
            addressString += address.getCountryName();
        if (address.getPostalCode() != null)
            addressString += ", " + address.getPostalCode();

        locationMarker = position;
        map.addMarker(new MarkerOptions().position(position).title(origin.getText().toString()).snippet(addressString).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }


    private void clearResults(){
        locationMarker = null;
        resultList = null;
        map.clear();
        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG_A);
        mListFragment.clearList();
        mListFragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(LIST_TAG_B);
        mListFragment.clearList();
        if (markers != null && markers.size() > 0)
            markers.clear();
    }

} // End of MainActivity