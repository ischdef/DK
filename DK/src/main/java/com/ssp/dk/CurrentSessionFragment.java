/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Stefan Schulze on 2014/03/08.
 */
public class CurrentSessionFragment extends Fragment {
    private LayoutInflater mInflater;

    private View mCurrentSessionFragmentView;
    private TextView mCurrentSessionNameView;
    private TextView mCurrentSessionCreationDateView;
    private TextView mCurrentSessionGamesCountView;
    private TextView mCurrentSessionNumPlayersView;
    private ListView mSessionPlayerListView;

    private SessionPlayerListAdapter mSessionPlayerListAdapter;

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
                if (!SessionsList.getInstance().addGameToSession(mSession.getId(), mCurrentGame)) {
                    throw new RuntimeException("addPlayerGameResult failed, invalid number of players: " + mSession.getNumberOfPlayers());
                }

                // TODO Show new results in current session table fragment
                // ...

                // Show new results in current session overview
                updateCurrentSessionOverview();

                return;
            } else {
                // Show 'add new game' dialog window for next player
                nextPlayerPosition++;
            }
        }

        // Show 'add new game' dialog window for next selected player
        DialogFragment dialog = new GameDialogFragment();
        // Supply input arguments
        Bundle args = new Bundle();
        args.putLong("sessionId", mSession.getId());
        args.putInt("playerPosition", nextPlayerPosition);
        args.putParcelable("currentGame", mCurrentGame);
        dialog.setArguments(args);
        // trigger onCreateDialog()
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
            DialogFragment dialog = new GameDialogFragment();
            // Supply input arguments
            Bundle args = new Bundle();
            args.putLong("sessionId", mSession.getId());
            args.putInt("playerPosition", 0); // first player
            args.putParcelable("currentGame", mCurrentGame);
            dialog.setArguments(args);
            // trigger onCreateDialog()
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
                //R.layout.fragment_current_session, container, false);
                R.layout.fragment_current_session_overview, container, false);

        // set view shortcuts
        mSessionPlayerListView = (ListView) mCurrentSessionFragmentView.findViewById(R.id.CurrentSessionOverview_PlayerResultList);
        mCurrentSessionNameView = (TextView) mCurrentSessionFragmentView.findViewById(R.id.CurrentSessionOverview_Name);
        mCurrentSessionCreationDateView = (TextView) mCurrentSessionFragmentView.findViewById(R.id.CurrentSessionOverview_CreationDate);
        mCurrentSessionGamesCountView = (TextView) mCurrentSessionFragmentView.findViewById(R.id.CurrentSessionOverview_GamesCount);
        mCurrentSessionNumPlayersView = (TextView) mCurrentSessionFragmentView.findViewById(R.id.CurrentSessionOverview_NumPlayers);

        // populate player list by using adapter
        mSessionPlayerListAdapter = new SessionPlayerListAdapter();
        mSessionPlayerListView.setAdapter(mSessionPlayerListAdapter);

        // Set session parameters
        mCurrentSessionGamesCountView.setText(getString(R.string.session_games_count) + ": " + mSession.getPlayedGames());
        mCurrentSessionNameView.setText(mSession.getName());
        mCurrentSessionNumPlayersView.setText(getString(R.string.session_players_count) + ": " + mSession.getNumberOfPlayers());
        // Convert creation time in date format
        final long creationTime = mSession.getTimeOfCreation();
        final String creationDate = DateFormat.getDateTimeInstance().format(new Date(creationTime));
        mCurrentSessionCreationDateView.setText(getString(R.string.session_creation_date) + ": " + creationDate);

        return mCurrentSessionFragmentView;
    }


    public void updateCurrentSessionOverview () {
        // Update Session player list
        mSessionPlayerListAdapter.notifyDataSetChanged();
        // Update number of played games
        mCurrentSessionGamesCountView.setText(getString(R.string.session_games_count) + ": " + mSession.getPlayedGames());
    }



    /**
     * Adapter
     */

    static class SessionPlayerListItemViewHolder {
        ImageView image;
        TextView name;
        TextView numDraws;
        TextView numWins;
        TextView numLosses;
        TextView score;
    }

    private class SessionPlayerListAdapter extends ArrayAdapter<Session.SessionPlayer> {
        public SessionPlayerListAdapter() {
            // Link with PlayerList and PlayerListLayout
            super (getActivity().getApplicationContext(), R.layout.session_player_list_item,
                    mSession.getSessionPlayerList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SessionPlayerListItemViewHolder holder; // used for faster view access

            // Check for initial inflation
            if (convertView == null) {
                // inflate
                convertView = mInflater.inflate(R.layout.session_player_list_item, parent, false);

                // set shortcuts
                holder = new SessionPlayerListItemViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.CurrentSessionOverview_SessionPlayer_Image);
                holder.name = (TextView) convertView.findViewById(R.id.CurrentSessionOverview_SessionPlayer_PlayerName);
                holder.numDraws = (TextView) convertView.findViewById(R.id.CurrentSessionOverview_SessionPlayer_NumDraws);
                holder.numWins = (TextView) convertView.findViewById(R.id.CurrentSessionOverview_SessionPlayer_NumWins);
                holder.numLosses = (TextView) convertView.findViewById(R.id.CurrentSessionOverview_SessionPlayer_NumLosses);
                holder.score = (TextView) convertView.findViewById(R.id.CurrentSessionOverview_SessionPlayer_Score);

                convertView.setTag(holder);
            } else {
                holder = (SessionPlayerListItemViewHolder) convertView.getTag();
            }

            // Set player parameters to list view item
            Session.SessionPlayer sessionPlayer = mSession.getSessionPlayerList().get(position);
            Player player = PlayerList.getInstance().getPlayerById(sessionPlayer.getId());
            if (player.getImage() == null) {
                // Picture is optional -> set default picture if no player picture is set
                holder.image.setImageDrawable(getResources().getDrawable(R.drawable.no_user_logo));
            } else {
                holder.image.setImageDrawable(player.getImage());
            }
            holder.name.setText(player.getName());
            holder.numDraws.setText(getString(R.string.current_session_num_draws) + ": " +
                    (mSession.getPlayedGames() - sessionPlayer.getLostGames() - sessionPlayer.getWonGames()));
            holder.numWins.setText(getString(R.string.current_session_num_wins) + ": " + sessionPlayer.getWonGames());
            holder.numLosses.setText(getString(R.string.current_session_num_losses) + ": " + sessionPlayer.getLostGames());
            holder.score.setText(getString(R.string.current_session_score) + ": " + sessionPlayer.getScore());

            return convertView;
        }
    }
}




