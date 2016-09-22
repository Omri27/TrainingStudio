package zina_eliran.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;

import com.firebase.client.Firebase;


import java.util.Map;

import zina_eliran.app.BusinessEntities.CMNLogHelper;

public class BaseActivity extends AppCompatActivity {

    //create shared preferences scope for all activities
    final String appPreferences = "trainingStudioPreferences";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    static Context appContext;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = this;
        Firebase.setAndroidContext(this);

        //create the Shared Preferences read/write objects
        preferences = getSharedPreferences(appPreferences, 0);
        editor = preferences.edit();
    }

    //region SHARED METHODS

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

    public static void navigateToActivity(Activity current, java.lang.Class<?> nextClass, boolean isFinishCurrentActivity, Map<String, String> intentParams) {
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

    //endregion

    public static Context _getAppContext() {
        return appContext;
    }

    public static String getIntentParam(Intent intent, String key) {
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                return extras.getString(key);
            }
        }
        return null;
    }

    public static String _getString(int key) {
        return appContext.getString(key);
    }

}
