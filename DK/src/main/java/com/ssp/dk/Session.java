/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import java.util.ArrayList;

/**
 * Created by Stefan Schulze on 2014/03/09.
 */
public class Session {
    // Session details
    private long mId;
    private String mName;
    private ArrayList<SessionPlayer> mPlayerList;
    private ArrayList<Game> mGames;
    private long mTimeOfCreation;

    public Session(long id, String sessionName, long sessionCreationDate) {
        mId = id;
        mName = sessionName;
        // set current time
        mTimeOfCreation = sessionCreationDate;
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
    public int getPlayedGames() { return mGames.size(); }
    public long getTimeOfCreation() { return mTimeOfCreation; }
    public int getNumberOfPlayers() {
        return mPlayerList.size();
    }

    /**
     * Add player to session before first game played
     * @param playerId Unique player ID taken from PlayerList
     */
    public void addPlayer(long playerId) {
        SessionPlayer player = new SessionPlayer(playerId);
        mPlayerList.add(player);
    }

    /**
     * Get list with all players in session
     * @return SessionPlayerList
     */
    public ArrayList<SessionPlayer> getSessionPlayerList() {
        return mPlayerList;
    }

    /**
     * Replace existing session player list with new players
     * @param playerIds List with IDs of new players
     * @return true, if playersList was updated;
     */
    public boolean replacePlayerList(long[] playerIds) {
        if (mGames.size() > 0) {
            // players can only be changed at the beginning of the session
            return false;
        }

        // clean current list first
        mPlayerList.clear();
        // add new players
        for (int i = 0; i < playerIds.length; i++) {
            addPlayer(playerIds[i]);
        }
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
        if (mPlayerList.size() != playedGame.getNumPlayers()) {
            return false;
        }

        // Add finished game to session
        mGames.add(playedGame);

        // subtract information from game
        for (int playerPosition = 0; playerPosition < mPlayerList.size(); playerPosition++) {
            SessionPlayer player = mPlayerList.get(playerPosition);
            if (playedGame.getGameResult(playerPosition) == Game.eGameResult.LOST) {
                player.addLostGames();
            } else if (playedGame.getGameResult(playerPosition) == Game.eGameResult.WON) {
                player.addWonGame();
            }
            player.addScore(playedGame.getScore(playerPosition));
            mPlayerList.set(playerPosition, player);
        }
        return true;
    }


    /**
     * Details of player in current session
     */
    public class SessionPlayer {
        // Player details
        private long mId;
        private int mWonGames;
        private int mLostGames;
        private int mScore;

        public SessionPlayer(long playerId) {
            mId = playerId;
            mWonGames = 0;
            mLostGames = 0;
            mScore = 0;
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

        public int getScore()  { return mScore; }
        public void addScore(int gameScore)  { mScore =+ gameScore; }

        public long getId() { return mId; }
    }
}
