package com.ssp.dk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;

import com.ssp.dk.CurrentSession.CurrentSessionFragment;
import com.ssp.dk.Game.Game;
import com.ssp.dk.Game.GameDialogFragment;
import com.ssp.dk.NavigationDrawer.NavigationDrawerFragment;
import com.ssp.dk.NavigationDrawer.TitleFragment;
import com.ssp.dk.Player.PlayerDialogFragment;
import com.ssp.dk.Player.PlayerList;
import com.ssp.dk.Player.PlayerListFragment;
import com.ssp.dk.Player.PlayerOptionsDialogFragment;
import com.ssp.dk.Session.Session;
import com.ssp.dk.Session.SessionOptionsDialogFragment;
import com.ssp.dk.Session.SessionOptionsPlayerSelectionDialogFragment;
import com.ssp.dk.Session.SessionsList;
import com.ssp.dk.Session.SessionsListFragment;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        PlayerDialogFragment.PlayerAddDialogCallbacks,
        PlayerOptionsDialogFragment.PlayerOptionsDialogCallbacks,
        SessionsListFragment.SessionAddDialogCallbacks,
        SessionOptionsDialogFragment.SessionOptionsDialogCallbacks,
        SessionOptionsPlayerSelectionDialogFragment.SessionOptionsPlayerSelectionDialogCallbacks,
        GameDialogFragment.GameDialogCallbacks {

    /**
     * Remember the last started session ID.
     */
    private static final String LAST_STARTED_SESSION_ID = "last_started_session_id";

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
    private TitleFragment mTitleFragment;
    private PlayerListFragment mPlayerListFragment;
    private SessionsListFragment mSessionsListFragment;
    private CurrentSessionFragment mCurrentSessionFragment;

    // Content Resolver related parameter
    public static final int REQUEST_PICK_CONTACT  = 1; // Request code used for selecting a contact
    public static final int REQUEST_PICK_PHOTO    = 2; // Request code used for selecting a image from storage
    public static final int REQUEST_IMAGE_CAPTURE = 3; // Request code used for taking a photo via camera

    // Application Details
    String mVersionNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get last started session
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mCurrentSessionId = sp.getLong(LAST_STARTED_SESSION_ID, 0);

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
        mTitleFragment = new TitleFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mTitleFragment)
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
            helpBuilder.setMessage(getString(R.string.dialog_about_copyright) + "\n"
                    + getString(R.string.dialog_about_ssp));
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
            // TODO Change back portrait mode -> close open drawer?
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
                mTitleFragment = new TitleFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mTitleFragment)
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
                // Check if valid session was selected first
                Session currentSession = mSessionsList.getSessionById(mCurrentSessionId);
                if (currentSession == null) {
                    // inform user to select valid session first
                    toastMessage(getString(R.string.toast_invalid_session));

                    mNavigationDrawerFragment.changeToSessionsDrawer();
                } else {
                    // Show current session fragment
                    mCurrentSessionFragment = new CurrentSessionFragment(mCurrentSessionId);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, mCurrentSessionFragment)
                            .commit();
                }
                break;
            case 3:
                // Show sessions fragment
                mSessionsListFragment = new SessionsListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, mSessionsListFragment)
                        .commit();
                break;
            default:
                throw new RuntimeException("Invalid drawer fragment selected: " + position);
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
    public void onSessionAddDialogPositiveClick(String sessionName) {
        // save new session if valid name
        if (sessionName.trim().length() == 0) {
            toastMessage(getString(R.string.toast_session_add_canceled) + ".");
        } else {
            final int numSessions = mSessionsList.addSession(sessionName);
            // Inform SessionsListView about new renamed - refresh list view
            mSessionsListFragment.updateSessionsListView();
            // update Navigation Drawer player counter
            mNavigationDrawerFragment.updateNumberOfSessionsCounter(numSessions);

            toastMessage(getString(R.string.toast_session_added) + " '" + sessionName + "'.");
        }
    }

    @Override
    public void onSessionAddDialogNegativeClick() {
        toastMessage(getString(R.string.toast_session_add_canceled) + ".");
    }

    @Override
    public void onSessionRenameDialogPositiveClick(String sessionName, long sessionId) {
        // save renamed session if valid name
        if (sessionName.trim().length() == 0) {
            toastMessage(getString(R.string.toast_session_rename_canceled) + ".");
        } else {
            mSessionsList.renameSession(sessionName, sessionId);
            // Inform SessionsListView about renamed session - refresh list view
            mSessionsListFragment.updateSessionsListView();

            toastMessage(getString(R.string.toast_session_renamed) + " '" + sessionName + "'.");
        }
    }

    @Override
    public void onSessionRenameDialogNegativeClick() {
        toastMessage(getString(R.string.toast_session_rename_canceled) + ".");
    }

    /***********************************
     * Callbacks SessionOptionsDialog  *
     ***********************************/

    @Override
    public void onSessionOptionsDialogStartClick(long sessionId) {
        // Check if at least one player in session
        if (mSessionsList.getSessionById(sessionId).getNumberOfPlayers() == 0) {
            toastMessage(getString(R.string.toast_session_invalid_start));
        } else {
            // Save selection
            mCurrentSessionId = sessionId;
            // Store selection for next app running
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sp.edit().putLong(LAST_STARTED_SESSION_ID, sessionId).apply();
            // inform NavigationDrawer about external drawer change
            mNavigationDrawerFragment.changeToCurrentSessionDrawer(sessionId);
        }
    }

    @Override
    public void onSessionOptionsDialogPlayersClick(long sessionId) {
        // Check if session was already started
        if (mSessionsList.getSessionById(sessionId).getNumberOfGames() > 0) {
            toastMessage(getString(R.string.toast_session_select_players_invalid));
        } else {
            // Show player selection window
            SessionOptionsPlayerSelectionDialogFragment playerDialog =
                    new SessionOptionsPlayerSelectionDialogFragment(sessionId);
            playerDialog.show(getFragmentManager(), "SessionOptionsPlayerSelectionDialogFragment");
        }
    }

    @Override
    public void onSessionOptionsDialogRenameClick(long sessionId) {
        mSessionsListFragment.showSessionNameDialog(false, sessionId);
    }

    @Override
    public void onSessionOptionsDialogDeleteClick(long sessionId) {
         // get session name
        String name = mSessionsList.getSessionById(sessionId).getName();

        // remove session from list
        final int numSessions = mSessionsList.deleteSession(sessionId);

        toastMessage(getString(R.string.toast_session_deleted) + " '" + name + "'.");

        // Inform SessionsListView about removed session - refresh list view
        mSessionsListFragment.updateSessionsListView();

        // update Navigation Drawer player counter
        mNavigationDrawerFragment.updateNumberOfSessionsCounter(numSessions);
    }


    /**************************************************
     * Callbacks SessionOptionsPlayerSelectionDialog  *
     **************************************************/

    @Override
    public void onSessionOptionsPlayerSelectionDialogCancelClick() {
        toastMessage(getString(R.string.toast_session_options_player_selection_cancel) + ".");
    }

    @Override
    public void onSessionOptionsPlayerSelectionDialogOkClick(long sessionId, long[] selectedPlayerIds) {
        // Inform session about updated players list
        if (mSessionsList.replaceSessionPlayerList(sessionId, selectedPlayerIds)) {
            // Inform SessionsListView about updated session - refresh list view
            mSessionsListFragment.updateSessionsListView();
            toastMessage(getString(R.string.toast_session_options_player_selection_ok) +
                    " '" + mSessionsList.getSessionById(sessionId).getName() + "'.");
        } else {
            toastMessage(getString(R.string.toast_session_options_player_selection_cancel) + ".");
        }
    }


    /************************
     * Callbacks GameDialog *
     ************************/

    @Override
    public void onGameDialogClick(int playerPosition, Game.eGameResult result, int score, boolean backButton) {
        // Check for cancel button click
        if (playerPosition == 0 && backButton) {
            // TODO show toast
        } else {
            mCurrentSessionFragment.addPlayerGameResult(playerPosition, result, score, backButton);
        }

    }




    /****************************************
     * handle Intent results from Fragments *
     ****************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        final PlayerDialogFragment playerDialogFragment =
                (PlayerDialogFragment)getFragmentManager().findFragmentByTag("PlayerDialogFragment");
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
                            String name = cursor.getString(
                                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
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
                        cursor.close();
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
