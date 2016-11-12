package zina_eliran.app;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView resendEmailTv;
    Button registerBtn;
    Button verifyBtn;
    ProgressBar pBar;
    String email;
    String name;
    String verificationCode;

    BEUser user = new BEUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //in debug mode: set to true, on release - set to false!
            CMNLogHelper.logTraffic = true;
            setContentView(R.layout.activity_register);

            //prevent open keyboard automatically
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            onCreateUI();
        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }

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
            resendEmailTv = (TextView) findViewById(R.id.register_resendEmail_tv);
            resendEmailTv.setPaintFlags(resendEmailTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            pBar = (ProgressBar) findViewById(R.id.register_pbar);
            pBar.setVisibility(View.INVISIBLE);
            pBar.bringToFront();
            registrationLayout.invalidate();
            verificationLayout.invalidate();
            //set ui layout mode
            setLayoutMode(isRegistered);

            registerBtn.setOnClickListener(this);
            verifyBtn.setOnClickListener(this);
            resendEmailTv.setOnClickListener(this);

            // init registration data
            email = readFromSharedPreferences(_getString(R.string.user_email));
            name = readFromSharedPreferences(_getString(R.string.user_name));
            verificationCode = readFromSharedPreferences(_getString(R.string.user_verification_code));

        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }

    public void setLayoutMode(boolean isRegistered) {
        try{
        if (isRegistered) {
            registrationLayout.setVisibility(View.GONE);
            verificationLayout.setVisibility(View.VISIBLE);
        } else {
            registrationLayout.setVisibility(View.VISIBLE);
            verificationLayout.setVisibility(View.GONE);
        }
        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {

                case R.id.register_register_btn:

                    registerBtn.setEnabled(false);
                    //set spinner on
                    pBar.setVisibility(View.VISIBLE);
                    sApi.setActionResponse(null);

                    //register user into db. a user update view
                    user.setName(nameEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());
                    user.setPrivateProfile(true);

                    sApi.registerUser(user, this);

                    break;

                case R.id.register_verify_btn:
                    verifyBtn.setEnabled(false);
                    //set spinner on
                    pBar.setVisibility(View.VISIBLE);

                    if (verificationCodeEditText.getText().toString().equals(verificationCode)) {

                        writeToSharedPreferences(_getString(R.string.user_verification_code), verificationCode);
                        writeToSharedPreferences(_getString(R.string.user_verification_permission), "true");

                        Toast.makeText(this, String.format(_getString(R.string.verification_success_message), name), Toast.LENGTH_LONG).show();

                        //navigate to lobby when user verified his account
                        navigateToActivity(this, LobbyActivity.class, true, null);

                    } else {
                        pBar.setVisibility(View.GONE);
                        verifyBtn.setEnabled(true);
                        Toast.makeText(this, _getString(R.string.verification_error_message), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.register_resendEmail_tv:
                    //Send verification code by email again
                    if (readFromSharedPreferences(_getString(R.string.user_id)) != "") {

                        resendEmailTv.setVisibility(View.INVISIBLE);
                        pBar.setVisibility(View.VISIBLE);
                        sApi.resendUserRegistrationEmail(email, name, verificationCode);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        Toast.makeText(_getAppContext(), _getString(R.string.registration_resend_email_message), Toast.LENGTH_LONG).show();
                                        resendEmailTv.setVisibility(View.VISIBLE);
                                        pBar.setVisibility(View.GONE);
                                    }
                                }, 10000);
                    }
                    //something went wrong with the registration - init the app
                    else {
                        Toast.makeText(this, _getString(R.string.registration_error_message), Toast.LENGTH_LONG).show();

                        //refresh the current activity
                        clearSharedPreferences();
                        finish();
                        startActivity(getIntent());
                        return;
                    }

                    break;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }


    @Override
    public void onActionCallback(BEResponse response) {
        //handle async response.
        if (response != null && response.getStatus() == BEResponseStatusEnum.success) {
            if (response.getActionType() == DALActionTypeEnum.registerUser) {

                sApi.setAppUser((BEUser) response.getEntities().get(0));

                name = sApi.getAppUser().getName();
                email = sApi.getAppUser().getEmail();
                verificationCode = sApi.getAppUser().getVerificationCode();
                writeToSharedPreferences(_getString(R.string.user_id), sApi.getAppUser().getId().toString());
                writeToSharedPreferences(_getString(R.string.user_name), sApi.getAppUser().getName());
                writeToSharedPreferences(_getString(R.string.user_email), sApi.getAppUser().getEmail());
                writeToSharedPreferences(_getString(R.string.user_verification_code), sApi.getAppUser().getVerificationCode());
                writeToSharedPreferences(_getString(R.string.user_registration_permission), "true");
                Toast.makeText(_getAppContext(), _getString(R.string.registration_success_message), Toast.LENGTH_LONG).show();

                //set spinner off
                pBar.setVisibility(View.GONE);

                //set layout
                setLayoutMode(true);

            }
        } else {
            //handle error
            registerBtn.setEnabled(true);

            //set spinner off
            pBar.setVisibility(View.GONE);

            Toast.makeText(_getAppContext(), _getString(R.string.registration_error_message), Toast.LENGTH_LONG).show();

        }
    }


}
