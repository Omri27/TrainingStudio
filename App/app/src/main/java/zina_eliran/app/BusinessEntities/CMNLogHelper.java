package zina_eliran.app.BusinessEntities;

import android.util.Log;

public class CMNLogHelper {

    static boolean logTraffic = false;

    public static void logError(String tagName, String message){
        if(logTraffic)
            Log.e(tagName, message);
    }
}
