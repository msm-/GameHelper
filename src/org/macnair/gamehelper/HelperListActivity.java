package org.macnair.gamehelper;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.Activity;

/**
 * An activity representing a list of Helpers.
 * The activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link HelperListFragment} and the item details (if present) is a
 * {@link SimpleScoring}.
 * <p>
 * This activity also implements the required
 * {@link HelperListFragment.Callbacks} interface to listen for item selections.
 */
public class HelperListActivity extends Activity implements
		HelperListFragment.Callbacks, 
		PlayerDialog.PlayerDialogListener {

    @SuppressWarnings("unused")
	private static final String TAG = "MyHelperListActivity";  

    private PlayersModel pm = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helper_list);
		pm = new PlayersModel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.players, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; show master list
	    		FragmentManager fm = getFragmentManager();
	    		fm.beginTransaction().show(fm.findFragmentById(R.id.helper_list)).commit();
	        	
	        	// Set the home icon to no longer be an up icon
	        	ActionBar actionBar = getActionBar();
	            actionBar.setDisplayHomeAsUpEnabled(false);
	            
	            return true;
	            
	        case R.id.player_chooser:
	        	DialogFragment pc = new PlayerDialog();
	        	
	        	// Pass the saved Players as an array argument 
	        	Bundle bdl = new Bundle();
	        	bdl.putParcelableArrayList(PlayerDialog.ARG_PLAYERS, (ArrayList<Player>)pm.getSortedPlayers(Player.SEEN_THEN_NAME_ORDER));
	        	pc.setArguments(bdl);
	        	
	        	pc.show(getFragmentManager(),"players");
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Callback method from {@link HelperListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		// show the detail view in this activity by
		// adding or replacing the relevant helper fragment using a
		// fragment transaction.
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		SimpleScoring fragment = new SimpleScoring();
		ft.replace(R.id.helper_detail_container, (Fragment) fragment);
		// Then hide the master list
		ft.hide(fm.findFragmentById(R.id.helper_list));
		ft.commit();
		
		// Set the home icon to be an up icon
    	ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onPlayerDialogOK(List<Player> selectedPlayers) {
		for (Player p : selectedPlayers) {
			if (! p.isAnonymous()) {
				p.setSeenNow();
			}
		}
		
		// TODO Pass change in players on to active helper
		Toast.makeText(this, "Got " + selectedPlayers.size() + " players", Toast.LENGTH_SHORT).show();
	}
}
