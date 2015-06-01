/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.CurrentSession;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ssp.dk.R;
import com.ssp.dk.Session.Session;
import com.ssp.dk.Session.SessionsList;
import com.ssp.dk.Table.TableFixHeaders;

/**
 * Created by Stefan Schulze on 2014/05/31.
 */
public class CurrentSessionTableFragment extends Fragment {

    /** The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CurrentSessionTableFragmentCallbacks {
        void onShowCurrentSessionOverview(long sessionId);
    }

    // Instance of the interface to deliver action events
    CurrentSessionTableFragmentCallbacks mListener;

    private LayoutInflater mInflater;

    private TableFixHeaders mCurrentSessionTableFragmentView;

    private CurrentSessionTableAdapter mCurrentSessionTableAdapter;

    // session short cut
    private long mSessionId;

    public CurrentSessionTableFragment(long sessionId) {
        mSessionId = sessionId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (CurrentSessionTableFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CurrentSessionTableFragmentListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Show CurrentSessionTableFragment specific actions only - delete prev. actions
        menu.clear();
        inflater.inflate(R.menu.current_session_table, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_current_session_overview:
                // Return to CurrentSessionFragment
                mListener.onShowCurrentSessionOverview(mSessionId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mCurrentSessionTableFragmentView = (TableFixHeaders) inflater.inflate(
                R.layout.fragment_current_session_table, container, false);

        // populate current session table by using adapter
        mCurrentSessionTableAdapter = new CurrentSessionTableAdapter(getActivity().getApplicationContext(), mSessionId);
        mCurrentSessionTableFragmentView.setAdapter(mCurrentSessionTableAdapter);

        return mCurrentSessionTableFragmentView;
    }
}




