/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.Player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;

import com.ssp.dk.R;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Schulze on 2014/01/16.
 */
public class PlayerList {
    // Singleton design pattern - create only instance during first class access
    private static PlayerList mInstance = null;
    private static Context mContext = null;

    // temporary PlayerList
    private List<Player> mPlayerList = new ArrayList<Player>();

    // database
    PlayerListDbHelper mDbHelper = null;

    // Default constructor not accessible from outside
    private PlayerList() {
        initPlayerList();
    }


    /**
     * Create only available PlayerList instance
     * @param context Context of caller
     * @return Created instance if called first time, otherwise already created instance
     */
    public static PlayerList createInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new PlayerList();
        }
        return mInstance;
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
     * Get number of players in the player list
     * @return number of players in list
     */
    public int getNumberOfPlayers() {
        return mPlayerList.size();
    }
    /**
     * Get player from playerList by position in list
     * @param listPosition position of player to be received
     * @return selected player; null of invalid listPosition
     */
    public Player getPlayerByPosition(int listPosition) {
        try {
            return mPlayerList.get(listPosition);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get player from playerList by unique player ID
     * @param playerId unique player ID
     * @return selected player; null if player was not found
     */
    public Player getPlayerById(long playerId) {
        // for-each loop of complete array of players
        for (Player player : mPlayerList) {
            if (player.getId() == playerId)
                return player;
        }
        // no player with playerId was found
        return null;
    }

    /**
     * Get playerList position of player with given playerId
     * @param playerId ID of selected player
     * @return playerList position; -1 if invalid playerId was given
     */
    private int getPlayerListPositionByPlayerId(long playerId) {
        // check complete list till (first) player with playerId is found
        int numPlayers = mPlayerList.size();
        for (int listPos = 0; listPos < numPlayers; listPos++) {
            if (mPlayerList.get(listPos).getId() == playerId)
                return listPos;
        }
        // no player with playerId was found
        return -1;
    }

    /**
     * get previous playerList from internal storage and copy to temporary PlayerList
     */
    private void initPlayerList() {
        // Create SQL Database Helper
        mDbHelper = new PlayerListDbHelper(mContext);

        // Open database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns will be used after query
        String[] projection = {
                PlayerListDbEntry._ID,
                PlayerListDbEntry.COLUMN_PLAYER_NAME,
                PlayerListDbEntry.COLUMN_PLAYED_COUNTER,
                PlayerListDbEntry.COLUMN_WON_COUNTER,
                PlayerListDbEntry.COLUMN_LOST_COUNTER
        };
        // Sort by ID in the resulting Cursor
        String sortOrder = PlayerListDbEntry._ID + " ASC";
        // Query all players
        Cursor cursor = db.query(
                PlayerListDbEntry.TABLE_NAME,  // The table to query
                projection,                    // The columns to return
                null,                          // The columns for the WHERE clause
                null,                          // The values for the WHERE clause
                null,                          // don't group the rows
                null,                          // don't filter by row groups
                sortOrder                      // The sort order
        );

        // Copy players from DB to temporary playerList
        if (cursor != null) {
            // cursor position after query is before first row
            while (cursor.moveToNext()) {
                // Get player parameters from database
                long playerId = cursor.getLong(cursor.getColumnIndexOrThrow(PlayerListDbEntry._ID));
                String playerName = cursor.getString(cursor.getColumnIndex(PlayerListDbEntry.COLUMN_PLAYER_NAME));
                int playedGames = cursor.getInt(cursor.getColumnIndex(PlayerListDbEntry.COLUMN_PLAYED_COUNTER));
                int wonGames = cursor.getInt(cursor.getColumnIndex(PlayerListDbEntry.COLUMN_WON_COUNTER));
                int lostGames = cursor.getInt(cursor.getColumnIndex(PlayerListDbEntry.COLUMN_LOST_COUNTER));

                // Get player image from internal storage
                Drawable playerImage = Drawable.createFromPath(getPlayerImageFilePath(playerId));

                // add player to temporary playerList
                Player player = new Player(playerId, playerName, playerImage, playedGames, wonGames, lostGames);
                mPlayerList.add(player);
            }
            cursor.close();
        }

        // Close database again after usage
        db.close();
    }

    /**
     * Add a player to the permanent PlayerList
     * @param playerName name of player to be added
     * @param playerImage image of player to be added
     * @return number of players in list
     */
    public int addPlayer(String playerName, Drawable playerImage) {
        // Open database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PlayerListDbEntry.COLUMN_PLAYER_NAME, playerName);
        values.put(PlayerListDbEntry.COLUMN_PLAYED_COUNTER, 0);
        values.put(PlayerListDbEntry.COLUMN_WON_COUNTER, 0);
        values.put(PlayerListDbEntry.COLUMN_LOST_COUNTER, 0);
        // Insert player as new row, returning the primary key value of the new row
        long playerId = db.insert(PlayerListDbEntry.TABLE_NAME, null, values);
        // Close database again after usage
        db.close();

        // Save image to internal storage
        if (playerImage == null) {
            playerImage = mContext.getResources().getDrawable(R.drawable.no_user_logo);
        }
        savePlayerImageToStorage(playerImage, playerId);

        // Add to temporary PlayerList
        Player player = new Player(playerId, playerName, playerImage, 0, 0, 0);
        mPlayerList.add(player);

        // Debug printout
        //Toast.makeText(mContext, "Player " + player.getName() + " added with ID "
        //        + playerId, Toast.LENGTH_SHORT).show();

        return getNumberOfPlayers();
    }

    /**
     * Permanently delete player from playerList
     * @param playerId ID of player to be deleted
     * @return number of players in list
     */
    public int deletePlayer(long playerId) {
        // Remove from temporary PlayerList
        mPlayerList.remove(getPlayerListPositionByPlayerId(playerId));

        // Open database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = PlayerListDbEntry._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(playerId) };
        // Remove from database
        db.delete(PlayerListDbEntry.TABLE_NAME, selection, selectionArgs);
        // Close database again after usage
        db.close();

        // Delete playerImage on internal storage
        deletePlayerImageFromStorage(playerId);

        return getNumberOfPlayers();
    }

    /**
     * Update player statistics values
     * @param playerId Unique player ID
     * @param playedSessions Number of played sessions
     * @param wonSessions Number of won sessions
     * @param lostSessions Number of lost sessions
     */
    public void updatePlayerStats(long playerId, int playedSessions, int wonSessions, int lostSessions) {
        // Edit temporary playerList
        Player player = getPlayerById(playerId);
        player.setPlayedSessions(playedSessions);
        player.setWonSessions(wonSessions);
        player.setLostSessions(lostSessions);
        mPlayerList.set(getPlayerListPositionByPlayerId(player.getId()), player);

        // Open database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Create a new map of values, to only update stats values
        ContentValues values = new ContentValues();
        values.put(PlayerListDbEntry.COLUMN_PLAYED_COUNTER, playedSessions);
        values.put(PlayerListDbEntry.COLUMN_WON_COUNTER, wonSessions);
        values.put(PlayerListDbEntry.COLUMN_LOST_COUNTER, lostSessions);
        // Which row to update, based on the ID
        String selection = PlayerListDbEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(playerId) };
        db.update(PlayerListDbEntry.TABLE_NAME, values, selection, selectionArgs);
        // Close database again after usage
        db.close();
    }

    /**
     * Change player name or image
     * @param playerId Unique player ID
     * @param playerName Changed player name
     * @param playerImage Changed player image
     */
    public void editPlayer(Long playerId, String playerName, Drawable playerImage) {
        // Edit temporary playerList
        Player player = getPlayerById(playerId);
        player.setName(playerName);
        player.setImage(playerImage);
        mPlayerList.set(getPlayerListPositionByPlayerId(player.getId()), player);

        // Open database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Create a new map of values, to only update the player's name
        ContentValues values = new ContentValues();
        values.put(PlayerListDbEntry.COLUMN_PLAYER_NAME, playerName);
        // Which row to update, based on the ID
        String selection = PlayerListDbEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(playerId) };
        db.update(PlayerListDbEntry.TABLE_NAME, values, selection, selectionArgs);
        // Close database again after usage
        db.close();

        // Save image to internal storage
        savePlayerImageToStorage(playerImage, playerId);
    }



    /**
     * Get filename of stored player image
     * @param playerId Unique player ID
     * @return String of filename
     */
    private String getPlayerImageFileName(long playerId) {
        return "PlayerImage_" + playerId + ".png";
    }

    private String getPlayerImageFilePath(long playerId) {
        return mContext.getFilesDir().getAbsolutePath() + "/" + getPlayerImageFileName(playerId);
    }

    /**
     * Saves given player image as PNG to internal storage
     * @param drawable player image
     * @param playerId Unique player ID
     * @return Absolute file path of saved image
     */
    private String savePlayerImageToStorage(Drawable drawable, long playerId) {
        // Convert Drawable to Bitmap
        Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
        // Try to save bitmap to default application directory
        try
        {
            // File name is defined by th unique player ID
            final FileOutputStream fos =
                    mContext.openFileOutput(getPlayerImageFileName(playerId), Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos); // PNG is lossless, so only storage
            fos.close();
            return getPlayerImageFilePath(playerId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete playerImage from storage
     * @param playerId Unique player ID
     * @return true if file was deleted
     */
    private boolean deletePlayerImageFromStorage(long playerId) {
        return mContext.deleteFile(getPlayerImageFileName(playerId));
    }



    /***********************************
     * Database for PlayerList table
     ***********************************/

    /** Inner class that defines the database contents.
     *  Implements BaseColumns to include "_ID" and "_COUNT"
     */
    public static abstract class PlayerListDbEntry implements BaseColumns {
        public static final String TABLE_NAME = "playerlist";
        public static final String COLUMN_PLAYER_NAME  = "playername";
        public static final String COLUMN_PLAYED_COUNTER = "playedcounter";
        public static final String COLUMN_WON_COUNTER  = "woncounter";
        public static final String COLUMN_LOST_COUNTER = "lostcounter";
    }

    public class PlayerListDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "PlayerList.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE  = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_PLAYERLIST_TABLE =
                "CREATE TABLE " + PlayerListDbEntry.TABLE_NAME + " (" +
                PlayerListDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                PlayerListDbEntry.COLUMN_PLAYER_NAME    + TEXT_TYPE + COMMA_SEP +
                PlayerListDbEntry.COLUMN_PLAYED_COUNTER + INT_TYPE  + COMMA_SEP +
                PlayerListDbEntry.COLUMN_WON_COUNTER    + INT_TYPE  + COMMA_SEP +
                PlayerListDbEntry.COLUMN_LOST_COUNTER   + INT_TYPE  + " )";
        private static final String SQL_DELETE_PLAYERLIST_TABLE = "DROP TABLE IF EXISTS " + PlayerListDbEntry.TABLE_NAME;

        public PlayerListDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_PLAYERLIST_TABLE);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // on upgrade drop older tables first an recreate table in new format
            // TODO do upgrade that old data is not lost but converted to new format instead
            db.execSQL(SQL_DELETE_PLAYERLIST_TABLE);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
