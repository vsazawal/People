package edu.umd.cs.people;

import android.app.LoaderManager;

import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.provider.ContactsContract.Contacts; //the Contacts table



public class PersonListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener
{


    private static final int PEOPLE_ID = 1; // can be any int
    private SimpleCursorAdapter mAdapter;
    private String mCurFilter;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ListView listView = (ListView) findViewById(R.id.person_list);
        assert listView != null;
        setupListView(listView);

        mCurFilter = null;

    }

    private void setupListView(@NonNull ListView lView) {

        getLoaderManager().initLoader(PEOPLE_ID, null, this);


        //pass null for cursor for now (third parameter in the constructor below)
        mAdapter = new SimpleCursorAdapter(this, R.layout.person_list_content, null,
                new String[] {Contacts.DISPLAY_NAME_PRIMARY}, new int[]{ R.id.name },
                Adapter.NO_SELECTION);
        lView.setAdapter(mAdapter);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        Uri baseUri;
        if (mCurFilter != null) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mCurFilter));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }


        return new CursorLoader(this, baseUri,  //URI of Contacts table (or Filter)
                new String[] {Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY}, // The columns to return for each row
                null,  // Selection criteria
                null,  // Ditto
                null); // The sort order for the returned rows
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = new SearchView(this);
        MenuItem item = menu.findItem(R.id.search);
        item.setActionView(mSearchView);
        mSearchMenuItem = item;

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setIconified(false);
        mSearchView.setFocusable(true);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        // superclass handles the searchview
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onClose() {

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        //mSearchMenuItem.collapseActionView();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(PEOPLE_ID, null, this);
        return true;


    }
}






