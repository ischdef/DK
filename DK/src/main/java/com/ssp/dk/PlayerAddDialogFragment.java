/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Stefan Schulze on 2014/01/14.
 */
public class PlayerAddDialogFragment extends DialogFragment {

    Dialog mDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_add_player, null))
                // Add action buttons
                .setPositiveButton(R.string.dialog_add_player_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO save new player

                    }
                })
                .setNegativeButton(R.string.dialog_add_player_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PlayerAddDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create dialog and save for later
        mDialog = builder.create();

        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Disable "Add button" because no entered yet
        ((AlertDialog)mDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }
}
