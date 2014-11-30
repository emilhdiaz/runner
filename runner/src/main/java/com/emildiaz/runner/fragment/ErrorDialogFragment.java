package com.emildiaz.runner.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ErrorDialogFragment extends DialogFragment {
    private Dialog dialog;

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return this.dialog;
    }
}
