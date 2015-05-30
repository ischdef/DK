/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.Player;

import android.graphics.drawable.Drawable;

/**
 * Created by Stefan Schulze on 2014/01/13.
 */

public class Player {
    // Player details
    private long mId;
    private String mName;
    private Drawable mImage;
    private int mPlayedSessions;
    private int mWonSessions;
    private int mLostSessions;

    public Player(long id, String playerName, Drawable image,
                  int playedSessions, int wonSessions, int lostSessions) {
        mId = id;
        mName = playerName;
        mImage = image;
        mPlayedSessions = playedSessions;
        mWonSessions = wonSessions;
        mLostSessions = lostSessions;
    }

    private Player() {
        // shall not be used
    }

    public long getId() {
        return mId;
    }
    public String getName() {
        return mName;
    }
    public int getPlayedSessions() {
        return mPlayedSessions;
    }
    public int getWonSessions() {
        return mWonSessions;
    }
    public int getLostSessions() { return mLostSessions; }
    public Drawable getImage() { return mImage; }

    public void setName(String name) { mName = name; }
    public void setImage(Drawable image) { mImage = image; }
    public void setPlayedSessions(int playedSessions) { mPlayedSessions = playedSessions; }
    public void setWonSessions(int wonSessions) { mWonSessions = wonSessions; }
    public void setLostSessions(int lostSessions) { mLostSessions = lostSessions; }
}

