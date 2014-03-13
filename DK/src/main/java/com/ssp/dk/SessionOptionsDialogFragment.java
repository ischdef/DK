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
 * Created by Stefan Schulze on 2014/03/12.
 */
public class SessionOptionsDialogFragment extends DialogFragment {
    /** The activity that creates an instance of this dialog fragment must
      * implement this interface in order to receive event callbacks.
      * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SessionOptionsDialogCallbacks {
        public void onSessionOptionsDialogStartClick(long sessionId);
        public void onSessionOptionsDialogPlayersClick(long sessionId);
        public void onSessionOptionsDialogRenameClick(long sessionId);
        public void onSessionOptionsDialogDeleteClick(long sessionId);

    }
    // Instance of the interface to deliver action events
    SessionOptionsDialogCallbacks mListener;

    // Unique ID of selected Session
    long mSessionId;

    public SessionOptionsDialogFragment(long sessionId) {
        mSessionId = sessionId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (SessionOptionsDialogCallbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SessionOptionsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set Player Options
        String[] options = {
                getString(R.string.dialog_session_options_start),   // start   = 0
                getString(R.string.dialog_session_options_players),  // players = 1
                getString(R.string.dialog_session_options_rename),  // rename  = 2
                getString(R.string.dialog_session_options_delete)}; // delete  = 3

        String sessionName = SessionsList.getInstance().getSessionById(mSessionId).getName();
        builder.setTitle(sessionName)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Forward selection back to caller
                        switch (which) {
                            case 0: // start
                                // Start using selected session
                                mListener.onSessionOptionsDialogStartClick(mSessionId);
                                break;
                            case 1: // players
                                // Show player selection window
                                mListener.onSessionOptionsDialogPlayersClick(mSessionId);
                                break;
                            case 2: // rename
                                // Rename selected session
                                mListener.onSessionOptionsDialogRenameClick(mSessionId);
                                break;
                            case 3: // delete
                                // Delete selected session
                                mListener.onSessionOptionsDialogDeleteClick(mSessionId);
                                break;
                            default:
                                // TODO exception
                        }
                    }
                });
        return builder.create();
    }
}
