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
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 9/22/2016.
 */

public class GetBEObjectEventListener implements ValueEventListener {
    private ReadDataTypeEnum readDataType;
    private BEBaseEntity object;
    private boolean shouldForwardResponse; //if flag is false, no response sent to setActionResponse
    private FireBaseHandler fbHandler;


    public BEBaseEntity getObject() {
        return object;
    }

    public GetBEObjectEventListener(ReadDataTypeEnum readType, boolean shouldForwardResponse){
        this.readDataType = readType;
        this.object = new BEBaseEntity();
        this.shouldForwardResponse = shouldForwardResponse;
    }

    //Required expected Object Type to read
    public GetBEObjectEventListener(ReadDataTypeEnum readType, boolean shouldForwardResponse, FireBaseHandler fbHandler){
        this.readDataType = readType;
        this.object = new BEBaseEntity();
        this.shouldForwardResponse = shouldForwardResponse;
        this.fbHandler = fbHandler;
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        if (shouldForwardResponse){
            forwardResponse(null, BEResponseStatusEnum.error, firebaseError.toString());
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        BEResponseStatusEnum status = BEResponseStatusEnum.success;
        String errorMessage = null;

        if (readDataType == ReadDataTypeEnum.training)
            object = dataSnapshot.getValue(BETraining.class);
        else if (readDataType == ReadDataTypeEnum.user)
            object = dataSnapshot.getValue(BEUser.class);
        else {
            status = BEResponseStatusEnum.error;
            errorMessage = "Unknown data type";
        }

        //Use for tests - Zina
        Log.e("DAL", "ObjectListener");
        if( object instanceof BEUser)
            CMNLogHelper.logError("GetUser", ((BEUser)object).getEmail());
        else if (object instanceof BETraining)
            CMNLogHelper.logError("GetTraining", ((BETraining)object).getName());

        if (shouldForwardResponse)
            forwardResponse(object, status, errorMessage);


    }

    private void forwardResponse(BEBaseEntity object, BEResponseStatusEnum isActionSucceeded, String errorMessage){
        BEResponse res = new BEResponse();
        if (object != null) {
            ArrayList<BEBaseEntity> arr = new ArrayList<>();
            arr.add(object);
        }
        res.setStatus(isActionSucceeded);
        if (errorMessage != null)
            res.setMessage(errorMessage);
        fbHandler.onActionCallback(res);
//        DAL.setActionResponse(res);
    }
}
