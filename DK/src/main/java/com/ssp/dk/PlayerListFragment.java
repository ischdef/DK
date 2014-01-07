package com.ssp.dk;

/**
 * Created by Stefan Schulze on 2014/01/03.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PlayerListFragment extends Fragment {

    private View mPlayersListView;

    public PlayerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO ...
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO ...
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPlayersListView = inflater.inflate(
                R.layout.fragment_players_list, container, false);

        return mPlayersListView;
    }


}
