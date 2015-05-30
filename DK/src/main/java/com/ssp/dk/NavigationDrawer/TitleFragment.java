/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.NavigationDrawer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssp.dk.R;

/**
 * Created by Stefan Schulze on 2014/01/13.
 */
public class TitleFragment extends Fragment{
    private View mTitleView;

    public void TitleFragment(){
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTitleView = inflater.inflate(R.layout.fragment_title, container, false);
        return mTitleView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Show TitleScreen specific actions only - delete prev. actions
        menu.clear();
        inflater.inflate(R.menu.title_screen, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
