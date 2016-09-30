package zina_eliran.app.BusinessEntities;

import android.util.Log;

public class CMNLogHelper {

    public static boolean logTraffic = true;

    public static void logError(String tagName, String message){
        if(logTraffic)
            Log.e(tagName, message);
    }
}
