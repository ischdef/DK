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

    /**
     * Get only available PlayerList instance
     */
    public static PlayerList getInstance() {
        return mInstance;
    }

    /**
     * Get PlayerList array
     * @return PlayerList array
     */
    public List<Player> getList() {
        return mPlayerList;
    }

    /**
     * Get player from playerList by position in list
     * @param listPosition position of player to be received
     * @return selected player
     */
    public Player getPlayer(int listPosition) {
        return mPlayerList.get(listPosition);
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
     * @param playerName name of player to be added
     * @param playerImage image of player to be added
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

    /**
     * Permanently delete player from playerList
     * @param position position of player in list to be deleted
     * @return true if player was deleted
     */
    public boolean deletePlayer(int position) {
        // First remove from temporary PlayerList
        mPlayerList.remove(position);

        // TODO Second remove from internal storage
        // ...

        return true;
    }

    /**
     * Change name and/or picture of player
     * @param position position of player in list to be changed
     * @param playerName name of player to be changed
     * @param playerImage image of player to be changed
     * @return true if player was changed
     */
    public boolean editPlayer(int position, String playerName, Drawable playerImage) {
        // Create Player
        Player player = new Player(playerName, playerImage);

        // First overwrite in temporary PlayerList
        mPlayerList.set(position, player);

        // TODO Second update internal storage
        // ...

        return true;
    }
}
