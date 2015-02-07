package com.emildiaz.runner.fragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.emildiaz.runner.R;
import com.emildiaz.runner.db.RunTable;
import com.emildiaz.runner.model.Run;
import com.emildiaz.runner.providers.RunProvider;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class RunHistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RUN_LOADER = 2;
    private static final String[] cursorColumns = {
        RunTable.COLUMN_DISTANCE,
        RunTable.COLUMN_DATE
    };
    private static final int[] viewFields = {
        R.id.distance,
        R.id.date,
    };
    private RunSelectedListener runSelectedListener;
    private SimpleCursorAdapter adapter;

    public interface RunSelectedListener {
        public void onRunSelected(Run run);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(RUN_LOADER, null, this);

        adapter = new SimpleCursorAdapter(
            this.getActivity(),
            R.layout.fragment_run_history_list_item,
            null,
            cursorColumns,
            viewFields,
            0
        );

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                String text;
                switch (view.getId()) {
                    case R.id.distance:
                        text = String.format("%.2f, mile(s)", cursor.getDouble(columnIndex));
                        ((TextView) view).setText(text);
                        return true;
                    case R.id.date:
                        DateTime date = DateTime.forInstant(cursor.getLong(columnIndex), TimeZone.getDefault());
                        text = date.format("WWW, MMM D ", Locale.ENGLISH);
                        ((TextView) view).setText(text);
                        return true;
                }
                return false;
            }
        });

        // Setup the list adapter
        setListAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(
            this.getActivity(),
            RunProvider.CONTENT_URI,
            null,
            null,
            null,
            null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Make sure parent activity implements RunSelectedListener
        if (!(activity instanceof RunSelectedListener)) {
            throw new ClassCastException(activity.toString() + " must implement RunSelectedListener");
        }

        runSelectedListener = (RunSelectedListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        runSelectedListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Run run = Run.get(getActivity(), id);
        runSelectedListener.onRunSelected(run);
    }
}
