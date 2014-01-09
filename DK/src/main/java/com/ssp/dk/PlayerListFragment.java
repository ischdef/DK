package com.ssp.dk;

/**
 * Created by Stefan Schulze on 2014/01/03.
 */

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayerListFragment extends Fragment {

    private class Player {
        // Player details
        private EditText mPlayerName;
        private int mPlayedGames;
        private int mWonGames;
        private int mLostGames;

        private Player(EditText mPlayerName) {
            this.mPlayerName = mPlayerName;
            mPlayedGames = 0;
            mWonGames = 0;
            mLostGames = 0;
        }

        public EditText getmPlayerName() {
            return mPlayerName;
        }

        public void setmPlayerName(EditText mPlayerName) {
            this.mPlayerName = mPlayerName;
        }

        public int getmPlayedGames() {
            return mPlayedGames;
        }

        public void setmPlayedGames(int mPlayedGames) {
            this.mPlayedGames = mPlayedGames;
        }

        public int getmWonGames() {
            return mWonGames;
        }

        public void setmWonGames(int mWonGames) {
            this.mWonGames = mWonGames;
        }

        public int getmLostGames() {
            return mLostGames;
        }

        public void setmLostGames(int mLostGames) {
            this.mLostGames = mLostGames;
        }
    }

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

        // TODO anything to do?
    }

    @Override
    public void onPause() {
        super.onPause();

        // TODO check if anything to store
    }
}
