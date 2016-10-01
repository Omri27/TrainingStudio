package zina_eliran.app;

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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingDetailsModeEnum;
import zina_eliran.app.BusinessEntities.BETrainingLevelEnum;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

public class TrainingDetailsActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener {

    LinearLayout notificationsSwitchesLayout;
    Switch userJoinedNotificationSwitch;
    Switch trainingFullNotificationSwitch;
    EditText descriptionEt;
    Spinner levelSpinner;
    ArrayAdapter<CharSequence> levelAdapter;
    EditText dateEt;
    EditText timeEt;
    EditText participatesEt;
    Spinner durationSpinner;
    ArrayAdapter<CharSequence> durationAdapter;

    ImageButton locationIBtn;
    Button actionBtn;
    BETrainingDetailsModeEnum activityMode;
    boolean isDirty;
    BETraining training;


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
            dateEt = (EditText) findViewById(R.id.training_details_date_et);
            timeEt = (EditText) findViewById(R.id.training_details_time_et);
            participatesEt = (EditText) findViewById(R.id.training_details_participates_et);
            locationIBtn = (ImageButton) findViewById(R.id.training_details_location_map_ibtn);

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
            dateEt.setEnabled(false);
            timeEt.setEnabled(false);
            participatesEt.setEnabled(false);
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

            switch (activityMode) {
                case training_details_create_mode:
                    //create object

                    //!!!validations will be added as ui elements such as spinners & calenders & google map elements at phase 2

                    training = new BETraining();
                    if (!validateBeforeSave()) {
                        return;
                    }
                    training.setDescription(descriptionEt.getText().toString());
                    training.setLevel(BETrainingLevelEnum.valueOf(levelSpinner.getSelectedItem().toString()));
                    training.setDuration(Integer.parseInt(durationSpinner.getSelectedItem().toString().replace("Min", "").trim()));
                    //create full date object
                    Date d = new Date();
                    training.setTrainingDate(d);
                    training.setMaxNumberOfParticipants(Integer.parseInt(participatesEt.getText().toString()));
                    training.setCurrentNumberOfParticipants(0);
                    training.setCreationDate(new Date());
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
            if (dateEt.getText().toString().isEmpty()) {
                isValid = false;
            }
            if (timeEt.getText().toString().isEmpty()) {
                isValid = false;
            }
            if (participatesEt.getText().toString().isEmpty()) {
                isValid = false;
            }


            if (!isValid) {
                Toast.makeText(_getAppContext(), _getString(R.string.inputs_missing_or_invalid), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }
        return  isValid;
    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            if (response != null) {
                if (response.getStatus() == BEResponseStatusEnum.error) {

                } else {

                    if (response.getActionType() == DALActionTypeEnum.getTraining) {
                        training = ((BETraining) response.getEntities().get(0));
                        //bind elements to the object fields
                        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat timeFormatter = new SimpleDateFormat("hh:mm");

                        descriptionEt.setText(training.getDescription());
                        levelSpinner.setSelection(levelAdapter.getPosition(training.getLevel().toString()));
                        levelAdapter.notifyDataSetChanged();
                        durationSpinner.setSelection(durationAdapter.getPosition(training.getDuration() + " Min"));
                        durationAdapter.notifyDataSetChanged();

                        dateEt.setText(dateFormatter.format(training.getTrainingDate()));
                        timeEt.setText(timeFormatter.format(training.getTrainingDate()));
                        participatesEt.setText("" + training.getMaxNumberOfParticipants());
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

        }
    }

}
