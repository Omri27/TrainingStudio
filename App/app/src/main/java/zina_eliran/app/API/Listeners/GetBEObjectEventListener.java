package zina_eliran.app.API.Listeners;


import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import zina_eliran.app.API.DAL;
import zina_eliran.app.BaseActivity;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;

/**
 * Created by Zina K on 9/22/2016.
 */

public class GetBEObjectEventListener implements ValueEventListener {
    private ReadDataTypeEnum readDataType;

//    public GetBEObjectEventListener(){
//
//    }

    //Required expected Object Type to read
    public GetBEObjectEventListener(ReadDataTypeEnum readType){
        this.readDataType = readType;
    }


    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BEResponse res = new BEResponse();
        BEBaseEntity object = new BEBaseEntity();
        ArrayList<BEBaseEntity> arr = new ArrayList<>();

        if (readDataType == ReadDataTypeEnum.training)
            object = dataSnapshot.getValue(BETraining.class);
        else if (readDataType == ReadDataTypeEnum.user)
            object = dataSnapshot.getValue(BEUser.class);
        else {
            res.setStatus(BEResponseStatusEnum.error);
            res.setMessage("Unknown data type");
        }

        arr.add(object);
        res.setEntity(arr);

        //Use for tests - Zina
        Log.e("DAL", "ObjectListener");
        if( object instanceof BEUser){
            Log.e("GetUser", ((BEUser)object).getEmail());
        }
        else if (object instanceof BETraining){
            Log.e("GetTraining", ((BETraining)object).getName());
        }

        DAL.setActionResult(res);



    }
}
