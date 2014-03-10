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

        /*
        // populate player list by using adapter
        mSessionsListAdapter = new SessionsListAdapter();
        mSessionsListView.setAdapter(mSessionsListAdapter);

        // Add callback for clicking single session
        mSessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listPosition, long rowId) {
                // get ID of selected session
                long sessionId = SessionList.getInstance().getSessionByPosition(listPosition).getId();
                // show menu to delete or change item
                SessionOptionsDialogFragment dialog = new SessionOptionsDialogFragment(playerId);
                dialog.show(getFragmentManager(), "SessionOptionsDialogFragment");
            }
        });
        */

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
}
