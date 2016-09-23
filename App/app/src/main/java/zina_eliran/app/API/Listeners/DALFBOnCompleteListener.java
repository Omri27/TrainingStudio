package zina_eliran.app.API.Listeners;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import zina_eliran.app.API.DAL;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;

/**
 * Created by Zina K on 9/23/2016.
 */

public class DALFBOnCompleteListener implements Firebase.CompletionListener{

    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        BEResponse response = new BEResponse();

        if (firebaseError != null) {
            response.setStatus(BEResponseStatusEnum.error);
            response.setMessage(firebaseError.getMessage());
        } else {
            response.setStatus(BEResponseStatusEnum.success);
            Log.e("OnComplete", firebase.toString());
        }
        DAL.setActionResult(response);
    }
}
