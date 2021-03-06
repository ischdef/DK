package com.ssp.dk.Player;

/**
 * Created by Stefan Schulze on 2014/01/03.
 */

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ssp.dk.R;


public class PlayerListFragment extends Fragment {

    private View mPlayerListFragmentView;
    private ListView mPlayerListView;
    private ArrayAdapter<Player> mPlayerListAdapter;
    private LayoutInflater mInflater;

    public PlayerListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mPlayerListFragmentView = inflater.inflate(
                R.layout.fragment_player_list, container, false);

        // set listView shortcut
        mPlayerListView = (ListView) mPlayerListFragmentView.findViewById(R.id.player_list);

        // populate player list by using adapter
        mPlayerListAdapter = new PlayerListAdapter();
        mPlayerListView.setAdapter(mPlayerListAdapter);

        // Add callback for long clicking single player
        mPlayerListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int listPosition, long rowId) {
                // get ID of selected player
                long playerId = PlayerList.getInstance().getPlayerByPosition(listPosition).getId();
                // show menu to delete or change item
                PlayerOptionsDialogFragment dialog = new PlayerOptionsDialogFragment(playerId);
                dialog.show(getFragmentManager(), "PlayerOptionsDialogFragment");
                // Callback consumed
                return true;
            }
        });

        mPlayerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listPosition, long rowId) {
                // get ID of selected player
                long playerId = PlayerList.getInstance().getPlayerByPosition(listPosition).getId();
                // show menu to delete or change item
                PlayerOptionsDialogFragment dialog = new PlayerOptionsDialogFragment(playerId);
                dialog.show(getFragmentManager(), "PlayerOptionsDialogFragment");
            }
        });

        return mPlayerListFragmentView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Show playerList specific actions only - delete prev. actions
        menu.clear();
        inflater.inflate(R.menu.player_list, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_player_list_new_player) {
            // Show 'add new player' dialog window
            DialogFragment dialog = new PlayerDialogFragment();
            dialog.show(getFragmentManager(), "PlayerDialogFragment");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    /**
     * Adapter
     */

    public void updatePlayerListView () {
        mPlayerListAdapter.notifyDataSetChanged();
    }

    static class PlayerListItemViewHolder {
        ImageView image;
        TextView name;
        TextView playedSessions;
        TextView wonSessions;
        TextView lostSessions;
    }

    private class PlayerListAdapter extends ArrayAdapter<Player> {
        public PlayerListAdapter() {
            // Link with PlayerList and PlayerListLayout
            super (getActivity().getApplicationContext(), R.layout.player_list_item,
                    PlayerList.getInstance().getList());
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PlayerListItemViewHolder holder; // used for faster view access

            // Check for initial inflation
            if (convertView == null) {
                // inflate
                convertView = mInflater.inflate(R.layout.player_list_item, parent, false);

                // set shortcuts
                holder = new PlayerListItemViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.playerImage);
                holder.name = (TextView) convertView.findViewById(R.id.playerName);
                holder.playedSessions = (TextView) convertView.findViewById(R.id.playerSessionsCount);
                holder.wonSessions = (TextView) convertView.findViewById(R.id.playerSessionsWins);
                holder.lostSessions = (TextView) convertView.findViewById(R.id.playerSessionsLosses);

                convertView.setTag(holder);
            } else {
                holder = (PlayerListItemViewHolder) convertView.getTag();
            }

            // Set player parameters to list view item
            Player player = PlayerList.getInstance().getList().get(position);
            if (player.getImage() == null) {
                // Picture is optional -> set default picture if no player picture is set
                holder.image.setImageDrawable(getResources().getDrawable(R.drawable.no_user_logo));
            } else {
                holder.image.setImageDrawable(player.getImage());
            }
            holder.name.setText(player.getName());
            holder.playedSessions.setText(getString(R.string.player_sessions_count) + ": " + player.getPlayedSessions());
            holder.wonSessions.setText(getString(R.string.player_sessions_wins) + ": " + player.getWonSessions());
            holder.lostSessions.setText(getString(R.string.player_sessions_losses) + ": " + player.getLostSessions());

            return convertView;
        }
    }
}
