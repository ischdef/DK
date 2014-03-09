/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by Stefan Schulze on 2014/03/09.
 */
public class SessionsListFragment extends Fragment {

    public SessionsListFragment() {
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
