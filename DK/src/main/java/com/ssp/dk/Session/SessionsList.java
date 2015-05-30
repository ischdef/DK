/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.Session;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.ssp.dk.Game.Game;

import java.util.ArrayList;
import java.util.Date;
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

    // database
    SessionsListDbHelper mDbHelper = null;

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


    /**
     * Get stored values from database and copy them to the temporary SessionsList
     */
    private void initSessionsList() {
        // Create SQL Database Helper
        mDbHelper = new SessionsListDbHelper(mContext);

        // Open database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Query all sessions
        Cursor sessionsCursor = db.query(
                SessionsListDbEntry.TABLE_NAME,     // The table to query
                new String[] {"*"},                 // return all columns
                null,                               // The columns for the WHERE clause
                null,                               // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                SessionsListDbEntry._ID + " ASC"    // Ascending sort order by ID
        );

        // Copy sessions from DB to temporary SessionsList
        if (sessionsCursor != null) {
            // cursor position after query is before first row
            while (sessionsCursor.moveToNext()) {
                // Get player parameters from database
                long sessionId = sessionsCursor.getLong(sessionsCursor.getColumnIndexOrThrow(SessionsListDbEntry._ID));
                String sessionName = sessionsCursor.getString(sessionsCursor.getColumnIndex(SessionsListDbEntry.COLUMN_SESSION_NAME));
                long sessionDate = sessionsCursor.getLong(sessionsCursor.getColumnIndex(SessionsListDbEntry.COLUMN_SESSION_CREATION_DATE));
                int sessionStarted = sessionsCursor.getInt(sessionsCursor.getColumnIndex(SessionsListDbEntry.COLUMN_SESSION_STARTED));

                // create session with parameters extracted from DB
                Session session = new Session(sessionId, sessionName, sessionDate);

                // Query all session players
                Cursor playersCursor = db.query(
                        mDbHelper.getPlayerListTableName(sessionId), // The table to query
                        new String[] {"*"},                   // return all columns
                        null,                                 // The columns for the WHERE clause
                        null,                                 // The values for the WHERE clause
                        null,                                 // don't group the rows
                        null,                                 // don't filter by row groups
                        SessionPlayerListDbEntry._ID + " ASC" // Ascending sort order by ID
                );
                // Copy players from DB to session
                if (playersCursor != null) {
                    // cursor position after query is before first row
                    while (playersCursor.moveToNext()) {
                        // Get player parameters from database
                        //long playerPosition = sessionsCursor.getLong(playersCursor.getColumnIndexOrThrow(SessionPlayerListDbEntry._ID));
                        long playerId = playersCursor.getLong(playersCursor.getColumnIndex(SessionPlayerListDbEntry.COLUMN_PLAYER_ID));
                        // add player to session
                        session.addPlayer(playerId);
                    }
                    playersCursor.close();
                }

                // Copy games if session was already started, otherwise games table does not exist
                if (sessionStarted == 1) {
                    // Query all session games
                    Cursor gamesCursor = db.query(
                            mDbHelper.getGameListTableName(sessionId), // The table to query
                            new String[] {"*"},                 // return all columns
                            null,                               // The columns for the WHERE clause
                            null,                               // The values for the WHERE clause
                            null,                               // don't group the rows
                            null,                               // don't filter by row groups
                            SessionGameListDbEntry._ID + " ASC" // Ascending sort order by ID
                    );
                    // Copy games from DB to session
                    if (gamesCursor != null) {
                        // cursor position after query is before first row
                        while (gamesCursor.moveToNext()) {
                            Game game = new Game(session.getNumberOfPlayers());
                            // Get game scores from all players
                            for (int playerPosition = 0; playerPosition < session.getNumberOfPlayers(); playerPosition++) {
                                String scoreColumnName = mDbHelper.getGameListTableScoreColumnName(playerPosition);
                                String resultColumnName = mDbHelper.getGameListTableResultColumnName(playerPosition);
                                int score = gamesCursor.getInt(gamesCursor.getColumnIndex(scoreColumnName));
                                int result = gamesCursor.getInt(gamesCursor.getColumnIndex(resultColumnName));
                                game.setScore(playerPosition, score, Game.eGameResult.values()[result]);
                            }
                            // add game to session
                            session.addGame(game);
                        }
                        playersCursor.close();
                    }
                }

                // Add session to temporary sessionList
                mSessionsList.add(session);
            }
            sessionsCursor.close();
        }

        // Close database again after usage
        db.close();
    }

    /**
     * Add an empty session to the sessionsList
     * @param sessionName Name of new session
     * @return number of sessions in list
     */
    public int addSession(String sessionName) {
        // get creation date of session
        long date = new Date().getTime();

        // Open database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Add session parameters to new row in the SessionsList table
        ContentValues values = new ContentValues();
        values.put(SessionsListDbEntry.COLUMN_SESSION_NAME, sessionName);
        values.put(SessionsListDbEntry.COLUMN_SESSION_CREATION_DATE, date);
        values.put(SessionsListDbEntry.COLUMN_SESSION_STARTED, 0); // 0 = Session not yet started
        long sessionId = db.insert(SessionsListDbEntry.TABLE_NAME, null, values);

        // add new empty SessionPlayerList table
        mDbHelper.addPlayerList(db, sessionId);

        // Close database again after usage
        db.close();

        // Add session to temporary SessionsList
        Session session = new Session(sessionId, sessionName, date);
        mSessionsList.add(session);

        return getNumberOfSessions();
    }

    /**
     * Permanently delete session from sessionList
     * @param sessionId ID of session to be deleted
     * @return number of sessions in list
     */
    public int deleteSession(long sessionId) {
        int sessionPosition = getSessionsListPositionBySessionId(sessionId);
        Session session = getSessionByPosition(sessionPosition);

        // Start removing from database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // If session was already started, remove SessionGamList table
        if (session.getPlayedGames() > 0) {
            mDbHelper.removeGameList(db, sessionId);
        }
        // Remove SessionPlayerList table
        mDbHelper.removePlayerList(db, sessionId);
        // Remove session from SessionList table
        String selection = SessionsListDbEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(sessionId) };
        db.delete(SessionsListDbEntry.TABLE_NAME, selection, selectionArgs);

        // Close database again after usage
        db.close();

        // Remove from temporary SessionsList
        mSessionsList.remove(getSessionsListPositionBySessionId(sessionId));

        return getNumberOfSessions();
    }

    /**
     * Rename an existing session
     * @param sessionName Name of new session
     * @param sessionId Unique ID of session to be renamed
     */
    public void renameSession(String sessionName, long sessionId) {
         // Open database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Create a new map of values, to only update the session's name
        ContentValues values = new ContentValues();
        values.put(SessionsListDbEntry.COLUMN_SESSION_NAME, sessionName);
        // Which row to update, based on the ID
        String selection = SessionsListDbEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(sessionId) };
        db.update(SessionsListDbEntry.TABLE_NAME, values, selection, selectionArgs);
        // Close database again after usage
        db.close();

        // Rename session in temporary SessionsList
        int position = getSessionsListPositionBySessionId(sessionId);
        Session session = getSessionByPosition(position);
        session.setName(sessionName);
        mSessionsList.set(position, session);
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


    public boolean replaceSessionPlayerList(long sessionId, long[] newPlayerIds) {
        // Update first in temporary session
        Session session = getSessionById(sessionId);
        if (!session.replacePlayerList(newPlayerIds)) {
            // players can only be changed at the beginning of the session
            return false;
        }

        // Open database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Delete current SessionPlayerList table in DB
        mDbHelper.removePlayerList(db, sessionId);
        // add new empty SessionPlayerList table
        mDbHelper.addPlayerList(db, sessionId);
        // add new players to SessionPlayerList
        String playerTableName = mDbHelper.getPlayerListTableName(sessionId);
        for (int playerPosition = 0; playerPosition < session.getNumberOfPlayers(); playerPosition++) {
            ContentValues values = new ContentValues();
            values.put(SessionPlayerListDbEntry.COLUMN_PLAYER_ID, newPlayerIds[playerPosition]);
            db.insert(playerTableName, null, values);
        }
        // Close database again after usage
        db.close();

        return true;
    }

    /**
     * Add a played game to the end of the gameList of the selected session
     * @param sessionId Unique ID of session
     * @param game Played game to be added
     * @return true if successfully added
     */
    public boolean addGameToSession(long sessionId, Game game) {
        // Update session in temporary SessionsList
        int sessionPosition = getSessionsListPositionBySessionId(sessionId);
        Session session = getSessionByPosition(sessionPosition);
        if (!session.addGame(game)) {
            // invalid number of players
            return false;
        }
        // TODO check if needed, because getSession should return reference, so session would already be set
        mSessionsList.set(sessionPosition, session);

        // Open database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // if this is the first game added to the session, start session
        if (session.getPlayedGames() == 1) {
            // set "session started" in DB
            ContentValues values = new ContentValues();
            values.put(SessionsListDbEntry.COLUMN_SESSION_STARTED, 1); // 1 = Session started
            // Which row to update, based on the session ID
            String selection = SessionsListDbEntry._ID + " LIKE ?";
            String[] selectionArgs = { String.valueOf(sessionId) };
            db.update(SessionsListDbEntry.TABLE_NAME, values, selection, selectionArgs);

            // create SessionGameList table in DB
            mDbHelper.addGameList(db, sessionId, game.getNumPlayers());
        }

        // Add game to new row in the session's gameList table
        ContentValues values = new ContentValues();
        for (int playerPosition = 0; playerPosition < game.getNumPlayers(); playerPosition++) {
            String scoreColumnName = mDbHelper.getGameListTableScoreColumnName(playerPosition);
            String resultColumnName = mDbHelper.getGameListTableResultColumnName(playerPosition);
            values.put(scoreColumnName, game.getScore(playerPosition));
            values.put(resultColumnName, game.getGameResult(playerPosition).getValue());
        }
        db.insert(mDbHelper.getGameListTableName(sessionId), null, values);

        // Close database again after usage
        db.close();

        return true;
    }




    /*******************************************************************************************
     * Databases: SessionsList, SessionPlayerList, SessionGameList
     *
     * - The SessionsList contains a list of sessions.
     * - A session consists of a table head defined by the PlayerList.
     *   The PlayerList defines the order of players participating in this session.
     *   A player is defined by his unique ID.
     * - A session consists of multiple games.
     * - A game consists of a GameScore + GameResult for every player in the PlayerList
     *
     *******************************************************************************************/

    /** Inner class that defines the database contents
     *  Implements BaseColumns to include "_ID" and "_COUNT"
     */
    public static abstract class SessionsListDbEntry implements BaseColumns {
        public static final String TABLE_NAME = "sessionslist";
        public static final String COLUMN_SESSION_NAME  = "sessionname";
        public static final String COLUMN_SESSION_CREATION_DATE = "sessioncreationdate";
        public static final String COLUMN_SESSION_STARTED = "sessionstarted";
    }

    public static abstract class SessionPlayerListDbEntry implements BaseColumns {
        public static final String TABLE_NAME_PREFIX = "sessionplayerslist";
        public static final String COLUMN_PLAYER_ID  = "playerid";
    }

    public static abstract class SessionGameListDbEntry implements BaseColumns {
        public static final String TABLE_NAME_PREFIX = "sessiongamelist";
        public static final String COLUMN_SCORE_PREFIX  = "score";
        public static final String COLUMN_RESULT_PREFIX  = "result";
    }

    public class SessionsListDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "SessionsList.db";

        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE  = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ";

        private static final String SQL_CREATE_SESSIONSLIST_TABLE =
                "CREATE TABLE " + SessionsListDbEntry.TABLE_NAME + " (" +
                        SessionsListDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                        SessionsListDbEntry.COLUMN_SESSION_NAME  + TEXT_TYPE + COMMA_SEP +
                        SessionsListDbEntry.COLUMN_SESSION_CREATION_DATE + INT_TYPE + COMMA_SEP +
                        SessionsListDbEntry.COLUMN_SESSION_STARTED + INT_TYPE +" )";

        private static final String SQL_CREATE_PLAYERLIST_TABLE_COLUMNS =
                 " (" + SessionPlayerListDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                        SessionPlayerListDbEntry.COLUMN_PLAYER_ID + INT_TYPE  + " )";

        private static final String SQL_CREATE_GAMELIST_TABLE_COLUMNS =
                " (" + SessionGameListDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT";


        public SessionsListDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            // only create SessionList table; other tables are still empty/not defined
            db.execSQL(SQL_CREATE_SESSIONSLIST_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // on upgrade drop older tables first an recreate table in new format
            // TODO do upgrade that old data is not lost but converted to new format instead
            db.execSQL(SQL_DELETE_TABLE + SessionsListDbEntry.TABLE_NAME);
            // TODO also delete other tables if they were created later (to be checked in SessionList table)
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        /**
         * Create name of PlayerList table corresponding to the session with the provided session id
         * @param sessionId Unique ID of session
         * @return name of PlayerList table
         */
        public String getPlayerListTableName(long sessionId) {
            return SessionPlayerListDbEntry.TABLE_NAME_PREFIX + sessionId;
        }

        /**
         * Create PlayerList table corresponding to sessionId. The table will be added to the sessions db.
         * @param db SessionsList database
         * @param sessionId Unique ID of session
         */
        public void addPlayerList(SQLiteDatabase db, long sessionId) {
            db.execSQL("CREATE TABLE " + getPlayerListTableName(sessionId) + SQL_CREATE_PLAYERLIST_TABLE_COLUMNS);
        }

        /**
         * Remove PlayerList table corresponding to sessionId. The table will be deleted from the sessions db.
         * @param db SessionsList database
         * @param sessionId Unique ID of session
         */
        public void removePlayerList(SQLiteDatabase db, long sessionId) {
            db.execSQL(SQL_DELETE_TABLE + getPlayerListTableName(sessionId));
        }

        /**
         * Create name of GameList table corresponding to the session with the provided session id
         * @param sessionId Unique ID of session
         * @return name of GameList table
         */
        public String getGameListTableName(long sessionId) {
            return SessionGameListDbEntry.TABLE_NAME_PREFIX + sessionId;
        }

        /**
         * Create name of GameList table score column corresponding to the playerPosition
         * @param playerPosition Unique ID of session
         * @return name of score column table
         */
        public String getGameListTableScoreColumnName(long playerPosition) {
            return SessionGameListDbEntry.COLUMN_SCORE_PREFIX + playerPosition;
        }

        /**
         * Create name of GameList table result column corresponding to the playerPosition
         * @param playerPosition Unique ID of session
         * @return name of result column table
         */
        public String getGameListTableResultColumnName(long playerPosition) {
            return SessionGameListDbEntry.COLUMN_RESULT_PREFIX + playerPosition;
        }

        /**
         * Create GameList table corresponding to sessionId. The table will be added to the sessions db.
         * @param db SessionsList database
         * @param sessionId Unique ID of session
         * @param numPlayers number of players in corresponding PlayerList table
         */
        public void addGameList(SQLiteDatabase db, long sessionId, int numPlayers) {
            String sqlString = "CREATE TABLE " + getGameListTableName(sessionId) + SQL_CREATE_GAMELIST_TABLE_COLUMNS;
            // add columns for score and result for every player in this session
            for (int playerPosition = 0; playerPosition < numPlayers; playerPosition++) {
                sqlString = sqlString + COMMA_SEP + getGameListTableScoreColumnName(playerPosition) + INT_TYPE
                                      + COMMA_SEP + getGameListTableResultColumnName(playerPosition) + INT_TYPE;
            }
            sqlString = sqlString + " )";
            db.execSQL(sqlString);
        }

        /**
         * Remove GameList table corresponding to sessionId. The table will be deleted from the sessions db.
         * @param db SessionsList database
         * @param sessionId Unique ID of session
         */
        public void removeGameList(SQLiteDatabase db, long sessionId) {
            db.execSQL(SQL_DELETE_TABLE + getGameListTableName(sessionId));
        }
    }
}
