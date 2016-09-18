package zina_eliran.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import zina_eliran.app.BusinessEntities.CMNLogHelper;

public class LobbyActivity extends BaseActivity implements View.OnClickListener {

    Button createTrainingBtn;
    Button publicTrainingsBtn;
    Button myProfileSettingsBtn;
    Button myTrainings;
    Button myProgressBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        //navigate to Lobby if the user is verified

        onCreateUI();

       /* if (!isVerified()) {
            onCreateUI();
        } else {
            navigateToActivity(this, RegisterActivity.class, false, null);
        }*/

    }

    private void onCreateUI() {
        try {

            //bind ui
            createTrainingBtn = (Button)findViewById(R.id.lobby_create_training_btn);
            publicTrainingsBtn = (Button)findViewById(R.id.lobby_public_trainings_btn);
            myProfileSettingsBtn = (Button)findViewById(R.id.lobby_my_profile_settings_btn);
            myTrainings = (Button)findViewById(R.id.lobby_my_trainings_btn);
            myProgressBtn = (Button)findViewById(R.id.lobby_my_progress_btn);

            createTrainingBtn.setOnClickListener(this);
            publicTrainingsBtn.setOnClickListener(this);
            myProfileSettingsBtn.setOnClickListener(this);
            myTrainings.setOnClickListener(this);
            myProgressBtn.setOnClickListener(this);

        } catch (Exception e) {
            CMNLogHelper.logError("LobbyActivity", e.getMessage());
        }
    }

    public boolean isVerified() {
        return !readFromSharedPreferences("userVerification").isEmpty();
    }

    @Override
    public void onClick(View v) {
        try {
            Map<String, String>  intentParams = new HashMap<>();
            switch (v.getId()) {
                case R.id.lobby_create_training_btn:
                    intentParams.put("hasCreateButton","true");
                    navigateToActivity(this, TrainingsListActivity.class, false, intentParams);
                    break;

                case R.id.lobby_public_trainings_btn:
                    intentParams.put("isCreateButton","false");
                    navigateToActivity(this, TrainingsListActivity.class, false, intentParams);
                    break;

                case R.id.lobby_my_profile_settings_btn:
                    navigateToActivity(this, TrainingsListActivity.class, false, null);
                    break;

                case R.id.lobby_my_trainings_btn:
                    intentParams.put("isCreateButton","false");
                    navigateToActivity(this, TrainingsListActivity.class, false, intentParams);
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
