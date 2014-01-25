/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

/**
 * Created by Stefan Schulze on 2014/01/21.
 */
public class PlayerImageOptionsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set Player Options
        String[] options = {getString(R.string.dialog_player_image_options_camera), // camera = 0
                getString(R.string.dialog_player_image_options_file), // file = 1
                getString(R.string.dialog_player_image_options_contact), // contact = 2
                getString(R.string.dialog_player_image_options_default), // default = 3
                getString(R.string.dialog_player_image_options_cancel),};  // cancel = 4
        builder.setTitle(R.string.dialog_player_image_options_title)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Forward selection back to caller
                        switch (which) {
                            case 0: // camera
                                // TODO Start camera

                                //mListener.onPlayerImageOptionsDialogImageSelected(image);
                                break;
                            case 1: // file
                                // TODO Start file manager

                                //mListener.onPlayerImageOptionsDialogImageSelected(image);
                                break;
                            case 2: // contact
                                // Start contact selection
                                Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
                                getActivity().startActivityForResult(pickContactIntent, MainActivity.PICK_CONTACT_REQUEST);

                            case 3: // default
                                // TODO Set default player image

                            case 4: // Cancel
                                // player image selection
                                // Don't do anything - dialog will be closed automatically
                            default:
                                // TODO exception
                        }
                    }
                });
        return builder.create();
    }
}
