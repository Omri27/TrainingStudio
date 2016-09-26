package zina_eliran.app.API.Listeners;

import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import zina_eliran.app.API.DAL;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 9/23/2016.
 */

public class OnSetValueCompleteListener implements Firebase.CompletionListener{
    private FireBaseHandler fbHandler;
    private ArrayList<BEBaseEntity> entities;


    public OnSetValueCompleteListener(){}

    public  OnSetValueCompleteListener(FireBaseHandler fbHandler, ArrayList<BEBaseEntity> entities){
        this.fbHandler = fbHandler;
        this.entities = entities;
    }


    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        BEResponse response = new BEResponse();
        response.setActionType(DALActionTypeEnum.registerUser);
        response.setEntityType(BETypesEnum.user);

        if (firebaseError != null) {
            response.setStatus(BEResponseStatusEnum.error);
            response.setMessage(firebaseError.getMessage());
        } else {
            response.setStatus(BEResponseStatusEnum.success);
            response.setEntity(entities);
            Log.e("OnComplete", firebase.toString());
        }

        fbHandler.onActionCallback(response);
//        DAL.setActionResponse(response);
    }
//
//    @Override
//    public void onDataChange(DataSnapshot dataSnapshot) {
//
//    }
//
//    @Override
//    public void onCancelled(DatabaseError databaseError) {
//
//    }
//
//
//    public void
}
