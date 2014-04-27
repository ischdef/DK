/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Stefan Schulze on 2014/03/08.
 */
public class CurrentSessionFragment extends Fragment {
    private LayoutInflater mInflater;
    private View mCurrentSessionFragmentView;

    // session short cut
    private Session mSession;

    // Currently played game results
    private Game mCurrentGame;

    public CurrentSessionFragment(long sessionId) {
        mSession = SessionsList.getInstance().getSessionById(sessionId);
    }

    /**
     * Store result of GameDialog for selected player.
     * If this is the last player in the list, the complete game will be stored to session.
     * Otherwise the GameDialog for the next player will be displayed.
     * @param playerPosition Position of player in session
     * @param result GameResult of player
     * @param score Game score of player
     * @param showPrevious if true the previous player game dialog will be shown again
     *                     if false the next player (if any) game dialog will be shown
     */
    public void addPlayerGameResult(int playerPosition, Game.eGameResult result, int score,
                                          boolean showPrevious) {
        // store new game result for selected player
        mCurrentGame.setScore(playerPosition, score, result);

        // select player to be shown next in Game Dialog
        int nextPlayerPosition = playerPosition;
        if (showPrevious) {
            if (playerPosition == 0) {
                // should not happen, show same player game dialog again
                nextPlayerPosition = 0;
            } else {
                // Show 'add new game' dialog window for previous player
                nextPlayerPosition--;
            }
        } else {
            if (playerPosition + 1 == mSession.getNumberOfPlayers()) {
                // all player game results are stored in the game -> store game in session
                SessionsList.getInstance().addGameToSession(mSession.getId(), mCurrentGame);

                // TODO Show new results in current session table fragment
                // ...

                return;
            } else {
                // Show 'add new game' dialog window for next player
                nextPlayerPosition++;
            }
        }

        // Show 'add new game' dialog window for next selected player
        DialogFragment dialog = new GameDialogFragment(mSession.getId(), nextPlayerPosition,
                mCurrentGame.getGameResult(nextPlayerPosition), mCurrentGame.getScore(nextPlayerPosition));
        dialog.show(getFragmentManager(), "GameDialogFragment");
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Show CurrentSessionFragment specific actions only - delete prev. actions
        menu.clear();
        inflater.inflate(R.menu.current_session, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_current_session_new_game) {
            // Init current game
            mCurrentGame = new Game(mSession.getNumberOfPlayers());
            // Show 'add new game' dialog window for first player in session
            DialogFragment dialog = new GameDialogFragment(mSession.getId(), 0, Game.eGameResult.NEUTRAL, 0);
            dialog.show(getFragmentManager(), "GameDialogFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mCurrentSessionFragmentView = inflater.inflate(
                R.layout.fragment_current_session, container, false);


        return mCurrentSessionFragmentView;
    }
}
