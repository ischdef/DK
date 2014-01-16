/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Schulze on 2014/01/16.
 */
public class PlayerList {
    // Singleton design pattern - create only instance during first class access
    private static PlayerList mInstance = new PlayerList();

    // temporary PlayerList
    private List<Player> mPlayerList = new ArrayList<Player>();

    // Default constructor not accessible from outside
    private PlayerList() {
        initPlayerList();
    }

     // Get only available PlayerList instance
    public static PlayerList getInstance() {
        return mInstance;
    }


    /**
     * get previous playerList from internal storage and copy to temporary PlayerList
     */
    private void initPlayerList() {
        // TODO get previous playerList from internal storage and copy to temporary PlayerList
        // ...
    }

    /**
     * Add a player to the permanent PlayerList
     * @param playerName Name of player to be added
     * @param imageUri Link to image of player to be added
     * @return true if player was successfully stored
     */
    public boolean addPlayer(String playerName, Drawable playerImage) {
        // First create Player
        Player player = new Player(playerName, playerImage);
        // First add to temporary PlayerList
        mPlayerList.add(player);

        // TODO Second add to internal storage
        // ...

        return true;
    }

}
