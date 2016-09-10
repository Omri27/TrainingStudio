package zina_eliran.app;

import android.os.Bundle;

public class LobbyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lobby);

        //navigate to Lobby if the user is verified
        if (isVerified()) {
            onCreateUI();
        } else {
            navigateToActivity(this, RegisterActivity.class, false);
        }

    }

    private void onCreateUI() {
        try {

            //bind ui

            //set ui status


        } catch (Exception e) {
            CMNLogHelper.logError("LobbyActivity", e.getMessage());
        }
    }

    public boolean isVerified() {
        return !readFromSharedPreferences("userVerification").isEmpty();
    }

}
