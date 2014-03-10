/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan Schulze on 2014/03/09.
 */
public class Session {
    // Session details
    private long mId;
    private String mName;
    private int mNumGames;
    private int mNumPlayers;
    private ArrayList<SessionPlayer> mPlayerList;
    private ArrayList<Game> mGames;
    private long mTimeOfCreation;

    public Session(long id, String sessionName) {
        mId = id;
        mName = sessionName;
        mNumGames = 0;
        mNumPlayers = 0;
        // get current time
        final Date date = new Date();
        mTimeOfCreation = date.getTime();
        // init arrays
        mPlayerList = new ArrayList<SessionPlayer>();
        mGames = new ArrayList<Game>();
    }

    private Session() {
        // shall not be used
    }

    public long getId() {
        return mId;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) { mName = name; }
    public int getPlayedGames() {
        return mNumGames;
    }
    public long getTimeOfCreation() { return mNumGames; }
    public int getNumberOfPlayers() {
        return mNumPlayers;
    }

    /**
     * Add player to session before first game played
     * @param playerId Unique player ID taken from PlayerList
     * @return true, if player was added;
     */
    public boolean addPlayer(long playerId) {
        if (mNumGames > 0) {
            // players can only be added at the beginning of the session
            return false;
        }
        SessionPlayer player = new SessionPlayer(playerId);
        mPlayerList.add(player);
        mNumPlayers++;
        return true;
    }

    /**
     * Add finished game to session
     * @param playedGame Finished game to be added
     * @return Returns true if added to end of session list. Return false if not added successfully.
     *         E.g. not the same amount of players as in session.
     */
    public boolean addGame(Game playedGame) {
        // Check if game fits with session configuration
        if (mNumPlayers != playedGame.getNumPlayers()) {
            return false;
        }

        // Add finished game to session
        mGames.add(playedGame);

        // subtract information from game
        for (int playerPosition = 0; playerPosition < mNumPlayers; playerPosition++) {
            SessionPlayer player = mPlayerList.get(playerPosition);
            if (playedGame.getmGameResults()[playerPosition] == Game.eGameResult.LOST) {
                player.addLostGames();
            } else if (playedGame.getmGameResults()[playerPosition] == Game.eGameResult.WON) {
                player.addWonGame();
            }
            mPlayerList.set(playerPosition, player);
        }
        return true;
    }


    /**
     * Details of player in current session
     */
    private class SessionPlayer {
        // Player details
        private long mId;
        private int mWonGames;
        private int mLostGames;

        public SessionPlayer(long playerId) {
            mId = playerId;
            mWonGames = 0;
            mLostGames = 0;
        }
        private SessionPlayer() {
            // shall not be used
        }

        public int getLostGames() { return mLostGames; }
        public void addLostGames() {
            mLostGames++;
        }

        public int getWonGames() { return mWonGames; }
        public void addWonGame() {
            mWonGames++;
        }

        public long getId() { return mId; }
    }
}