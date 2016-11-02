package zina_eliran.app;

import android.app.DialogFragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import zina_eliran.app.BusinessEntities.BEFragmentResultTypeEnum;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingDetailsModeEnum;
import zina_eliran.app.BusinessEntities.BETrainingLevelEnum;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.DatePickerFragment;
import zina_eliran.app.Utils.DateTimeFragmentHandler;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.TimePickerFragment;

public class TrainingDetailsActivity extends BaseFragmentActivity
        implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener, DateTimeFragmentHandler {

    LinearLayout notificationsSwitchesLayout;
    Switch userJoinedNotificationSwitch;
    Switch trainingFullNotificationSwitch;
    EditText descriptionEt;
    Spinner levelSpinner;
    ArrayAdapter<CharSequence> levelAdapter;
    TextView dateTv;
    TextView timeTv;
    Spinner participatesSpinner;
    ArrayAdapter<CharSequence> participatesAdapter;
    Spinner durationSpinner;
    ArrayAdapter<CharSequence> durationAdapter;

    ImageButton locationIBtn;
    Button actionBtn;
    BETrainingDetailsModeEnum activityMode;
    boolean isDirty;
    BETraining training;
    Calendar trainingCalender;
    Location trainingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_details);

        //prevent open keyboard automatically
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        intent = getIntent();
        training = new BETraining();
        onCreateUI();
    }

    public void onCreateUI() {

        try {

            initActivityElements();

            initNotificationsSwithces();

            initActivityMode();

            initActionButton();

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
    }

    private void initActivityElements() {
        try {


            descriptionEt = (EditText) findViewById(R.id.training_details_training_name_et);
            locationIBtn = (ImageButton) findViewById(R.id.training_details_location_map_ibtn);
            dateTv = (TextView) findViewById(R.id.training_details_date_tv);
            timeTv = (TextView) findViewById(R.id.training_details_time_tv);

            //add events
            dateTv.setOnClickListener(this);
            timeTv.setOnClickListener(this);

            levelSpinner = (Spinner) findViewById(R.id.training_details_level_spinner);
            levelAdapter = ArrayAdapter.createFromResource(this,
                    R.array.training_level_values, R.layout.app_spinner_item);
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            levelSpinner.setAdapter(levelAdapter);
            levelSpinner.setSelection(0, true);
            levelAdapter.notifyDataSetChanged();


            durationSpinner = (Spinner) findViewById(R.id.training_details_duration_spinner);
            durationAdapter = ArrayAdapter.createFromResource(this,
                    R.array.training_duration_values, R.layout.app_spinner_item);
            durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            durationSpinner.setAdapter(durationAdapter);
            durationSpinner.setSelection(0, true);
            durationAdapter.notifyDataSetChanged();


            participatesSpinner = (Spinner) findViewById(R.id.training_details_participates_spinner);
            participatesAdapter = ArrayAdapter.createFromResource(this,
                    R.array.training_participates_number_values, R.layout.app_spinner_item);
            participatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            participatesSpinner.setAdapter(participatesAdapter);
            participatesSpinner.setSelection(0, true);
            participatesAdapter.notifyDataSetChanged();


        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
    }

    private void initNotificationsSwithces() {
        try {
            notificationsSwitchesLayout = (LinearLayout) findViewById(R.id.training_details_switches_layout);
            userJoinedNotificationSwitch = (Switch) findViewById(R.id.training_details_user_joined_notification_switch);
            trainingFullNotificationSwitch = (Switch) findViewById(R.id.training_details_training_full_notification_switch);
            userJoinedNotificationSwitch.setOnCheckedChangeListener(this);
            trainingFullNotificationSwitch.setOnCheckedChangeListener(this);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void initActionButton() {
        try {

            actionBtn = (Button) findViewById(R.id.training_details_action_btn);
            actionBtn.setOnClickListener(this);

            switch (activityMode) {
                case training_details_create_mode:
                    setActionButtonState("Create", false, true);
                    break;
                case training_details_join_mode:
                    setActionButtonState("Join", false, true);
                    break;
                case training_details_cancel_mode:
                    setActionButtonState("Cancel Training", true, true);
                    break;
                case training_details_leave_mode:
                    setActionButtonState("Leave", true, true);
                    break;
                case training_details_view_mode:
                    setActionButtonState("", false, false);
                    break;
            }


        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void setActionButtonState(String text, boolean isRedColor, boolean isVisible) {
        try {
            actionBtn.setText(text);
            if (isVisible) {
                if (isRedColor) {
                    actionBtn.setBackgroundColor(0xDDFF4A30);
                } else {
                    actionBtn.setBackgroundColor(0xAAFFFFFF);
                }
            } else {
                actionBtn.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void initActivityMode() {
        try {
            String _activityMode = getIntentParam(intent, _getString(R.string.training_details_activity_mode));
            if (_activityMode.isEmpty()) {
                activityMode = BETrainingDetailsModeEnum.training_details_view_mode;
            } else {
                activityMode = BETrainingDetailsModeEnum.valueOf(_activityMode);
            }


            if (activityMode != BETrainingDetailsModeEnum.training_details_create_mode) {
                sApi.getTraining(getIntentParam(intent, _getString(R.string.training_details_training_id)), this);
            }

            switch (activityMode) {
                case training_details_join_mode:
                case training_details_leave_mode:
                case training_details_view_mode:
                    notificationsSwitchesLayout.setVisibility(View.GONE);
                case training_details_cancel_mode:
                    disableTrainingElements();
                    break;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void disableTrainingElements() {
        try {

            descriptionEt.setEnabled(false);
            dateTv.setEnabled(false);
            timeTv.setEnabled(false);
            participatesSpinner.setEnabled(false);
            locationIBtn.setEnabled(false);
            levelSpinner.setEnabled(false);
            durationSpinner.setEnabled(false);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }

    }


    @Override
    public void onClick(View view) {
        try {

            switch (view.getId()) {
                case R.id.training_details_action_btn:
                    switch (activityMode) {
                        case training_details_create_mode:
                            //create object

                            //!!!validations will be added as ui elements such as spinners & calenders & google map elements at phase 2

                            training = new BETraining();
                            if (!validateBeforeSave()) {
                                return;
                            }
                            training.setDescription(descriptionEt.getText().toString());
                            training.setLevel(BETrainingLevelEnum.valueOf(levelSpinner.getSelectedItem().toString().replace("Level:", "").trim()));
                            training.setDuration(Integer.parseInt(durationSpinner.getSelectedItem().toString().replace("Min", "").trim()));
                            training.setTrainingDateTimeCalender(trainingCalender);
                            training.setMaxNumberOfParticipants(Integer.parseInt(participatesSpinner.getSelectedItem().toString().replace("Runners", "").trim()));
                            training.setCurrentNumberOfParticipants(0);
                            //training.setLocation(trainingLocation);
                            training.setCreatorId(sApi.getAppUser().getId());
                            training.setPatricipatedUserIds(new ArrayList<String>());
                            training.setStatus(BETrainingStatusEnum.open);
                            training.setJoinTrainingNotificationFlag(userJoinedNotificationSwitch.isChecked());
                            training.setTrainingFullNotificationFlag(trainingFullNotificationSwitch.isChecked());

                            sApi.createTraining(training, this);

                            break;
                        case training_details_join_mode:
                            sApi.joinTraining(training.getId(), sApi.getAppUser().getId(), this);
                            break;
                        case training_details_leave_mode:
                            sApi.leaveTraining(training.getId(), sApi.getAppUser().getId(), this);
                            break;
                        case training_details_cancel_mode:
                            if (isDirty) {
                                //save
                                training.setJoinTrainingNotificationFlag(userJoinedNotificationSwitch.isChecked());
                                training.setTrainingFullNotificationFlag(trainingFullNotificationSwitch.isChecked());
                                sApi.updateTraining(training, this);
                            } else {
                                //cancel
                                training.setStatus(BETrainingStatusEnum.cancelled);
                                sApi.updateTraining(training, this);
                            }
                            break;
                    }
                    break;
                case R.id.training_details_time_tv:
                    DialogFragment timeFragment = new TimePickerFragment(this, trainingCalender);
                    timeFragment.show(getFragmentManager(), "timePicker");
                    break;
                case R.id.training_details_date_tv:
                    DialogFragment dateFragment = new DatePickerFragment(this, trainingCalender);
                    dateFragment.show(getFragmentManager(), "datePicker");
                    break;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
    }

    private boolean validateBeforeSave() {

        boolean isValid = true;
        try {
            if (descriptionEt.getText().toString().isEmpty()) {
                isValid = false;
            }
            if (dateTv.getText().toString().isEmpty() || dateTv.getText().toString().contains("Pick")) {
                isValid = false;
            }
            if (timeTv.getText().toString().isEmpty() || timeTv.getText().toString().contains("Pick")) {
                isValid = false;
            }

            if (!isValid) {
                Toast.makeText(_getAppContext(), _getString(R.string.inputs_missing_or_invalid), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
        return isValid;
    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            if (response != null) {
                if (response.getStatus() == BEResponseStatusEnum.error) {
                    CMNLogHelper.logError("TrainingDetailsActivity", "error in training details callbacks | err:" + response.getMessage());
                    Toast.makeText(_getAppContext(), "Error while performing training action, please try again later.", Toast.LENGTH_LONG).show();

                } else if (response.getEntityType() == BETypesEnum.Trainings) {

                    if (response.getActionType() == DALActionTypeEnum.getTraining) {
                        training = ((BETraining) response.getEntities().get(0));
                        //bind elements to the object fields
                        descriptionEt.setText(training.getDescription());
                        levelSpinner.setSelection(levelAdapter.getPosition("Level: " + training.getLevel().toString()));
                        levelAdapter.notifyDataSetChanged();
                        durationSpinner.setSelection(durationAdapter.getPosition(training.getDuration() + " Min"));
                        durationAdapter.notifyDataSetChanged();
                        participatesSpinner.setSelection(participatesAdapter.getPosition((training.getMaxNumberOfParticipants() - 3) + " Runners"));
                        participatesAdapter.notifyDataSetChanged();

                        dateTv.setText(dateFormatter.format(training.getTrainingDateTimeCalender().getTime()));
                        timeTv.setText(timeFormatter.format(training.getTrainingDateTimeCalender().getTime()));
                        userJoinedNotificationSwitch.setChecked(training.isJoinTrainingNotificationFlag());
                        trainingFullNotificationSwitch.setChecked(training.isTrainingFullNotificationFlag());

                    } else if (response.getActionType() == DALActionTypeEnum.joinTraining ||
                            response.getActionType() == DALActionTypeEnum.leaveTraining ||
                            response.getActionType() == DALActionTypeEnum.createTraining ||
                            response.getActionType() == DALActionTypeEnum.updateTraining) {
                        Map<String, String> intentParams = new HashMap<>();
                        switch (activityMode) {
                            case training_details_create_mode:
                                //navigate to my created trainings list
                                intentParams.put(_getString(R.string.training_list_manage_training_permission), "true");
                                intentParams.put(_getString(R.string.training_list_my_trainings_mode), "true");
                                navigateToActivity(this, TrainingsListActivity.class, true, intentParams);
                                break;
                            case training_details_join_mode:
                            case training_details_leave_mode:
                                //navigate to my joined trainings list
                                intentParams.put(_getString(R.string.training_list_manage_training_permission), "true");
                                intentParams.put(_getString(R.string.training_list_my_trainings_mode), "true");
                                intentParams.put(_getString(R.string.training_list_join_mode), "true");
                                navigateToActivity(this, TrainingsListActivity.class, true, intentParams);
                                break;
                            case training_details_cancel_mode:
                                if (isDirty) {
                                    isDirty = false;
                                    setActionButtonState("Cancel Training", true, true);
                                } else {
                                    //navigate to my created trainings list
                                    intentParams.put(_getString(R.string.training_list_manage_training_permission), "true");
                                    intentParams.put(_getString(R.string.training_list_my_trainings_mode), "true");
                                    navigateToActivity(this, TrainingsListActivity.class, true, intentParams);
                                }
                                break;
                        }
                    }
                } else {
                    CMNLogHelper.logError("TrainingDetailsActivity", "wrong action type in callback" + response.getEntityType() + ", " + response.getActionType());
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try {
            if (activityMode == BETrainingDetailsModeEnum.training_details_cancel_mode) {
                isDirty = true;
                setActionButtonState("Update", false, true);
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
    }

    @Override
    public void onFragmentCallback(Calendar value, BEFragmentResultTypeEnum entityType) {
        try {
            Calendar cal = Calendar.getInstance();
            if (entityType == BEFragmentResultTypeEnum.date) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                if (value.getTime().before(cal.getTime()) && !isTodaySelectedDate(value)) {
                    Toast.makeText(_getAppContext(), "Training date must be equal or later than today.", Toast.LENGTH_LONG).show();
                    dateTv.setText("Pick Date");
                } else {

                    if (trainingCalender == null) {
                        trainingCalender = value;
                    } else {
                        trainingCalender.set(Calendar.YEAR, value.get(Calendar.YEAR));
                        trainingCalender.set(Calendar.MONTH, value.get(Calendar.MONTH));
                        trainingCalender.set(Calendar.DAY_OF_MONTH, value.get(Calendar.DAY_OF_MONTH));
                    }

                    dateTv.setText(dateFormatter.format(value.getTime()));
                }
            } else if (entityType == BEFragmentResultTypeEnum.time) {
                cal.set(Calendar.YEAR, 0);
                cal.set(Calendar.MONTH, 0);
                cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.add(Calendar.HOUR, 6);
                if (trainingCalender != null &&
                        isTodaySelectedDate(trainingCalender) &&
                        value.getTime().before(cal.getTime())) {
                    Toast.makeText(_getAppContext(), "Training time must be at least 6 hours later then now.", Toast.LENGTH_LONG).show();
                    timeTv.setText("Pick Time");
                } else {

                    if (trainingCalender == null) {
                        trainingCalender = value;
                    } else {
                        trainingCalender.set(Calendar.HOUR, value.get(Calendar.HOUR_OF_DAY));
                        trainingCalender.set(Calendar.MINUTE, value.get(Calendar.MINUTE));
                    }

                    if (value.get(Calendar.HOUR_OF_DAY) > 12) {
                        trainingCalender.set(Calendar.AM_PM, Calendar.PM);
                    }
                    else {
                        trainingCalender.set(Calendar.AM_PM, Calendar.AM);
                    }

                    timeTv.setText(timeFormatter.format(value.getTime()));
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }

    }

    private boolean isTodaySelectedDate(Calendar cal) {
        try {

            Calendar today = Calendar.getInstance();

            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            return (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH));
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
        return false;
    }
}
