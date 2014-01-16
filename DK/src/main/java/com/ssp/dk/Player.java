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
    private String mName;
    private Drawable mImage;
    private int mPlayedGames;
    private int mWonGames;
    private int mLostGames;

    public Player(String playerName, Drawable image) {
        mName = playerName;
        mImage = image;
        mPlayedGames = 0;
        mWonGames = 0;
        mLostGames = 0;
    }

    private Player() {
        // shall not be used
    }

    public String getPlayerName() {
        return mName;
    }

    public void setPlayerName(String playerName) { mName = playerName; }

    public int getPlayedGames() {
        return mPlayedGames;
    }

    public void setPlayedGames(int playedGames) {
        mPlayedGames = playedGames;
    }

    public int getWonGames() {
        return mWonGames;
    }

    public void setWonGames(int wonGames) {
        mWonGames = wonGames;
    }

    public int getLostGames() {
        return mLostGames;
    }

    public void setLostGames(int lostGames) {
        mLostGames = lostGames;
    }

    public Drawable getImage() { return mImage; }

    public void setImage(Drawable image) { mImage = image; }
}

