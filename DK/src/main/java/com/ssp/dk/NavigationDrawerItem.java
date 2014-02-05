/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

/**
 * Created by Stefan Schulze on 2014/02/04.
 */
public class NavigationDrawerItem {

    private String mTitle;
    private int mIcon;
    private String mCount = "0";
    // boolean to set visibility of the counter
    private boolean mIsCounterVisible = false;

    public NavigationDrawerItem() {}

    public NavigationDrawerItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
        mIsCounterVisible = false;
    }

    public NavigationDrawerItem(String title, int icon, boolean isCounterVisible, String count) {
        mTitle = title;
        mIcon = icon;
        mIsCounterVisible = isCounterVisible;
        mCount = count;
    }

    public String getTitle() { return mTitle; }
    public int getIcon() { return mIcon; }
    public String getCount() { return mCount; }
    public boolean getCounterVisibility() { return mIsCounterVisible; }

    public void setTitle(String title) { mTitle = title; }
    public void setIcon(int icon) { this.mIcon = icon; }
    public void setCount(String count) { this.mCount = count; }
    public void setCounterVisibility(boolean isCounterVisible) { this.mIsCounterVisible = isCounterVisible; }

}
