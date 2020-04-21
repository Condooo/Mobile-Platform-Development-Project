package com.mpd.mpdproject;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

public class ResultFragment extends Fragment {
    // Traffic Scotland URLs
    private String urlWorksCurrent = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlWordsPlanned = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlIncidents = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    private LinkedList<WidgetClass> resultList;

    OnDataPass dataPasser;
    int elementsTotal = 0;
    int elementsCurrent = 0;

    private boolean searchWorksCurrent = true;
    private boolean searchWorksPlanned = true;
    private boolean searchIncidents = true;
    private Date startDate = null;
    private Date endDate = null;
    private Location origin = null;
    private int range = 0;
    private boolean checkRange = false;
    private boolean searchAll = false;
    private String input = "";


    public interface OnDataPass{
        void onDataPass(LinkedList<WidgetClass> resultList);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass)context;
    }

    public void setUI(){

    }

    public void PerformResultTask(){
        PopulateResults resultTask = new PopulateResults();
        resultTask.execute();
    }

    private void clearSearchParameters(){
        startDate = null;
        endDate = null;
        searchWorksCurrent = true;
        searchWorksPlanned = true;
        searchIncidents = true;
        origin = null;
        range = 0;
        checkRange = false;
        searchAll = false;
        input = "";
    }

    public void setSearchParameters(Date startDate, Date endDate, boolean searchWorksCurrent, boolean searchWorksPlanned, boolean searchIncidents, Location origin, boolean checkRange, int range, boolean searchAll, String input){
        this.startDate = startDate;
        this.endDate = endDate;
        this.searchWorksCurrent = searchWorksCurrent;
        this.searchWorksPlanned = searchWorksPlanned;
        this.searchIncidents = searchIncidents;
        this.origin = origin;
        this.range = range;
        this.checkRange = checkRange;
        this.searchAll = searchAll;
        this.input = input;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            resultList = (LinkedList<WidgetClass>) savedInstanceState.getSerializable("results");
            Log.e("ResultTag", "Results loaded");
            for (WidgetClass item : resultList){
                Log.e("ResultTag", item.getTitle());
            }
            passData(resultList);
        }
        else {
            Log.e("ResultTag", "New results");
            PopulateResults resultTask = new PopulateResults();
            resultTask.execute();
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

//        VModel = new ViewModelProvider(this).get(ActivityModel.class);
//        final Observer<LinkedList<WidgetClass>> resultObserver = new Observer<LinkedList<WidgetClass>>() {
//            @Override
//            public void onChanged(@Nullable final LinkedList<WidgetClass> newResults) {
//                resultList = newResults;
//            }
//        };
//
//        VModel.getResults().observe(this, resultObserver);


    }

    public void passData(LinkedList<WidgetClass> data){
        dataPasser.onDataPass(data);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private String getResults(String url){
        URL aurl;
        URLConnection yc;
        BufferedReader in = null;
        String inputLine = "";
        String result = "";

        try {
            aurl = new URL(url);
            yc = aurl.openConnection();
            in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            while ((inputLine = in.readLine()) != null)
            {
                String trimInput = inputLine.trim();
                result = result + trimInput;
            }
            in.close();
        } catch (IOException e) {
            Log.e("MyTag", "ioException");
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("results", resultList);
        super.onSaveInstanceState(outState);
    }

    private class PopulateResults extends AsyncTask<Void, Integer, Void> {
        String resultWorksCurrent = "";
        String resultWorksPlanned = "";
        String resultIncidents = "";
        int progress_status;
        TextView progressText = ((MainActivity)getActivity()).getProgressText();
        ProgressBar progressBar = ((MainActivity)getActivity()).getProgressBar();

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progress_status = 0;

            resultList = new LinkedList<WidgetClass>();

            progressText.setText("Parsing data 0%");
            resultWorksCurrent = "";
            resultWorksPlanned = "";
            resultIncidents = "";
            resultList.clear();
            elementsTotal = 0;
            elementsCurrent = 0;
            progressBar.setProgress(0);
            progressText.setVisibility(View.VISIBLE);
            progressText.setText("Connecting...");
        }

        private void calculateElements(String dataToParse){
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(dataToParse));

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT){
                    if (eventType == XmlPullParser.END_TAG){
                        if (parser.getName().equalsIgnoreCase("item")){
                            elementsTotal++;
                        }
                    }
                    eventType = parser.next();
                }

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: DETERMINE RESULTS TO GET

            resultWorksCurrent = getResults(urlWorksCurrent);
            resultWorksPlanned = getResults(urlWordsPlanned);
            resultIncidents = getResults(urlIncidents);

            calculateElements(resultWorksCurrent);
            calculateElements(resultWorksPlanned);
            calculateElements(resultIncidents);

            parseData(resultWorksCurrent, WidgetClass.TYPE.ROADWORK_CURRENT, this, elementsTotal);
            parseData(resultWorksPlanned, WidgetClass.TYPE.ROADWORK_PLANNED, this, elementsTotal);
            parseData(resultIncidents, WidgetClass.TYPE.INCIDENT, this, elementsTotal);

            return null;
        }

        public void doProgress(int value){
            publishProgress(value);
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);

            progressBar.setProgress(values[0]);
            progressText.setText("Parsing data " + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Log.e("List size", Integer.toString(resultList.size()));
            Log.e("Lines", "Current: " + elementsCurrent + ", Total: " + elementsTotal);
            progressText.setVisibility(View.INVISIBLE);
            passData(resultList);
            clearSearchParameters();
        }
    }

    /**
     * Parses the description of the input feed and assigns date and information values to the specified widget
     * @param widget The widget to assign values to
     * @param input The input string to parse data from
     * @param type The information type contained in the input string
     */
    private void parseDescription(WidgetClass widget, String input, MainActivity.descType type){
        String datePattern = "EEEE, dd MMMM yyyy - HH:mm";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        Date date;
        switch (type){
            case START:
                input = input.replace("Start Date:", "");
                input = input.trim();
                try {
                    date = formatter.parse(input);
                    widget.setStartDate(date);
                } catch (ParseException e) {
                    Log.e("StartParseTag", type.toString() + ": Couldn't parse date from input string");
                }
                break;
            case END:
                input = input.replace("End Date:", "");
                input = input.trim();
                try {
                    date = formatter.parse(input);
                    widget.setEndDate(date);
                    widget.calculateDuration();
                } catch (ParseException e) {
                    Log.e("EndParseTag", type.toString() + ": Couldn't parse date from input string");
                }
                break;
            case DELAY:
                input = input.replace("Delay Information: ", "");
                input = input.trim();
                widget.setDelayInfo(input);
                break;
            default:
                break;
        }
    }


    private LinkedList<WidgetClass> parseData(String dataToParse, WidgetClass.TYPE type, PopulateResults task, int elementsTotal){
        WidgetClass widget = null;
        LinkedList<WidgetClass> widgetList = null;
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(dataToParse));
            Log.e("Data to parse", dataToParse);

            int eventType = parser.getEventType();
            parseLoop: while(eventType != XmlPullParser.END_DOCUMENT) {
                // Start tag found
                if (eventType == XmlPullParser.START_TAG) {
                    // Check tag
                    if (parser.getName().equalsIgnoreCase("channel")) {
                        widgetList = new LinkedList<WidgetClass>();
                    } else if (parser.getName().equalsIgnoreCase("item")) {                             // ITEM START
                        widget = new WidgetClass();
                        widget.setType(type);                                                                        // Type
                    } else if (parser.getName().equalsIgnoreCase("title") && widget != null) {           // Title
                        String temp = parser.nextText();
                        widget.setTitle(temp);
                    } else if (parser.getName().equalsIgnoreCase("description") && widget != null) {     // Description
                        String temp = parser.nextText();
                        String[] splitTemp = temp.split("<br />");
                        String startTemp, endTemp, delayTemp;
                        if (splitTemp.length > 0) {
                            startTemp = splitTemp[0];
                            parseDescription(widget, startTemp, MainActivity.descType.START);                                    // Start date
                        }
                        if (splitTemp.length > 1) {
                            endTemp = splitTemp[1];
                            parseDescription(widget, endTemp, MainActivity.descType.END);                                        // End date
                        }
                        if (splitTemp.length > 2) {
                            delayTemp = splitTemp[2];
                            parseDescription(widget, delayTemp, MainActivity.descType.DELAY);                                    // Delay info
                        }
                    } else if (parser.getName().equalsIgnoreCase("point") && widget != null) {           // Coordinates
                        String temp = parser.nextText();
                        String[] coords = temp.split(" ");
                        widget.setCoordinates(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]));
                        String lat = Float.toString(widget.getCoordLat());
                        String lng = Float.toString(widget.getCoordLng());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    // TODO: Stuff
                    if (parser.getName().equalsIgnoreCase("item")) {                                   // ITEM END
                        widgetList.add(widget);
                        widget = null;
                        elementsCurrent++;
                    }
                    //inputLinesCurrent++;
                }
                eventType = parser.next();
                float progress = ((float) elementsCurrent / (float) elementsTotal) * 100;
                task.doProgress((int) progress);

            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        Location dest = new Location("destination");
        for(WidgetClass item:widgetList){
            pollItem(item, geocoder, dest);
        }

        return widgetList;
    }

    private void pollItem(WidgetClass item, Geocoder geocoder, Location dest){
        boolean matchingResult = true;

        // Type
        switch (item.getType()){
            case ROADWORK_CURRENT:
                if (!searchWorksCurrent)
                    matchingResult = false;
                break;
            case ROADWORK_PLANNED:
                if (!searchWorksPlanned)
                    matchingResult = false;
                break;
            case INCIDENT:
                if (!searchIncidents)
                    matchingResult = false;
                break;
            default:
                break;
        }

        // Date
        if (startDate != null){
            if (item.getStartDate().before(startDate))
                matchingResult = false;
        }
        if (endDate != null){
            if (item.getEndDate().after(endDate))
                matchingResult = false;
        }

        // Range
        if (checkRange){
            if (!searchAll) {
                dest.setLatitude((item.getCoordLat()));
                dest.setLongitude(item.getCoordLng());
                float distance = origin.distanceTo(dest);
                if (distance / 1000 > range) {
                    matchingResult = false;
                }
            }
        } else{
            if (!item.getTitle().contains(input))
                matchingResult = false;
        }

        if (matchingResult)
            resultList.add(item);
    }

}
