/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Toast;

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
                                // Check if Camera is available
                                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                                    // Start camera
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                        getActivity().startActivityForResult(takePictureIntent, MainActivity.REQUEST_IMAGE_CAPTURE);
                                    }
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "No camera available", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1: // file
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                getActivity().startActivityForResult(photoPickerIntent, MainActivity.REQUEST_PICK_PHOTO);
                                break;
                            case 2: // contact
                                // Start contact selection
                                Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
                                getActivity().startActivityForResult(pickContactIntent, MainActivity.REQUEST_PICK_CONTACT);

                            case 3: // default
                                // Get default user log Uri
                                int resId = R.drawable.no_user_logo;
                                Resources resources = getActivity().getApplicationContext().getResources();
                                Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                                        resources.getResourcePackageName(resId) +
                                        '/' + resources.getResourceTypeName(resId) +
                                        '/' + resources.getResourceEntryName(resId) );
                                // Set default player image
                                ((PlayerDialogFragment)getFragmentManager().findFragmentByTag("PlayerDialogFragment")).
                                        setSelectedPlayerImage(imageUri);
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
