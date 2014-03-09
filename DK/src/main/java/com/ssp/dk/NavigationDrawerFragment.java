package com.ssp.dk;

;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private MenuInflater mMenuInflater;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private LayoutInflater mInflater;

    private ArrayList<NavigationDrawerItem> mNavDrawerItemList;
    private NavigationDrawerListAdapter mNavDrawerItemListAdapter;

    // Fixed order of drawer element
    private final int DRAWER_POSITION_TITLE_SCREEN      = 0;
    private final int DRAWER_POSITION_PLAYERS           = 1;
    private final int DRAWER_POSITION_CURRENT_SESSION   = 2;
    private final int DRAWER_POSITION_SESSIONS          = 3;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mInflater = inflater;
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        // set up the drawer's list view with items
        mNavDrawerItemList = new ArrayList<NavigationDrawerItem>();
        NavigationDrawerItem itemTitleScreen = new NavigationDrawerItem(
                getString(R.string.navigation_drawer_list_item_title_title_screen),
                R.id.navigation_drawer_list_item_icon_title_screen);
        NavigationDrawerItem itemPlayer = new NavigationDrawerItem(
                getString(R.string.navigation_drawer_list_item_title_players),
                R.id.navigation_drawer_list_item_icon_players,
                true, String.valueOf(PlayerList.getInstance().getNumberOfPlayers()));
        NavigationDrawerItem itemCurrentSession = new NavigationDrawerItem(
                getString(R.string.navigation_drawer_list_item_title_current_session),
                R.id.navigation_drawer_list_item_icon_current_session);
        NavigationDrawerItem itemSessions = new NavigationDrawerItem(
                getString(R.string.navigation_drawer_list_item_title_sessions),
                R.id.navigation_drawer_list_item_icon_sessions);
        mNavDrawerItemList.add(itemTitleScreen);    // DRAWER_POSITION_TITLE_SCREEN
        mNavDrawerItemList.add(itemPlayer);         // DRAWER_POSITION_PLAYERS
        mNavDrawerItemList.add(itemCurrentSession); // DRAWER_POSITION_CURRENT_SESSION
        mNavDrawerItemList.add(itemSessions);       // DRAWER_POSITION_SESSIONS
        // setting the nav drawer list adapter
        mNavDrawerItemListAdapter = new NavigationDrawerListAdapter(mNavDrawerItemList);
        mDrawerListView.setAdapter(mNavDrawerItemListAdapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void updateNumberOfPlayersCounter(int numPlayers) {
        // replace old count
        NavigationDrawerItem countItem = mNavDrawerItemList.get(DRAWER_POSITION_PLAYERS);
        countItem.setCount(String.valueOf(numPlayers));
        mNavDrawerItemList.set(DRAWER_POSITION_PLAYERS, countItem);
        // Show new count in drawer
        mNavDrawerItemListAdapter.notifyDataSetChanged();
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Enabling action bar app icon and behaving it as toggle button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                // hide action bar menu items
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        // Save selection
        mCurrentSelectedPosition = position;

        //  Highlight selection in drawer (if later open again)
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        // Close drawer, because selection was made
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        // Inform caller about selection -> trigger fragment change
        if (mCallbacks != null && mDrawerListView != null) {
            String itemTitle = ((NavigationDrawerItem)mDrawerListView.getItemAtPosition(position)).getTitle();
            mCallbacks.onNavigationDrawerItemSelected(position, itemTitle);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mMenuInflater = inflater;
        // If the drawer is open, show no actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            menu.clear();
            inflater.inflate(R.menu.open_drawer, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (isDrawerOpen() && mMenuInflater != null) {
            // If the drawer is open, show the no actions in the action bar. See also
            // showGlobalContextActionBar, which controls the top-left area of the action bar.
            menu.clear();
            mMenuInflater.inflate(R.menu.open_drawer, menu);
            //showGlobalContextActionBar();
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position, String itemTitle);
    }


    /**********************************
     * Navigation drawer list adapter *
     **********************************/

    static class NavigationDrawerItemViewHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtCount;
    }

    public class NavigationDrawerListAdapter extends BaseAdapter {

        private ArrayList<NavigationDrawerItem> mNavDrawerItems;

        public NavigationDrawerListAdapter(ArrayList<NavigationDrawerItem> navDrawerItems) {
            mNavDrawerItems = navDrawerItems;
        }

        @Override
        public int getCount() {
            return mNavDrawerItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mNavDrawerItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NavigationDrawerItemViewHolder holder; // used for faster view access

            // Check for initial inflation
            if (convertView == null) {
                // inflate
                convertView = mInflater.inflate(R.layout.navigation_drawer_item, null);
                // set shortcuts
                holder = new NavigationDrawerItemViewHolder();
                holder.imgIcon = (ImageView) convertView.findViewById(R.id.navigation_drawer_icon);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.navigation_drawer_title);
                holder.txtCount = (TextView) convertView.findViewById(R.id.navigation_drawer_counter);
                convertView.setTag(holder);
            } else {
                holder = (NavigationDrawerItemViewHolder) convertView.getTag();
            }

            // Set drawer item parameters to view items
            holder.imgIcon.setImageResource(mNavDrawerItems.get(position).getIcon());
            holder.txtTitle.setText(mNavDrawerItems.get(position).getTitle());
            // displaying count - check whether it set visible or not
            if (mNavDrawerItems.get(position).getCounterVisibility()) {
                holder.txtCount.setText(mNavDrawerItems.get(position).getCount());
                holder.txtCount.setVisibility(View.VISIBLE);
            } else {
                // hide the counter view
                holder.txtCount.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

}
