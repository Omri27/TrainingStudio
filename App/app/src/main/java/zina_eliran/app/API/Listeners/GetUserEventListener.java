package zina_eliran.app.API.Listeners;


import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import zina_eliran.app.BaseActivity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;

/**
 * Created by Zina K on 9/22/2016.
 */

public class GetUserEventListener implements ValueEventListener {


    public GetUserEventListener(){

    }


    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BEUser user = dataSnapshot.getValue(BEUser.class);
        BEResponse res = new BEResponse();
        res.setEntity(user);
        res.setStatus(BEResponseStatusEnum.success);
        Log.e("DAL", "Listener Zina");
        Log.e("DAL", user.getName());
        Log.e("DAL", ((BEUser)res.getEntity()).getEmail());
        BaseActivity.updateUser(res);
    }
}
