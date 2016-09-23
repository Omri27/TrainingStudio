package zina_eliran.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.AppConstsEnum;

public class LobbyActivity extends BaseActivity implements View.OnClickListener {

    Button createTrainingBtn;
    Button publicTrainingsBtn;
    Button myProfileSettingsBtn;
    Button myTrainingsBtn;
    Button startTrainingBtn;
    Button myProgressBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

/*        if (isVerified()) {
            onCreateUI();
        }
         //navigate to Lobby if the user is verified
         else {
            navigateToActivity(this, RegisterActivity.class, false, null);
        }*/

        onCreateUI();

    }

    private void onCreateUI() {
        try {

            //bind ui
            createTrainingBtn = (Button) findViewById(R.id.lobby_create_training_btn);
            publicTrainingsBtn = (Button) findViewById(R.id.lobby_public_trainings_btn);
            myProfileSettingsBtn = (Button) findViewById(R.id.lobby_my_profile_settings_btn);
            myTrainingsBtn = (Button) findViewById(R.id.lobby_my_trainings_btn);
            startTrainingBtn = (Button) findViewById(R.id.lobby_start_training_btn);
            myProgressBtn = (Button) findViewById(R.id.lobby_my_progress_btn);

            //add events
            createTrainingBtn.setOnClickListener(this);
            publicTrainingsBtn.setOnClickListener(this);
            myProfileSettingsBtn.setOnClickListener(this);
            myTrainingsBtn.setOnClickListener(this);
            startTrainingBtn.setOnClickListener(this);
            myProgressBtn.setOnClickListener(this);

        } catch (Exception e) {
            CMNLogHelper.logError("LobbyActivity", e.getMessage());
        }
    }

    public boolean isVerified() {
        return !readFromSharedPreferences(AppConstsEnum.userVerificationPermission.toString()).isEmpty();
    }

    @Override
    public void onClick(View v) {
        try {
            Map<String, String> intentParams = new HashMap<>();
            switch (v.getId()) {
                case R.id.lobby_create_training_btn:
                    //go to edit activity.
                    //navigateToActivity(this, TrainingsListActivity.class, false, intentParams);
                    break;

                case R.id.lobby_public_trainings_btn:
                    intentParams.put(_getString(R.string.training_list_title), _getString(R.string.public_training_list_title));
                    intentParams.put(_getString(R.string.training_list_public_mode), "true");
                    navigateToActivity(this, TrainingsListActivity.class, false, intentParams);
                    break;

                case R.id.lobby_my_profile_settings_btn:
                    //navigateToActivity(this, TrainingsListActivity.class, false, null);
                    break;

                case R.id.lobby_my_trainings_btn:
                    intentParams.put(_getString(R.string.training_list_title), _getString(R.string.my_training_list_title));
                    intentParams.put(AppConstsEnum.manageTrainingPermission.toString(), "true");
                    intentParams.put(_getString(R.string.training_list_my_trainings_mode), "true");
                    navigateToActivity(this, TrainingsListActivity.class, false, intentParams);
                    break;

                case R.id.lobby_start_training_btn:
                    //disables in phase 1
                    break;

                case R.id.lobby_my_progress_btn:
                    //disables in phase 1
                    break;

            }
        } catch (Exception e) {
            CMNLogHelper.logError("LobbyActivity", e.getMessage());
        }
    }
}
