/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Schulze on 2014/03/13.
 */
public class SessionOptionsPlayerSelectionDialogFragment extends DialogFragment {
    /** The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SessionOptionsPlayerSelectionDialogCallbacks {
        public void onSessionOptionsPlayerSelectionDialogOkClick(long sessionId, long[] selectedPlayerIds);
        public void onSessionOptionsPlayerSelectionDialogCancelClick();

    }

    long mSessionId;
    String[] mPlayerNames;
    long[] mPlayerIds;
    boolean[] mSelectedPlayers;

    // Instance of the interface to deliver action events
    SessionOptionsPlayerSelectionDialogCallbacks mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (SessionOptionsPlayerSelectionDialogCallbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SessionOptionsPlayerSelectionDialogFragment");
        }
    }

    public SessionOptionsPlayerSelectionDialogFragment(long sessionId) {
        mSessionId = sessionId;
        // Get players of current session
        ArrayList<Session.SessionPlayer> sessionPlayerList =
                SessionsList.getInstance().getSessionById(sessionId).getSessionPlayerList();

        // get all available player names
        List<Player> playerList = PlayerList.getInstance().getList();
        int numPlayers = playerList.size();
        // init arrays
        mPlayerNames = new String[numPlayers];
        mPlayerIds = new long[numPlayers];
        mSelectedPlayers = new boolean[numPlayers];
        // get multi choice relevant parameters
        for (int i = 0; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            mPlayerNames[i] = player.getName();
            mPlayerIds[i] = player.getId();
            mSelectedPlayers[i] = false;
            // check if prev. selected
            for (int j = 0; j < sessionPlayerList.size(); j++) {
                if (player.getId() == sessionPlayerList.get(j).getId()) {
                    mSelectedPlayers[i] = true;
                }
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.dialog_session_player_selection_title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(mPlayerNames, mSelectedPlayers,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedPlayers[which] = true;
                                } else {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedPlayers[which] = false;
                                }
                            }
                        }
                )
                        // Set the action buttons
                .setPositiveButton(R.string.dialog_session_player_selection_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Get players of current session
                        List<Player> playerList = PlayerList.getInstance().getList();
                        // get number of selected players
                        int numSelectedPlayers = 0;
                        for (int i = 0; i < mSelectedPlayers.length; i++) {
                            if (mSelectedPlayers[i]) {
                                numSelectedPlayers++;
                            }
                        }
                        // create list of players ID of selected players
                        long[] selectedPlayerIds = new long[numSelectedPlayers];
                        int selectedPlayerIndex = 0;
                        for (int i = 0; i < mSelectedPlayers.length; i++) {
                            if (mSelectedPlayers[i]) {
                                selectedPlayerIds[selectedPlayerIndex++] = mPlayerIds[i];
                            }
                        }
                        // Save selected players in session
                        mListener.onSessionOptionsPlayerSelectionDialogOkClick(mSessionId, selectedPlayerIds);
                    }
                })
                .setNegativeButton(R.string.dialog_session_player_selection_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSessionOptionsPlayerSelectionDialogCancelClick();
                    }
                });

        return builder.create();
    }
}
