package zina_eliran.app.API;

import com.firebase.client.Firebase;

import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;

/**
 * Created by Zina K on 9/10/2016.
 */
public class DAL {

    private static Firebase rootRef = new Firebase("https://trainingstudiofb.firebaseio.com/web/data");
//    Firebase ref = new Firebase("https://docs-examples.firebaseio.com/android/saving-data/fireblog");

    public static void registerUser(BEUser user) {
        try{
            Firebase userRef = rootRef.child("users").child(user.getId().toString());
            userRef.setValue(user);
        } catch (Exception e) {

            CMNLogHelper.logError("DAL", e.getMessage());
        }


    }
}
