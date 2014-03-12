/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    /**
     * Add an empty session to the sessionsList
     * @param sessionName Name of new session
     * @return number of sessions in list
     */
    public int addSession(String sessionName) {
        // TODO set unique session ID by creating session database
        // ...

        // Add session to temporary SessionsList (currently ID = getNumberOfSessions)
        Session session = new Session(getNumberOfSessions(), sessionName);
        mSessionsList.add(session);

        return getNumberOfSessions();
    }

    /**
     * Permanently delete session from sessionList
     * @param sessionId ID of session to be deleted
     * @return number of sessions in list
     */
    public int deleteSession(long sessionId) {
        // Remove from temporary SessionsList
        mSessionsList.remove(getSessionsListPositionByPlayerId(sessionId));

        // TODO Remove from database

        return getNumberOfSessions();
    }

    /**
     * Get session from sessionsList by position in list
     * @param listPosition position of session to be received
     * @return selected session; null of invalid listPosition
     */
    public Session getSessionByPosition(int listPosition) {
        try {
            return mSessionsList.get(listPosition);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Get session from sessionsList by unique session ID
     * @param sessionId unique session ID
     * @return selected session; null if session was not found
     */
    public Session getSessionById(long sessionId) {
        // for-each loop of complete array of players
        for (Session session : mSessionsList) {
            if (session.getId() == sessionId)
                return session;
        }
        // no session with sessionId was found
        return null;
    }

    /**
     * Get sessionsList position of player with given sessionId
     * @param sessionId ID of selected session
     * @return sessionsList position; -1 if invalid sessionId was given
     */
    private int getSessionsListPositionByPlayerId(long sessionId) {
        // check complete list till (first) session with sessionId is found
        int numSessions = getNumberOfSessions();
        for (int listPos = 0; listPos < numSessions; listPos++) {
            if (mSessionsList.get(listPos).getId() == sessionId)
                return listPos;
        }
        // no session with sessionId was found
        return -1;
    }
}
