package com.emildiaz.runner.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emildiaz.runner.R;
import com.emildiaz.runner.model.Run;

import roboguice.fragment.RoboFragment;

public class RunDetailFragment extends Fragment {

    MapFragment mapFragment;

    private static final String EXTRA_ID = "id";

    private Run run;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Long runId = getArguments().getLong(EXTRA_ID);
        run = Run.findById(Run.class, runId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_run_detail, container, false);
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapFragment.drawPath(run);
    }

    public static final RunDetailFragment newInstance(Run run) {
        RunDetailFragment runDetailFragment = new RunDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_ID, run.getId());
        runDetailFragment.setArguments(bundle);
        return runDetailFragment;
    }
}
