/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.Player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.ssp.dk.R;

/**
 * Created by Stefan Schulze on 2014/01/19.
 */
public class PlayerOptionsDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PlayerOptionsDialogCallbacks {
        public void onPlayerOptionsDialogDeleteClick(long playerId);
        public void onPlayerOptionsDialogEditClick(long playerId);
    }
    // Instance of the interface to deliver action events
    PlayerOptionsDialogCallbacks mListener;

    // Unique ID of selected Player
    long mPlayerId;

    public PlayerOptionsDialogFragment(long playerId) {
        mPlayerId = playerId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (PlayerOptionsDialogCallbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerOptionsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set Player Options
        String[] options = {getString(R.string.dialog_player_options_delete), // delete = 0
                            getString(R.string.dialog_player_options_edit)};  // edit = 1
        builder.setTitle(R.string.dialog_player_options_title)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Forward selection back to caller
                        switch (which) {
                            case 0: // delete
                                // Delete selected player
                                mListener.onPlayerOptionsDialogDeleteClick(mPlayerId);
                                break;
                            case 1: // edit
                                // Open Edit Player dialog
                                mListener.onPlayerOptionsDialogEditClick(mPlayerId);
                                break;
                            default:
                                throw new RuntimeException("Invalid option selected in PlayerOptionsDialog: " + which);
                        }
                    }
                });
        return builder.create();
    }
}
