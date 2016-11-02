package zina_eliran.app.Utils;


import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import zina_eliran.app.BusinessEntities.BEFragmentResultTypeEnum;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    DateTimeFragmentHandler activity;
    Calendar c;

    public TimePickerFragment(DateTimeFragmentHandler activity, Calendar c) {
        super();
        this.activity = activity;
        if(c == null){
            this.c  = Calendar.getInstance();
        }
        else {
            this.c = (Calendar)c.clone();
        }
    }

    public TimePickerFragment() {
        super();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 0);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 0);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        activity.onFragmentCallback(c, BEFragmentResultTypeEnum.time);
    }
}