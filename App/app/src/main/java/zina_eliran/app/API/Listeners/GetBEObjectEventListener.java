package zina_eliran.app.API.Listeners;


import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import zina_eliran.app.API.DAL;
import zina_eliran.app.BaseActivity;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 9/22/2016.
 */

public class GetBEObjectEventListener implements ValueEventListener {
    private ArrayList<BEBaseEntity> objects;
    private BETypesEnum dataType;
//    private BEBaseEntity object;
    private FireBaseHandler fbHandler; //If null, do not try to callback
    private DALActionTypeEnum action;


    public ArrayList<BEBaseEntity> getObjects(){
        return objects;
    }


    public BEBaseEntity getObject() {
        return objects.get(0);
    }


    //Required expected Object Type to read
    public GetBEObjectEventListener(BETypesEnum dataType, FireBaseHandler fbHandler, DALActionTypeEnum action){
        this.dataType = dataType;
        this.objects = new ArrayList<>();
        this.fbHandler = fbHandler;
        this.action = action;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BEResponseStatusEnum status = BEResponseStatusEnum.success;
        String errorMessage = null;


        if (action == DALActionTypeEnum.getTraining || action == DALActionTypeEnum.getUser){
            if (dataType == BETypesEnum.Trainings)
                objects.add(dataSnapshot.getValue(BETraining.class));
            else if (dataType == BETypesEnum.Users)
                objects.add(dataSnapshot.getValue(BEUser.class));
            else {
                status = BEResponseStatusEnum.error;
                errorMessage = "Unknown data type";
            }

            //Use for tests - Zina
            Log.e("DAL", "ObjectListener");
            if( objects.get(0) instanceof BEUser)
                CMNLogHelper.logError("GetUserListener", "[[" + action.toString()  + "]]"  + " " + ((BEUser)objects.get(0)).toString());
            else if (objects.get(0) instanceof BETraining)
                CMNLogHelper.logError("GetTrainingListener", "[[" + action.toString() + "]]" + " " + ((BETraining)objects.get(0)).toString());
        }

        else if (action == DALActionTypeEnum.getAllTrainings){
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                BETraining t = postSnapshot.getValue(BETraining.class);
                objects.add(t);
            }

            //Use for tests - Zina
            for (int i = 0; i < objects.size(); i++)
                CMNLogHelper.logError("All trainings listener", objects.get(i).toString());
        }

        else {
            status = BEResponseStatusEnum.error;
            errorMessage = "Unknown action type";
        }

        if (fbHandler != null)
            forwardResponse(objects, status, errorMessage);


    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        if (fbHandler != null){
            forwardResponse(null, BEResponseStatusEnum.error, firebaseError.toString());
        }
    }


    private void forwardResponse(ArrayList<BEBaseEntity> objects, BEResponseStatusEnum isActionSucceeded, String errorMessage){
        BEResponse res = new BEResponse();
        res.setActionType(action);
        res.setEntityType(dataType);

        if (objects != null) {
            res.setEntities(objects);
            CMNLogHelper.logError("GetListener-forward"+ new Date() + " " + action.toString()+ " " + isActionSucceeded.toString() + " ", objects.toString());
        }
        res.setStatus(isActionSucceeded);
        if (errorMessage != null)
            res.setMessage(errorMessage);

        if (fbHandler != null)
            fbHandler.onActionCallback(res);
    }
}
