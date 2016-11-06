package zina_eliran.app.API.Listeners;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 11/2/2016.
 */

public class OnTrainingChangeListener implements ChildEventListener {
    private FireBaseHandler fireBaseHandler;

    public OnTrainingChangeListener(FireBaseHandler fireBaseHandler){
        this.fireBaseHandler = fireBaseHandler;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        BETrainingStatusEnum status = dataSnapshot.getValue(BETrainingStatusEnum.class);
        if (status == BETrainingStatusEnum.cancelled){
            //update user if he registered to this training and notification flag in on
            CMNLogHelper.logError("TrainingSTATUSchangedTO",BETrainingStatusEnum.cancelled.toString());
            forwardResponse(DALActionTypeEnum.trainingCancelled);
        }

        else if (status == BETrainingStatusEnum.full){
            //update training owner
            CMNLogHelper.logError("TrainingSTATUSchangedTO",BETrainingStatusEnum.full.toString() );
            forwardResponse(DALActionTypeEnum.trainingFull);

        }

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    public void forwardResponse(DALActionTypeEnum action){
        BEResponse res = new BEResponse();
        res.setActionType(action);
        res.setStatus(BEResponseStatusEnum.success);

        if (fireBaseHandler != null)
            fireBaseHandler.onActionCallback(res);
    }
}
