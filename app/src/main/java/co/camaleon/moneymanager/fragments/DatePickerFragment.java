/*NOT USED*/

package co.camaleon.moneymanager.fragments;

/**
 * Created by Usuario on 30/03/2015.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;


public class DatePickerFragment extends DialogFragment{

    private static final String YEAR_KEY = "year";
    private static final String MONTH_KEY = "month";
    private static final String DAY_KEY = "day";

    private boolean isDialogShowing = false;

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    static DatePickerFragment newInstance(int year, int month, int day, DatePickerDialog.OnDateSetListener onDateSetListener) {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.setOnDateSetListener(onDateSetListener);

        //Pass the date in a bundle.
        Bundle bundle = new Bundle();
        bundle.putSerializable(YEAR_KEY, year);
        bundle.putSerializable(MONTH_KEY, month);
        bundle.putSerializable(DAY_KEY, day);

        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        int year = (Integer) getArguments().getSerializable(YEAR_KEY);
        int month = (Integer) getArguments().getSerializable(MONTH_KEY);
        int day = (Integer) getArguments().getSerializable(DAY_KEY);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);

        return dialog;
    }

    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isDialogShowing) return;
        super.show(manager, tag);

        isDialogShowing = true;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        isDialogShowing = false;
        super.onDismiss(dialog);
    }
}