/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

/**
 * Created by Stefan Schulze on 2014/03/10.
 */
public class Game {
    private int[] mScores;
    private eGameResult[] mGameResults;
    private int mNumPlayers;

    /**
     * Init new game with number of players
     *
     * @param numPlayers Number of players in this game
     */
    public Game(int numPlayers) {
        mNumPlayers = numPlayers;
        mScores = new int[numPlayers];
        mGameResults = new eGameResult[numPlayers];
        for (int playerPosition = 0; playerPosition < numPlayers; playerPosition++) {
            mScores[playerPosition] = 0;
            mGameResults[playerPosition] = eGameResult.NEUTRAL;
        }
    }

    public int getScore(int playerPosition) {
        return mScores[playerPosition];
    }

    public eGameResult getGameResult(int playerPosition) {
        return mGameResults[playerPosition];
    }

    public int getNumPlayers() {
        return mNumPlayers;
    }

    /**
     * Set score for single player
     * @param playerPosition Position of player in the game
     * @param score Number of points (positive or negative)
     * @param gameResult Result of game for selected player
     * @return true if score was set successfully; returns false if invalid playerPosition
     */
    public boolean setScore(int playerPosition, int score, eGameResult gameResult) {
        if (playerPosition >= mNumPlayers || playerPosition < 0) {
            return false;
        }
        mScores[playerPosition] = score;
        mGameResults[playerPosition] = gameResult;
        return true;
    }

    public enum eGameResult {
        NEUTRAL(0),
        LOST(1),
        WON(2);
        private final int mValue;

        eGameResult(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }

}
