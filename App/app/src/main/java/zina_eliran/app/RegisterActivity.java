package zina_eliran.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import zina_eliran.app.API.ServerAPI;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;


//the first activity, unless the user already registered & verified.
public class RegisterActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler {

    //region ACTIVITY MEMBERS

    LinearLayout registrationLayout;
    LinearLayout verificationLayout;
    EditText nameEditText;
    EditText emailEditText;
    EditText verificationCodeEditText;
    Button registerBtn;
    Button verifyBtn;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //in debug mode: set to true, on release - set to false!
        CMNLogHelper.logTraffic = true;
        setContentView(R.layout.activity_register);

        //prevent open keyboard automatically
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        onCreateUI();

    }

    private void onCreateUI() {
        try {
            boolean isRegistered = isRegistered();

            //bind ui
            registrationLayout = (LinearLayout) findViewById(R.id.register_register_layout);
            verificationLayout = (LinearLayout) findViewById(R.id.register_verification_layout);
            nameEditText = (EditText) findViewById(R.id.register_name_et);
            emailEditText = (EditText) findViewById(R.id.register_email_et);
            verificationCodeEditText = (EditText) findViewById(R.id.register_verification_code_et);
            registerBtn = (Button) findViewById(R.id.register_register_btn);
            verifyBtn = (Button) findViewById(R.id.register_verify_btn);
            pBar = (ProgressBar) findViewById(R.id.register_pbar);
            pBar.setVisibility(View.INVISIBLE);
            pBar.bringToFront();
            registrationLayout.invalidate();
            verificationLayout.invalidate();
            //set ui layout mode
            setLayoutMode(isRegistered);

            registerBtn.setOnClickListener(this);
            verifyBtn.setOnClickListener(this);

        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }

    public void setLayoutMode(boolean isRegistered) {
        if (isRegistered) {
            registrationLayout.setVisibility(View.GONE);
            verificationLayout.setVisibility(View.VISIBLE);
        } else {
            registrationLayout.setVisibility(View.VISIBLE);
            verificationLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {

                case R.id.register_register_btn:

                    registerBtn.setEnabled(false);
                    //set spinner on
                    //TODO Eiran - add async task here
                    pBar.setVisibility(View.VISIBLE);

                    sApi.setActionResponse(null);

                    //register user into db. a user update view
                    BEUser user = new BEUser();
                    user.setName(nameEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());

                    sApi.registerUser(user, this);

                    break;

                case R.id.register_verify_btn:
                    verifyBtn.setEnabled(false);
                    //set spinner on
                    pBar.setVisibility(View.VISIBLE);

                    if (sApi.getAppUser() != null &&
                            verificationCodeEditText.getText().toString().equals(sApi.getAppUser().getVerificationCode())) {


                            writeToSharedPreferences(_getString(R.string.user_verification_code), sApi.getAppUser().getVerificationCode());
                            writeToSharedPreferences(_getString(R.string.user_verification_permission), "true");

                            Toast.makeText(this, String.format(_getString(R.string.verification_success_message), sApi.getAppUser().getName() ), Toast.LENGTH_LONG).show();

                            //navigate to lobby when user verified his account
                            navigateToActivity(this, LobbyActivity.class, true, null);

                    } else {
                        pBar.setVisibility(View.GONE);
                        verifyBtn.setEnabled(true);
                        Toast.makeText(this, _getString(R.string.verification_error_message), Toast.LENGTH_LONG).show();
                    }
                    break;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }

    public void updateUser(BEUser _user, boolean isRegisterCallback) {
        try {


            if (isRegisterCallback) {
                writeToSharedPreferences(_getString(R.string.user_id), _user.getId().toString());
                writeToSharedPreferences(_getString(R.string.user_verification_code), _user.getVerificationCode());
                writeToSharedPreferences(_getString(R.string.user_registration_permission), "true");
                Toast.makeText(_getAppContext(), _getString(R.string.registration_success_message), Toast.LENGTH_LONG).show();

                //set layout
            } else {
                //verification callback
                writeToSharedPreferences(_getString(R.string.user_verification_permission), "true");
            }

        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
    }

    @Override
    public void onActionCallback(BEResponse response) {
        //handle async response.
        if(response!=null && response.getStatus() == BEResponseStatusEnum.success){
            if(response.getActionType() == DALActionTypeEnum.registerUser){

                sApi.setAppUser((BEUser) response.getEntity().get(0));
                writeToSharedPreferences(_getString(R.string.user_id), sApi.getAppUser().getId().toString());
                writeToSharedPreferences(_getString(R.string.user_verification_code), sApi.getAppUser().getVerificationCode());
                writeToSharedPreferences(_getString(R.string.user_registration_permission), "true");
                Toast.makeText(_getAppContext(), _getString(R.string.registration_success_message), Toast.LENGTH_LONG).show();

                //set spinner off
                pBar.setVisibility(View.GONE);

                //set layout
                setLayoutMode(true);

            }
        }
        else {
            //handle error

            registerBtn.setEnabled(true);

            //set spinner off
            pBar.setVisibility(View.GONE);

            Toast.makeText(_getAppContext(), _getString(R.string.registration_error_message), Toast.LENGTH_LONG).show();


        }
    }


}
