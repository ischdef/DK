package com.ssp.dk;

/**
 * Created by Stefan Schulze on 2014/01/03.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class PlayerListFragment extends Fragment {

    List<Player> mPlayerList = new ArrayList<Player>();
    private View mPlayersListView;

    public PlayerListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // TODO Init Player details needed?
        mPlayerList.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO ...
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPlayersListView = inflater.inflate(
                R.layout.fragment_players_list, container, false);

        return mPlayersListView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Show playersList specific actions only - delete prev. actions
        menu.clear();
        inflater.inflate(R.menu.players_list, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public void onPause() {
        super.onPause();

        // TODO check if anything to store
    }
}
