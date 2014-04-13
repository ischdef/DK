/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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
        testSession.addPlayer(1);
        testSession.addPlayer(2);
        testSession.addPlayer(3);
        testSession.addPlayer(4);
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
        mSessionsList.remove(getSessionsListPositionBySessionId(sessionId));

        // TODO Remove from database

        return getNumberOfSessions();
    }

    /**
     * Rename an existing session
     * @param sessionName Name of new session
     * @param sessionId Unique ID of session to be renamed
     */
    public int renameSession(String sessionName, long sessionId) {
        // TODO update database
        // ...

        // Rename session in temporary SessionsList
        int position = getSessionsListPositionBySessionId(sessionId);
        Session session = getSessionByPosition(position);
        session.setName(sessionName);
        mSessionsList.set(position, session);

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
    private int getSessionsListPositionBySessionId(long sessionId) {
        // check complete list till (first) session with sessionId is found
        int numSessions = getNumberOfSessions();
        for (int listPos = 0; listPos < numSessions; listPos++) {
            if (mSessionsList.get(listPos).getId() == sessionId)
                return listPos;
        }
        // no session with sessionId was found
        return -1;
    }



    /** Inner class that defines the database contents.
     *  Implements BaseColumns to include "_ID" and "_COUNT"
     */
    public static abstract class SessionsListDbEntry implements BaseColumns {
        public static final String TABLE_NAME = "sessionslist";
        public static final String COLUMN_SESSION_NAME  = "sessionname";
        public static final String COLUMN_SESSION_CREATION_DATE = "sessioncreationdate";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE  = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SessionsListDbEntry.TABLE_NAME + " (" +
                    SessionsListDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    SessionsListDbEntry.COLUMN_SESSION_NAME  + TEXT_TYPE + COMMA_SEP +
                    SessionsListDbEntry.COLUMN_SESSION_CREATION_DATE + INT_TYPE  +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SessionsListDbEntry.TABLE_NAME;

    public class SessionsListDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "SessionsList.db";

        public SessionsListDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
