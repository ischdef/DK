/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Stefan Schulze on 2014/03/09.
 */
public class SessionsListFragment extends Fragment {

    private View mSessionsListFragmentView;
    private ListView mSessionsListView;
    private ArrayAdapter<Session> mSessionsListAdapter;
    private LayoutInflater mInflater;

    public SessionsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mSessionsListFragmentView = inflater.inflate(
                R.layout.fragment_sessions_list, container, false);

        // set listView shortcut
        mSessionsListView = (ListView) mSessionsListFragmentView.findViewById(R.id.sessions_list);

        // populate sessions list by using adapter
        mSessionsListAdapter = new SessionsListAdapter();
        mSessionsListView.setAdapter(mSessionsListAdapter);

        // Add callback for clicking single session
        mSessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listPosition, long rowId) {
                // get ID of selected session
                //long sessionId = SessionList.getInstance().getSessionByPosition(listPosition).getId();
                // TODO show menu to use or delete selected session
                //SessionOptionsDialogFragment dialog = new SessionOptionsDialogFragment(playerId);
                //dialog.show(getFragmentManager(), "SessionOptionsDialogFragment");
            }
        });

        return mSessionsListFragmentView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Show SessionFragment specific actions only - delete prev. actions
        menu.clear();
        inflater.inflate(R.menu.sessions, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * Adapter
     */

    public void updateSessionsListView () {
        mSessionsListAdapter.notifyDataSetChanged();
    }

    static class SessionsListItemViewHolder {
        TextView name;
        TextView playedGames;
        TextView creationDate;
        TextView numPlayers;
    }

    private class SessionsListAdapter extends ArrayAdapter<Session> {
        public SessionsListAdapter() {
            // Link with SessionsList and SessionsListLayout
            super (getActivity().getApplicationContext(), R.layout.sessions_list_item,
                    SessionsList.getInstance().getList());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SessionsListItemViewHolder holder; // used for faster view access

            // Check for initial inflation
            if (convertView == null) {
                // inflate
                convertView = mInflater.inflate(R.layout.sessions_list_item, parent, false);

                // set shortcuts
                holder = new SessionsListItemViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.sessionName);
                holder.playedGames = (TextView) convertView.findViewById(R.id.sessionGamesCount);
                holder.creationDate = (TextView) convertView.findViewById(R.id.sessionCreationDate);
                holder.numPlayers = (TextView) convertView.findViewById(R.id.sessionNumPlayers);

                convertView.setTag(holder);
            } else {
                holder = (SessionsListItemViewHolder) convertView.getTag();
            }

            // Set session parameters to list view item
            Session session = SessionsList.getInstance().getList().get(position);
            holder.name.setText(session.getName());
            holder.playedGames.setText(getString(R.string.session_players_count) + ": " + session.getPlayedGames());
            holder.numPlayers.setText(getString(R.string.session_players_count) + ": " + session.getNumberOfPlayers());
            // Convert creation time in date format
            final long creationTime = session.getTimeOfCreation();
            final String creationDate = DateFormat.getDateTimeInstance().format(new Date(creationTime));
            holder.creationDate.setText(getString(R.string.session_creation_date) + ": " + creationDate);

            return convertView;
        }
    }
}
