package zina_eliran.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import zina_eliran.app.API.ServerAPI;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.AppConstsEnum;


//the first activity, unless the user already registered & verified.
public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    //region ACTIVITY MEMBERS

    LinearLayout registrationLayout;
    LinearLayout verificationLayout;
    EditText nameEditText;
    EditText emailEditText;
    EditText verificationCodeEditText;
    Button registerBtn;
    Button verifyBtn;
    BEUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //in debug mode: set to true, on release - set to false!
        CMNLogHelper.logTraffic = true;
        setContentView(R.layout.activity_register);

        //set the key edit text if the user is already registered
        if (!isRegistered()) {
            /*user = getUser();
            if(user != null){
                onCreateUI(true);
            }
            else {
                //toast here

                onCreateUI(false);
            }
*/

            onCreateUI(true);
        } else {
            onCreateUI(false);
        }

    }

    private void onCreateUI(boolean isRegistered) {
        try {

            //bind ui
            registrationLayout = (LinearLayout) findViewById(R.id.register_register_layout);
            verificationLayout = (LinearLayout) findViewById(R.id.register_verification_layout);
            nameEditText = (EditText) findViewById(R.id.register_name_et);
            emailEditText = (EditText) findViewById(R.id.register_email_et);
            verificationCodeEditText = (EditText) findViewById(R.id.register_verification_code_et);
            registerBtn = (Button) findViewById(R.id.register_register_btn);
            verifyBtn = (Button) findViewById(R.id.register_verify_btn);

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

    public boolean isRegistered() {

        return !readFromSharedPreferences(AppConstsEnum.userRegistrationPermission.toString()).isEmpty();
    }

    public boolean isVerified() {
        return !readFromSharedPreferences(AppConstsEnum.userVerificationPermission.toString()).isEmpty();
    }


    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {
                case R.id.register_register_btn:
                    //validate email
                    user = new BEUser();
                    user.setName(nameEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());

                    BEResponse registerResult = ServerAPI.registerUser(user);

                    //validate registration & write to shared preferences
                    if (registerResult != null && registerResult.getStatus() == BEResponseStatusEnum.success) {
                        user = (BEUser) registerResult.getEntity();
                        writeToSharedPreferences(AppConstsEnum.userId.toString(), user.getId().toString());

                        Toast.makeText(this, _getString(R.string.registration_success_message), Toast.LENGTH_LONG).show();

                        //set layout
                        setLayoutMode(true);
                    } else {
                        Toast.makeText(this, _getString(R.string.registration_error_message), Toast.LENGTH_LONG).show();
                    }

                    break;

                case R.id.register_verify_btn:
                    if (user != null) {
                        user.setVerificationCode(verificationCodeEditText.getText().toString());

                        BEResponse verificationResult = ServerAPI.registerUser(user);

                        //validate verification & write to shared preferences
                        if (verificationResult != null && verificationResult.getStatus() == BEResponseStatusEnum.success) {
                            user = (BEUser) verificationResult.getEntity();
                            writeToSharedPreferences(AppConstsEnum.userVerificationCode.toString(), user.getVerificationCode());
                            writeToSharedPreferences(AppConstsEnum.userVerificationPermission.toString(), "true");

                            Toast.makeText(this, String.format(_getString(R.string.verification_success_message), user.getName() ), Toast.LENGTH_LONG).show();

                            //navigate to lobby when user verified his account
                            navigateToActivity(this, LobbyActivity.class, true, null);
                        }
                    } else {
                        Toast.makeText(this, _getString(R.string.verification_error_message), Toast.LENGTH_LONG).show();
                    }
                    break;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }

    public BEUser getUser() {
        BEResponse result = ServerAPI.getUser(readFromSharedPreferences(AppConstsEnum.userId.toString()));
        if (result.getStatus() == BEResponseStatusEnum.success) {
            return (BEUser) result.getEntity();
        }
        return null;
    }
}
