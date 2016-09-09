package zina_eliran.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


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

        //set the key edit text if the user is already registered
        if (isRegistered()) {
            onCreateUI(true);
        } else {
            onCreateUI(false);
        }

    }

    private void navigateToLobby() {
        try {
            navigateToActivity(this, LobbyActivity.class);
        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
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

    public void setLayoutMode(boolean isRegistered){
        if(isRegistered){
            registrationLayout.setVisibility(View.GONE);
            verificationLayout.setVisibility(View.VISIBLE);
        }
        else {
            registrationLayout.setVisibility(View.VISIBLE);
            verificationLayout.setVisibility(View.GONE);
        }
    }

    public boolean isVerified() {
        return !readFromSharedPreferences("isVerified").isEmpty();
    }

    public boolean isRegistered() {
        return !readFromSharedPreferences("isRegistered").isEmpty();
    }

    @Override
    public void onClick(View v) {
        try {

            switch (v.getId()) {
                case R.id.register_register_btn:
                    //validate email
                    BEUser user = new BEUser(nameEditText.getText().toString(), emailEditText.getText().toString());
                    BEUser result = (BEUser)BL.registerUser(user);

                    ////validate registration & write to shared preferences

                    //set layout
                    setLayoutMode(true);
                    break;
                case R.id.register_verify_btn:
                    break;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("RegisterActivity", e.getMessage());
        }
    }
}
