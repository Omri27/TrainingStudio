package zina_eliran.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;



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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //in debug mode: set to true, on release - set to false!
        CMNLogHelper.logTraffic = true;
        setContentView(R.layout.activity_register);
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
                    //validate email
                    BEUser user = new BEUser();
                    user.setName(nameEditText.getText().toString());
                    user.setEmail(emailEditText.getText().toString());

                    //register user into db. a user update call back will fire from BaseActivity
                    sApi.registerUser(user);

                   /* //validate registration & write to shared preferences
                    if (registerResult != null && registerResult.getStatus() == BEResponseStatusEnum.success) {
                        //user = (BEUser) registerResult.getEntity();
                        writeToSharedPreferences(AppConstsEnum.userId.toString(), user.getId().toString());

                        Toast.makeText(this, _getString(R.string.registration_success_message), Toast.LENGTH_LONG).show();

                        //set layout
                        setLayoutMode(true);
                    } else {
                        Toast.makeText(this, _getString(R.string.registration_error_message), Toast.LENGTH_LONG).show();
                    }
*/
                    break;

                case R.id.register_verify_btn:
                    if (sApi.getAppUser() != null &&
                            verificationCodeEditText.getText().toString().equals(sApi.getAppUser().getVerificationCode())) {

                        //BEResponse verificationResult = ServerAPI.registerUser(user);

                      /*  //validate verification & write to shared preferences
                        if (verificationResult != null && verificationResult.getStatus() == BEResponseStatusEnum.success) {
                            user = (BEUser) verificationResult.getEntity();
                            writeToSharedPreferences(AppConstsEnum.userVerificationCode.toString(), user.getVerificationCode());
                            writeToSharedPreferences(AppConstsEnum.userVerificationPermission.toString(), "true");

                            Toast.makeText(this, String.format(_getString(R.string.verification_success_message), user.getName() ), Toast.LENGTH_LONG).show();

                            //navigate to lobby when user verified his account
                            navigateToActivity(this, LobbyActivity.class, true, null);
                        }*/

                    } else {
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


            if(isRegisterCallback){
                writeToSharedPreferences(_getString(R.string.user_id), _user.getId().toString());
                writeToSharedPreferences(_getString(R.string.user_verification_code), _user.getVerificationCode());
                writeToSharedPreferences(_getString(R.string.user_registration_permission),"true");
                Toast.makeText(_getAppContext(), _getString(R.string.registration_success_message), Toast.LENGTH_LONG).show();

                //set layout
            }
            else {
                //verification callback
                writeToSharedPreferences(_getString(R.string.user_verification_permission), "true");
            }

        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
    }


}
