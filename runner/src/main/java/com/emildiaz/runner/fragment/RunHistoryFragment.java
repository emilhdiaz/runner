package com.emildiaz.runner.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.emildiaz.runner.R;
import com.emildiaz.runner.db.RunTable;
import com.emildiaz.runner.model.Run;

import java.util.List;
import java.util.Locale;

public class RunHistoryFragment extends ListFragment {

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
    private ArrayAdapter adapter;

    public interface RunSelectedListener {
        public void onRunSelected(Run run);
    }

    public class RunArrayAdapter extends ArrayAdapter<Run> {

        RunArrayAdapter(Context context, List<Run> runs) {
            super(context, R.layout.fragment_run_history_list_item, runs);
        }

        public View getView(int position, View view, ViewGroup parent) {

            // Inflate the view the first time
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.fragment_run_history_list_item, null);
            }

            // Update the view
            Run run = getItem(position);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            TextView distance = (TextView) view.findViewById(R.id.distance);
            TextView date = (TextView) view.findViewById(R.id.date);
            distance.setText(String.format("%.2f, mile(s)", run.calculateDistance()));
            date.setText(run.getStartDateTime().format("WWW, MMM D ", Locale.ENGLISH));

            return view;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Run> runs = Run.listAll(Run.class);
        adapter = new RunArrayAdapter(getActivity(), runs);

        // Setup the list adapter
        setListAdapter(adapter);
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
        Run run = (Run) adapter.getItem(position);
        runSelectedListener.onRunSelected(run);
    }
}
