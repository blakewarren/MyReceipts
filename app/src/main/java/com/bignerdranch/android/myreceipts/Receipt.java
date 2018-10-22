package com.bignerdranch.android.myreceipts;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Date;
import java.util.UUID;

public class Receipt {
    private UUID mId;
    private String mTitle;
    private String mShopName;
    private String mComment;
    private Date mDate;
    private double mLon;
    private double mLat;
    private Location mLocation;

    public Receipt() {
        this(UUID.randomUUID());
    }

    public Receipt(UUID id){
        mId = id;
        mDate = new Date();

    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getShopName() {
        return mShopName;
    }

    public void setShopName(String shopName) {
        mShopName = shopName;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;

    }public String getPhotoFilename(){
        return "IMG_" + getId().toString() + ".jpg";
    }
}
