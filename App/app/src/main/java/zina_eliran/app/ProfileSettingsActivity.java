package zina_eliran.app;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

public class ProfileSettingsActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener {


    ImageButton myPicIBtn;
    Switch privateProfileSwitch;
    TextView nameTv;
    TextView emailTv;
    EditText countryEt; //disabled at this moment
    EditText cityEt;
    Spinner levelSpinner;
    ArrayAdapter<CharSequence> levelAdapter;
    Spinner ageSpinner;
    ArrayAdapter<String> ageAdapter;
    Spinner weightSpinner;
    ArrayAdapter<String> weightAdapter;
    Spinner heightSpinner;
    ArrayAdapter<String> heightAdapter;
    ToggleButton genderTb;
    ProgressBar pBar;

    CheckBox sundayCbox;
    CheckBox mondayCbox;
    CheckBox tuesdayCbox;
    CheckBox wednesdayCbox;
    CheckBox thursdayCbox;
    CheckBox fridayCbox;
    CheckBox saturdayCbox;

    CheckBox firstCycleHoursCbox;
    CheckBox secondCycleHoursCbox;
    CheckBox thirdCycleHoursCbox;

    Switch trainingCanceledNotificationSwitch;
    Switch trainingFullNotificationSwitch;
    Switch trainingHourRemainderNotificationSwitch;

    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_profile_settings);

            //prevent open keyboard automatically
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            onCreateUI();

            bindUserDetails();
        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private void bindUserDetails() {
        try {
            if (sApi.getAppUser() != null) {
                BEUser user = sApi.getAppUser();

                nameTv.setText(user.getName());
                emailTv.setText(user.getEmail());
                privateProfileSwitch.setChecked(user.isPrivateProfile());
                countryEt.setText(user.getCountry());
                cityEt.setText(user.getCity());
                genderTb.setChecked(user.isMale());

                levelSpinner.setSelection(levelAdapter.getPosition(user.getTrainingLevel().toString()));
                levelAdapter.notifyDataSetChanged();
                ageSpinner.setSelection(ageAdapter.getPosition("Age: " + Integer.toString(user.getAge())));
                ageAdapter.notifyDataSetChanged();
                weightSpinner.setSelection(weightAdapter.getPosition(Float.toString(user.getWeigth()) + " KG"));
                weightAdapter.notifyDataSetChanged();
                heightSpinner.setSelection(heightAdapter.getPosition(Float.toString(user.getHeigth()) + " (m)"));
                heightAdapter.notifyDataSetChanged();

                saturdayCbox.setChecked(user.getMyPreferredDays().contains(7));
                sundayCbox.setChecked(user.getMyPreferredDays().contains(1));
                mondayCbox.setChecked(user.getMyPreferredDays().contains(2));
                tuesdayCbox.setChecked(user.getMyPreferredDays().contains(3));
                wednesdayCbox.setChecked(user.getMyPreferredDays().contains(4));
                thursdayCbox.setChecked(user.getMyPreferredDays().contains(5));
                fridayCbox.setChecked(user.getMyPreferredDays().contains(6));

                firstCycleHoursCbox.setChecked(user.getMyPreferredDays().contains(1));
                secondCycleHoursCbox.setChecked(user.getMyPreferredDays().contains(2));
                thirdCycleHoursCbox.setChecked(user.getMyPreferredDays().contains(3));

                trainingCanceledNotificationSwitch.setChecked(user.isTrainingCancelledNotification());
                trainingFullNotificationSwitch.setChecked(user.isTrainingFullNotification());
                trainingHourRemainderNotificationSwitch.setChecked(user.isTrainingRemainderNotification());

            }
        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private void onCreateUI() {
        try {

            //bind ui
            myPicIBtn = (ImageButton) findViewById(R.id.profile_settings_my_picture_ibtn);
            readProfileImage();

            nameTv = (TextView) findViewById(R.id.profile_settings_name_tv);
            emailTv = (TextView) findViewById(R.id.profile_settings_email_tv);
            countryEt = (EditText) findViewById(R.id.profile_settings_country_et);
            cityEt = (EditText) findViewById(R.id.profile_settings_city_et);
            genderTb = (ToggleButton) findViewById(R.id.profile_settings_gender_tb);
            saveBtn = (Button) findViewById(R.id.profile_settings_save_btn);

            pBar = (ProgressBar) findViewById(R.id.profile_settings_pbar);
            pBar.setVisibility(View.INVISIBLE);
            pBar.bringToFront();

            initActivitySpinners();

            initActivitySwitches();

            initCheckBoxes();

            //add events
            myPicIBtn.setOnClickListener(this);
            genderTb.setOnCheckedChangeListener(this);
            saveBtn.setOnClickListener(this);

        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private void readProfileImage() {
        try {
            myPicIBtn.setImageBitmap(readProfileImageFromInternalStorage());
        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private void initActivitySpinners() {
        try {

            levelSpinner = (Spinner) findViewById(R.id.profile_settings_level_spinner);
            levelAdapter = ArrayAdapter.createFromResource(this,
                    R.array.training_level_values, R.layout.app_spinner_item);
            levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            levelSpinner.setAdapter(levelAdapter);
            levelSpinner.setSelection(0, true);
            levelAdapter.notifyDataSetChanged();

            ArrayList<String> list = new ArrayList<>();
            for (int i = 18; i < 100; i++) {
                list.add("Age: " + i);
            }
            ageSpinner = (Spinner) findViewById(R.id.profile_settings_age_spinner);
            ageAdapter = new ArrayAdapter<String>(this, R.layout.app_spinner_item, list);
            ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ageSpinner.setAdapter(ageAdapter);
            ageSpinner.setSelection(0, true);
            ageAdapter.notifyDataSetChanged();


            list = new ArrayList<>();
            for (int i = 40; i < 150; i++) {
                list.add("" + i + " KG");
                list.add("" + i + ".5 KG");
            }
            weightSpinner = (Spinner) findViewById(R.id.profile_settings_weight_spinner);
            weightAdapter = new ArrayAdapter<String>(this, R.layout.app_spinner_item, list);
            weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            weightSpinner.setAdapter(weightAdapter);
            weightSpinner.setSelection(0, true);
            weightAdapter.notifyDataSetChanged();


            list = new ArrayList<>();
            for (int i = 50; i < 99; i++) {
                list.add("1." + i + " (m)");
            }

            for (int i = 0; i < 20; i++) {
                list.add("2." + i + " (m)");
            }
            heightSpinner = (Spinner) findViewById(R.id.profile_settings_height_spinner);
            heightAdapter = new ArrayAdapter<String>(this, R.layout.app_spinner_item, list);
            heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            heightSpinner.setAdapter(heightAdapter);
            heightSpinner.setSelection(0, true);
            heightAdapter.notifyDataSetChanged();


        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private void initActivitySwitches() {
        try {
            //bind ui
            privateProfileSwitch = (Switch) findViewById(R.id.profile_settings_private_profile_switch);
            trainingCanceledNotificationSwitch = (Switch) findViewById(R.id.profile_settings_training_canceled_notification_switch);
            trainingFullNotificationSwitch = (Switch) findViewById(R.id.profile_settings_training_full_notification_switch);
            trainingHourRemainderNotificationSwitch = (Switch) findViewById(R.id.profile_settings_training_remainder_notification_switch);

        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private void initCheckBoxes() {
        try {

            saturdayCbox = (CheckBox) findViewById(R.id.profile_settings_saturday_check_box);
            sundayCbox = (CheckBox) findViewById(R.id.profile_settings_sunday_check_box);
            mondayCbox = (CheckBox) findViewById(R.id.profile_settings_monday_check_box);
            tuesdayCbox = (CheckBox) findViewById(R.id.profile_settings_tuesday_check_box);
            wednesdayCbox = (CheckBox) findViewById(R.id.profile_settings_wednesday_check_box);
            thursdayCbox = (CheckBox) findViewById(R.id.profile_settings_thursday_check_box);
            fridayCbox = (CheckBox) findViewById(R.id.profile_settings_friday_check_box);
            firstCycleHoursCbox = (CheckBox) findViewById(R.id.profile_settings_first_cycle_hours_check_box);
            secondCycleHoursCbox = (CheckBox) findViewById(R.id.profile_settings_second_cycle_hours_check_box);
            thirdCycleHoursCbox = (CheckBox) findViewById(R.id.profile_settings_third_cycle_hours_check_box);

        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    private String saveProfileImageToInternalStorage(Bitmap bitmapImage) {
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/<app>/app_data/appImagesDir
            File directory = cw.getDir("appImagesDir", Context.MODE_PRIVATE);
            // Create appImagesDir
            File myPath = new File(directory, "profilePic.jpg");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(myPath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
                }
            }
            return directory.getAbsolutePath();

        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
        return "";

    }

    private Bitmap readProfileImageFromInternalStorage() {

        Bitmap profilePic = null;// = BitmapFactory.decodeResource(getResources(), R.drawable.app_man_icon);
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("appImagesDir", Context.MODE_PRIVATE);
            File f = new File(directory, "profilePic.jpg");
            profilePic = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
        return profilePic;
    }

    public ArrayList<Integer> getPreferredDays() {
        ArrayList<Integer> preferredDays = new ArrayList<>();
        if (sundayCbox.isChecked()) {
            preferredDays.add(1);
        }
        if (mondayCbox.isChecked()) {
            preferredDays.add(2);
        }
        if (tuesdayCbox.isChecked()) {
            preferredDays.add(3);
        }
        if (wednesdayCbox.isChecked()) {
            preferredDays.add(4);
        }
        if (thursdayCbox.isChecked()) {
            preferredDays.add(5);
        }
        if (fridayCbox.isChecked()) {
            preferredDays.add(6);
        }
        if (saturdayCbox.isChecked()) {
            preferredDays.add(7);
        }


        return preferredDays;
    }

    public ArrayList<Integer> getPreferredHours() {
        ArrayList<Integer> preferredHours = new ArrayList<>();
        if (firstCycleHoursCbox.isChecked()) {
            preferredHours.add(1);
        }
        if (secondCycleHoursCbox.isChecked()) {
            preferredHours.add(2);
        }
        if (thirdCycleHoursCbox.isChecked()) {
            preferredHours.add(3);
        }
        return preferredHours;
    }
    

    @Override
    public void onClick(View view) {
        try {

            switch (view.getId()) {
                case R.id.profile_settings_my_picture_ibtn:
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(i, RESULT_LOAD_IMG);
                    break;
                case R.id.profile_settings_save_btn:

                    //update the app user object with the activity data & send to server
                    BEUser user = sApi.getAppUser();
                    user.setActive(true);
                    user.setPrivateProfile(privateProfileSwitch.isChecked());
                    user.setCountry("Israel");
                    //user.setCountry(countryEt.getText().toString());//currently disabled, set only to israel.
                    user.setCity(cityEt.getText().toString());
                    user.setTrainingLevel(levelSpinner.getSelectedItem().toString());
                    user.setAge(ageSpinner.getSelectedItem().toString());
                    user.setMale(genderTb.isChecked());
                    user.setHeigth(heightSpinner.getSelectedItem().toString());
                    user.setWeigth(weightSpinner.getSelectedItem().toString());

                    ArrayList<Integer> list = getPreferredDays();
                    user.setMyPreferredDays(list);

                    list = getPreferredHours();
                    user.setMyPreferredHours(list);

                    user.setTrainingCancelledNotification(trainingCanceledNotificationSwitch.isChecked());
                    user.setTrainingFullNotification(trainingFullNotificationSwitch.isChecked());
                    user.setTrainingRemainderNotification(trainingHourRemainderNotificationSwitch.isChecked());

                    //set spinner on
                    pBar.setVisibility(View.VISIBLE);
                    sApi.setActionResponse(null);
                    saveBtn.setEnabled(false);

                    sApi.updateUser(user, this);
                    break;
            }


        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try {
        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            switch (requestCode) {
                case RESULT_LOAD_IMG:
                    try {
                        Uri selectedImage = data.getData();
                        //start crop activity on the selected picture
                        CropImage.activity(selectedImage)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(this);

                    } catch (Exception e) {
                        CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    try {

                        Bitmap b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), CropImage.getActivityResult(data).getUri());
                        saveProfileImageToInternalStorage(b);
                        myPicIBtn.setImageBitmap(b);
                        myPicIBtn.setBackgroundColor(Color.TRANSPARENT);
                        myPicIBtn.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (Exception e) {
                        CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
                    }
                    break;
            }
        }
    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            if (response != null) {
                if (response.getStatus() == BEResponseStatusEnum.error) {
                    CMNLogHelper.logError("ProfileSettingsActivity", "error in save user callback | err:" + response.getMessage());
                    Toast.makeText(_getAppContext(), "Error while saving user data, please try again later.", Toast.LENGTH_LONG).show();
                } else if (response.getActionType() == DALActionTypeEnum.updateUser && response.getEntityType() == BETypesEnum.Users) {
                    //set spinner off
                    pBar.setVisibility(View.INVISIBLE);
                    saveBtn.setEnabled(true);
                    sApi.setAppUser((BEUser) response.getEntities().get(0));
                    Toast.makeText(_getAppContext(), "Your data saved successfully.", Toast.LENGTH_LONG).show();
                } else {
                    CMNLogHelper.logError("ProfileSettingsActivity", "wrong action type in callback" + response.getEntityType() + ", " + response.getActionType());
                }
            }
        } catch (Exception e) {
            CMNLogHelper.logError("ProfileSettingsActivity", e.getMessage());
        }
    }


}
