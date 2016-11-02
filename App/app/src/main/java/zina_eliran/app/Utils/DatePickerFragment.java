package zina_eliran.app.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import zina_eliran.app.BusinessEntities.BEFragmentResultTypeEnum;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    DateTimeFragmentHandler activity;
    Calendar c;

    public DatePickerFragment(DateTimeFragmentHandler activity, Calendar c) {
        super();
        this.activity = activity;
        if(c == null){
            this.c  = Calendar.getInstance();
        }
        else {
            this.c = (Calendar)c.clone();
        }
    }

    public DatePickerFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0, 0);
        activity.onFragmentCallback(c, BEFragmentResultTypeEnum.date);
    }
}