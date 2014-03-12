package com.ssp.dk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        PlayerDialogFragment.PlayerAddDialogCallbacks,
        PlayerOptionsDialogFragment.PlayerOptionsDialogCallbacks,
        SessionsListFragment.SessionAddDialogCallbacks,
        SessionOptionsDialogFragment.SessionOptionsDialogCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * List with all stored players
     */
    private PlayerList mPlayerList = null;

    /**
     * List with all stored sessions
     */
    private SessionsList mSessionsList = null;

    /**
     * Unique session ID of currently active session
     */
    private long mCurrentSessionId = 0;

    // Shortcuts
    private PlayerListFragment mPlayerListFragment;
    private SessionsListFragment mSessionsListFragment;

    // Content Resolver related parameter
    public static final int REQUEST_PICK_CONTACT  = 1; // Request code used for selecting a contact
    public static final int REQUEST_PICK_PHOTO    = 2; // Request code used for selecting a image from storage
    public static final int REQUEST_IMAGE_CAPTURE = 3; // Request code used for taking a photo via camera

    // Application Details
    String mVersionNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create DBs if not already available
        if (mPlayerList == null) {
            mPlayerList = PlayerList.createInstance(getApplicationContext());
        }
        if (mSessionsList == null) {
            mSessionsList = SessionsList.createInstance(getApplicationContext());
        }

        // Get version number from manifest file
        try {
            mVersionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            mVersionNumber = "(No version number yet)";
        }

        // Set the activity content from a layout resource.
        // The resource will be inflated, adding all top-level views to the activity.
        setContentView(R.layout.activity_main);

        // Get navigationDrawer fragment after inflation by setContentView()
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set actionBarTitle to name of app
        //mActionBarTitle = getTitle();

        // Set up the drawer, align with layout (also calls onCreate())
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Show title screen
        FragmentManager fragmentManager = getFragmentManager();
        TitleFragment fragment = new TitleFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void toastMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            // Show "About" popup window
            AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
            helpBuilder.setTitle(getString(R.string.app_name) + " " + mVersionNumber);
            helpBuilder.setMessage(getString(R.string.dialog_about_copyright) + "\n" + getString(R.string.dialog_about_ssp));
            helpBuilder.setPositiveButton(R.string.dialog_about_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog
                        }
                    });
            // Show the dialog
            helpBuilder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Check orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // TODO Change to landscape mode -> always show drawer open?
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            // TODO Change back portrait mode -> close open drawer open?
        }

    }

    /******************************
     * Callbacks NavigationDrawer *
     ******************************/

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mNavigationDrawerFragment == null) {
            //Toast.makeText(getApplicationContext(), "Drawer Fragment error.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trigger fragment change depending on selected drawer
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                // Show Title fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new TitleFragment())
                        .commit();
                break;
            case 1:
                // Show PlayerList fragment
                mPlayerListFragment = new PlayerListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mPlayerListFragment)
                        .commit();
                break;
            case 2:
                // Show current session fragment
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new CurrentSessionTableFragment(mCurrentSessionId))
                        .commit();
                break;
            case 3:
                // Show sessions fragment
                mSessionsListFragment = new SessionsListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mSessionsListFragment)
                        .commit();
                break;
            default:
                // TODO add exception
                toastMessage(getString(R.string.toast_drawer_selection_error));
                return;
        }
    }


    /*****************************
     * Callbacks PlayerDialog *
     *****************************/

    @Override
    public void onPlayerAddDialogPositiveClick(String PlayerName, Drawable PlayerImage) {
        // save new player
        final int numPlayers = mPlayerList.addPlayer(PlayerName, PlayerImage);
        toastMessage(getString(R.string.toast_player_added) + " '" + PlayerName + "'.");

        // Inform PlayerListView about new player - refresh list view
        mPlayerListFragment.updatePlayerListView();

        // update Navigation Drawer player counter
        mNavigationDrawerFragment.updateNumberOfPlayersCounter(numPlayers);
    }

    @Override
    public void onPlayerAddDialogNegativeClick() {
        toastMessage(getString(R.string.toast_player_add_canceled) + ".");
    }

    @Override
    public void onPlayerEditDialogPositiveClick(long playerId, String playerName, Drawable playerImage) {
        // save player changes
        mPlayerList.editPlayer(playerId, playerName, playerImage);

        // Inform user about saved update
        toastMessage(getString(R.string.toast_player_edit) + " '" + playerName + "'.");

        // Inform PlayerListView about changed player - refresh list view
        mPlayerListFragment.updatePlayerListView();
    }

    @Override
    public void onPlayerEditDialogNegativeClick() {
        toastMessage(getString(R.string.toast_player_edit_canceled) + ".");
    }


    /*********************************
     * Callbacks PlayerOptionsDialog *
     *********************************/

    @Override
    public void onPlayerOptionsDialogDeleteClick(long playerId) {
        // get player name
        String name = mPlayerList.getPlayerById(playerId).getName();

        // remove player from list
        final int numPlayers = mPlayerList.deletePlayer(playerId);

        toastMessage(getString(R.string.toast_player_deleted) + " '" + name + "'.");

        // Inform PlayerListView about removed player - refresh list view
        mPlayerListFragment.updatePlayerListView();

        // update Navigation Drawer player counter
        mNavigationDrawerFragment.updateNumberOfPlayersCounter(numPlayers);
    }

    @Override
    public void onPlayerOptionsDialogEditClick(long playerId) {
        // Show 'edit player' dialog window
        DialogFragment dialog = new PlayerDialogFragment(playerId);
        dialog.show(getFragmentManager(), "PlayerDialogFragment");
    }


    /******************************
     * Callbacks SessionAddDialog *
     ******************************/

    @Override
    public void onSessionAddDialogPositiveClick(String SessionName) {
        // save new session if valid name
        if (SessionName.trim().length() == 0) {
            toastMessage(getString(R.string.toast_session_add_canceled) + ".");
        } else {
            mSessionsList.addSession(SessionName);
            // Inform SessionsListView about new player - refresh list view
            mSessionsListFragment.updateSessionsListView();

            toastMessage(getString(R.string.toast_session_added) + " '" + SessionName + "'.");
        }
    }

    @Override
    public void onSessionAddDialogNegativeClick() {
        toastMessage(getString(R.string.toast_session_add_canceled) + ".");
    }


    /***********************************
     * Callbacks SessionOptionsDialog  *
     ***********************************/

    @Override
    public void onSessionOptionsDialogStartClick(long sessionId) {
        // Save selection
        mCurrentSessionId = sessionId;
        // inform NavigationDrawer about external drawer change
        mNavigationDrawerFragment.changeToCurrentSessionDrawer(sessionId);
    }

    @Override
    public void onSessionOptionsDialogPlayersClick(long sessionId) {

    }

    @Override
    public void onSessionOptionsDialogRenameClick(long sessionId) {

    }

    @Override
    public void onSessionOptionsDialogDeleteClick(long sessionId) {

    }


    /****************************************
     * handle Intent results from Fragments *
     ****************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        final PlayerDialogFragment playerDialogFragment = (PlayerDialogFragment)getFragmentManager().findFragmentByTag("PlayerDialogFragment");
        if (playerDialogFragment == null) {
            toastMessage(getString(R.string.toast_fragment_error));
            return;
        }

        // Check which request we're responding to
        switch (requestCode) {

            case REQUEST_PICK_CONTACT: {
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK) {
                    // The user picked a contact - The Intent's data Uri identifies which contact was selected.
                    Uri resultUri = intent.getData();
                    if (resultUri != null) {
                        Cursor cursor =  getContentResolver().query(resultUri, null, null, null, null);
                        if (cursor.moveToFirst()) {
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            try {
                                Uri imageUri = Uri.parse(cursor.getString(
                                        cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));
                                // Inform PlayerDialogFragment about selected contact
                                playerDialogFragment.setSelectedContact(name, imageUri);
                            } catch (Exception e) {
                                // Inform PlayerDialogFragment only about selected contact name
                                playerDialogFragment.setSelectedContact(name, null);
                            }
                            cursor.close();
                            return;
                        }
                    }
                }
                toastMessage(getString(R.string.toast_no_contact_selected));
                break;
            }

            case REQUEST_PICK_PHOTO: {
                if (resultCode == RESULT_OK){
                    Uri selectedImage = intent.getData();
                    // Inform PlayerDialogFragment about selected picture
                    playerDialogFragment.setSelectedPlayerImage(selectedImage);
                    return;
                }
                toastMessage(getString(R.string.toast_no_image_selected));
                break;
            }

            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    // Get Thumbnail
                    Bundle extras = intent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    // Inform PlayerDialogFragment about taken picture
                    playerDialogFragment.setSelectedPlayerImage(imageBitmap);
                    return;
                }
                toastMessage(getString(R.string.toast_no_image_captured));
                break;
            }

            default: {
                toastMessage(getString(R.string.toast_invalid_request));
            }
        }
    }

}
