/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Schulze on 2014/03/11.
 */
public class SessionsList {

    // Singleton design pattern - create only instance during first class access
    private static SessionsList mInstance = null;
    private static Context mContext = null;

    // temporary SessionsList
    private List<Session> mSessionsList = new ArrayList<Session>();

    // TODO database
    //SessionsListDbHelper mDbHelper = null;

    // Default constructor not accessible from outside
    private SessionsList() {
        initSessionsList();
    }


    /**
     * Create only available SessionsList instance
     * @param context Context of caller
     * @return Created instance if called first time, otherwise already created instance
     */
    public static SessionsList createInstance(Context context) {
        if (mInstance == null) {
            mContext = context;
            mInstance = new SessionsList();
        }
        return mInstance;
    }

    /**
     * Get only available SessionsList instance
     */
    public static SessionsList getInstance() {
        return mInstance;
    }

    /**
     * Get SessionsList array
     * @return SessionsList array
     */
    public List<Session> getList() {
        return mSessionsList;
    }

    /**
     * Get number of players in the player list
     * @return number of players in list
     */
    public int getNumberOfSessions() {
        return mSessionsList.size();
    }


    // TODO init sessionsList
    private void initSessionsList() {
        // TODO remove Debug code
        // Add test sessions
        Session testSession = new Session(0, "TestSession");
        testSession.addPlayer(0);
        testSession.addPlayer(1);
        Game testGame = new Game(testSession.getNumberOfPlayers());
        testGame.setScore(0, 5, Game.eGameResult.WON);
        testGame.setScore(1, -5, Game.eGameResult.LOST);
        testSession.addGame(testGame);
        testSession.addGame(testGame);
        mSessionsList.add(testSession);
    }
}
