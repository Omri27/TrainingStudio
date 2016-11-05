package zina_eliran.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.View;

import com.firebase.client.Firebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import zina_eliran.app.API.DAL;
import zina_eliran.app.API.ServerAPI;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.CMNLogHelper;


public class BaseActivity extends AppCompatActivity {

    //create shared preferences scope for all activities
    final String appPreferences = "trainingStudioPreferences";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ServerAPI sApi;
    Context appContext;
    Intent intent;
    DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
    DateFormat timeFormatter = new SimpleDateFormat("kk:mm"); //kk = 1-24

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sApi = ServerAPI.getInstance();
        appContext = this;
        Firebase.setAndroidContext(this);

        //create the Shared Preferences read/write objects
        preferences = getSharedPreferences(appPreferences, 0);
        editor = preferences.edit();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //"force" LTR alignment
        String languageToLoad  = "en"; // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        //run this once when you want to "init" the registration process
        //clearSharedPreferences();
    }


    public String readFromSharedPreferences(String key) {
        String returnedValue = "";
        try {
            String value = preferences.getString(key, "");
            if (!value.equalsIgnoreCase("")) {
                returnedValue = value;
            }
        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
        return returnedValue;
    }

    public boolean writeToSharedPreferences(String key, String value) {
        try {
            editor.putString(key, value);
            editor.commit();
            return true;
        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
        return false;
    }

    public boolean deleteFromSharedPreferences(String key) {
        try {
            preferences.edit().remove(key).commit();
            return true;
        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
        return false;
    }

    public boolean clearSharedPreferences() {
        try {
            preferences.edit().clear().commit();
            return true;
        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
        return false;
    }

    public void navigateToActivity(Activity current, java.lang.Class<?> nextClass, boolean isFinishCurrentActivity, Map<String, String> intentParams) {
        try {
            Intent openActivity = new Intent(current, nextClass);
            if (isFinishCurrentActivity) {
                openActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            //add intent params
            if (intentParams != null) {
                for (String k : intentParams.keySet()) {
                    openActivity.putExtra(k, intentParams.get(k));
                }
            }
            current.startActivity(openActivity);

            if (isFinishCurrentActivity) {
                current.finish();
            }

        } catch (Exception e) {
            CMNLogHelper.logError("BaseActivity", e.getMessage());
        }
    }

    public boolean isRegistered() {
        return !readFromSharedPreferences(_getString(R.string.user_registration_permission)).isEmpty();
    }

    public boolean isVerified() {
        return !readFromSharedPreferences(_getString(R.string.user_verification_permission)).isEmpty();
    }

    public Context _getAppContext() {
        return appContext;
    }

    public String getIntentParam(Intent intent, String key) {
        String result = null;
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                result = extras.getString(key);
            }
        }
        return result != null ? result : "";
    }

    public String _getString(int key) {
        return appContext.getString(key);
    }

}
