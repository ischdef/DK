package com.ssp.dk;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        PlayerDialogFragment.PlayerAddDialogCallbacks,
        PlayerOptionsDialogFragment.PlayerOptionsDialogCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mActionBarTitle;


    /**
     * List with all stored players
     */
    private PlayerList mPlayerList = PlayerList.getInstance();

    // Shortcuts
    private PlayerListFragment mPlayerListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the activity content from a layout resource.
        // The resource will be inflated, adding all top-level views to the activity.
        setContentView(R.layout.activity_main);

        // Get navigationDrawer fragment
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set actionBarTitle to name of app
        mActionBarTitle = getTitle();

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

        // Add test player
        Drawable playerImage = getResources().getDrawable(R.drawable.no_user_logo);
        mPlayerList.addPlayer("Test Player 2", playerImage);
        mPlayerList.addPlayer("Test Player 1", playerImage);
        mPlayerList.addPlayer("Test Player 3", playerImage);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        // show selected navigation drawer (if any)
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mActionBarTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            menu.clear();
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        // get title of current selected drawer
        ListView drawerView = (ListView) mNavigationDrawerFragment.getView();
        if (drawerView == null) {
            Toast.makeText(getApplicationContext(), "Drawer View error.", Toast.LENGTH_SHORT).show();
            return;
        }
        String DrawerTitle = drawerView.getItemAtPosition(position).toString();

        // Trigger fragment change depending on selected drawer
        if (DrawerTitle.equals(getString(R.string.drawer_title_playerList))) {
            // Show PlayerList fragment
            FragmentManager fragmentManager = getFragmentManager();
            mPlayerListFragment= new PlayerListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mPlayerListFragment)
                    .commit();
        } else if (DrawerTitle.equals(getString(R.string.drawer_title_session))) {
            // TODO Show session fragment
            Toast.makeText(getApplicationContext(), getString(R.string.drawer_title_session), Toast.LENGTH_SHORT).show();
        } else if (DrawerTitle.equals(getString(R.string.drawer_title_game))) {
            // TODO Show game fragment
            Toast.makeText(getApplicationContext(), getString(R.string.drawer_title_game), Toast.LENGTH_SHORT).show();
        } else {
            // TODO exception
            Toast.makeText(getApplicationContext(), "Drawer Selection Error!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update action bar title to selected drawer title
        mActionBarTitle = DrawerTitle;
        getActionBar().setTitle(mActionBarTitle);
    }


    /*****************************
     * Callbacks PlayerDialog *
     *****************************/

    @Override
    public void onPlayerAddDialogPositiveClick(String PlayerName, Drawable PlayerImage) {
        // save new player
        mPlayerList.addPlayer(PlayerName, PlayerImage);
        Toast.makeText(getApplicationContext(), getText(R.string.toast_player_added) + " '" + PlayerName + "'.",
                Toast.LENGTH_SHORT).show();
        // Inform PlayerListView about new player
        mPlayerListFragment.updatePlayerListView();
    }

    @Override
    public void onPlayerAddDialogNegativeClick() {
        Toast.makeText(getApplicationContext(), getText(R.string.toast_player_add_canceled) + ".",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayerEditDialogPositiveClick(int listPosition, String PlayerName, Drawable PlayerImage) {
        // save player changes
        mPlayerList.editPlayer(listPosition, PlayerName, PlayerImage);
        Toast.makeText(getApplicationContext(), getText(R.string.toast_player_edit) + " '" + PlayerName + "'.",
                Toast.LENGTH_SHORT).show();
        // Inform PlayerListView about changed player
        mPlayerListFragment.updatePlayerListView();
    }

    @Override
    public void onPlayerEditDialogNegativeClick() {
        Toast.makeText(getApplicationContext(), getText(R.string.toast_player_edit_canceled) + ".",
                Toast.LENGTH_SHORT).show();
    }


    /*********************************
     * Callbacks PlayerOptionsDialog *
     *********************************/

    @Override
    public void onPlayerOptionsDialogDeleteClick(int position) {
        // get player name
        String name = mPlayerList.getPlayer(position).getName();
        // remove player from list
        mPlayerList.deletePlayer(position);
        Toast.makeText(getApplicationContext(), getText(R.string.toast_player_deleted) + " '" + name + "'.",
                Toast.LENGTH_SHORT).show();
        // Inform PlayerListView about removed player
        mPlayerListFragment.updatePlayerListView();
    }

    @Override
    public void onPlayerOptionsDialogEditClick(int position) {
        // Show 'edit player' dialog window
        DialogFragment dialog = new PlayerDialogFragment(position);
        dialog.show(getFragmentManager(), "PlayerDialogFragment");
    }
}
