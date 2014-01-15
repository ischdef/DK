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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Stefan Schulze on 2014/01/14.
 */
public class PlayerAddDialogFragment extends DialogFragment {

    Dialog mDialog;
    Button mAddButton;
    EditText mPlayerNameText;
    View mDialogView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate custom dialog view
        mDialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_player, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mDialogView)
                // Add action buttons
                .setPositiveButton(R.string.dialog_add_player_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String playerName = "Added player " + mPlayerNameText.getText().toString().trim() + ".";
                        // TODO save new player
                        Toast.makeText(getActivity().getApplicationContext(), playerName, Toast.LENGTH_SHORT).show();
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

        // save object shortcuts
        mAddButton = ((AlertDialog)mDialog).getButton(AlertDialog.BUTTON_POSITIVE);
        mPlayerNameText = (EditText)mDialogView.findViewById(R.id.AddPlayerDialog_Name);

        // Disable "Add button" because no Name entered yet
        mAddButton.setEnabled(false);

        // Add PlayerNameText Listeners
        mPlayerNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Enable Add button when user added a player name
                mAddButton.setEnabled(mPlayerNameText.getText().toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
}
