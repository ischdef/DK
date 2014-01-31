/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * Created by Stefan Schulze on 2014/01/13.
 */

public class Player {
    // Player details
    private long mId;
    private String mName;
    private Drawable mImage;
    private int mPlayedGames;
    private int mWonGames;
    private int mLostGames;

    public Player(long id, String playerName, Drawable image,
                  int playerGames, int wonGames, int lostGames) {
        mId = id;
        mName = playerName;
        mImage = image;
        mPlayedGames = playerGames;
        mWonGames = wonGames;
        mLostGames = lostGames;
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
    public int getPlayedGames() {
        return mPlayedGames;
    }
    public int getWonGames() {
        return mWonGames;
    }
    public int getLostGames() { return mLostGames; }
    public Drawable getImage() { return mImage; }

    public void setName(String name) { mName = name; }
    public void setImage(Drawable image) { mImage = image; }
    public void setPlayedGames(int playedGames) { mPlayedGames = playedGames; }
    public void setWonGames(int wonGames) {mWonGames = wonGames; }
    public void setLostGames(int lostGames) {mLostGames = lostGames; }
}

