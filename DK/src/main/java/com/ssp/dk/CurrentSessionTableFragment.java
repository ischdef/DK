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
 * Created by Stefan Schulze on 2014/03/08.
 */
public class CurrentSessionTableFragment extends Fragment {
    // Unique session ID
    private long mSessionId;

    public CurrentSessionTableFragment(long sessionId) {
        mSessionId = sessionId;
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

}
