package com.mpd.mpdproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class WidgetClass implements Parcelable {

    protected WidgetClass(Parcel in) {
        title = in.readString();
        delayInfo = in.readString();
        lat = in.readFloat();
        lng = in.readFloat();
        duration = in.readFloat();
        type = TYPE.values()[in.readInt()];
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
    }

    public static final Creator<WidgetClass> CREATOR = new Creator<WidgetClass>() {
        @Override
        public WidgetClass createFromParcel(Parcel in) {
            return new WidgetClass(in);
        }

        @Override
        public WidgetClass[] newArray(int size) {
            return new WidgetClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(delayInfo);
        dest.writeFloat(lat);
        dest.writeFloat(lng);
        dest.writeFloat(duration);
        dest.writeInt(type.ordinal());
        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());
    }

    public enum TYPE{ROADWORK_CURRENT, ROADWORK_PLANNED, INCIDENT};
    private TYPE type;
    private Date startDate, endDate;
    private String title;
    private String delayInfo;
    private float lat, lng;
    private float duration;

//    protected WidgetClass(Parcel in) {
//        title = in.readString();
//        delayInfo = in.readString();
//        lat = in.readFloat();
//        lng = in.readFloat();
//    }
//
//    public static final Creator<WidgetClass> CREATOR = new Creator<WidgetClass>() {
//        @Override
//        public WidgetClass createFromParcel(Parcel in) {
//            return new WidgetClass(in);
//        }
//
//        @Override
//        public WidgetClass[] newArray(int size) {
//            return new WidgetClass[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(title);
//        dest.writeString(delayInfo);
//        dest.writeFloat(lat);
//        dest.writeFloat(lng);
//        dest.writeSerializable(startDate);
//        dest.writeSerializable(endDate);
//        dest.writeSerializable(type);
//    }

    public WidgetClass(){
        this.title = "";

        this.type = null;

        this.startDate = new Date();
        this.endDate = new Date();

        delayInfo = "";

        lat = 0.0f;
        lng = 0.0f;

        duration = 0;
    }

    public WidgetClass(TYPE type, String title, Date startDate, Date endDate, String delayInfo, float lat, float lng){
        this.type = type;

        this.title = title;

        this.startDate = startDate;
        this.endDate = endDate;

        this.delayInfo = delayInfo;

        this.lat = lat;
        this.lng = lng;
    }

    // TITLE
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }

    // TYPE
    public void setType(TYPE type){
        this.type = type;
    }
    public TYPE getType(){
        return this.type;
    }

    // START VALUES
    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }
    public Date getStartDate(){
        return this.startDate;
    }

    // END VALUES
    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }
    public Date getEndDate(){
        return this.endDate;
    }

    public void setDuration(float duration){ this.duration = duration; }
    public void setDuration(Long duration){ this.duration = duration; }
    public float getDuration(){ return this.duration; }
    public void calculateDuration(){
        duration = Math.abs(startDate.getTime() - endDate.getTime());
        duration = (duration / (24 * 60 * 60 * 1000));
        duration++;
    }

    // INFO
    public void setDelayInfo(String info){
        delayInfo = info;
    }
    public String getDelayInfo(){
        return delayInfo;
    }

    // COORDINATES
    public void setCoordinates(float lat, float lng){
        this.lat = lat;
        this.lng = lng;
    }
    public float getCoordLat(){
        return lat;
    }
    public float getCoordLng(){
        return lng;
    }

}
