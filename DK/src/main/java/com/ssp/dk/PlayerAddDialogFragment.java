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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Stefan Schulze on 2014/01/14.
 */
public class PlayerAddDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PlayerAddDialogCallbacks {
        public void onPlayerAddDialogPositiveClick(String PlayerName, Drawable PlayerImage);
        public void onPlayerAddDialogNegativeClick();
    }

    // Instance of the interface to deliver action events
    PlayerAddDialogCallbacks mListener;

    // ShortCuts
    Dialog mDialog;
    Button mAddButton;
    EditText mPlayerNameText;
    ImageView mPlayerImageView;
    View mDialogView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (PlayerAddDialogCallbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerAddDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate custom dialog view
        // Pass null as the parent view because its going in the dialog layout
        mDialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_player, null);

        // Add action buttons
        builder.setView(mDialogView)
                .setPositiveButton(R.string.dialog_add_player_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String playerName = mPlayerNameText.getText().toString().trim();
                        // Send the positive button event back to the host activity
                        mListener.onPlayerAddDialogPositiveClick(playerName, mPlayerImageView.getDrawable());
                    }
                })
                .setNegativeButton(R.string.dialog_add_player_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onPlayerAddDialogNegativeClick();

                        //PlayerAddDialogFragment.this.getDialog().cancel();
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
        mPlayerImageView = (ImageView)mDialogView.findViewById(R.id.AddPlayerDialog_Image);

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
