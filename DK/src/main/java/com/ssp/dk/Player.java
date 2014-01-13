/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.net.Uri;

/**
 * Created by Stefan Schulze on 2014/01/13.
 */

public class Player {
    // Player details
    private String mPlayerName;
    private Uri mImageUri;
    private int mPlayedGames;
    private int mWonGames;
    private int mLostGames;

    private Player(String playerName, Uri imageUri) {
        mPlayerName = playerName;
        mImageUri = imageUri;
        mPlayedGames = 0;
        mWonGames = 0;
        mLostGames = 0;
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setPlayerName(String playerName) { mPlayerName = playerName; }

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

    public Uri getImageUri() { return mImageUri; }

    public void setImageUri(Uri imageUri) { mImageUri = imageUri; }
}

