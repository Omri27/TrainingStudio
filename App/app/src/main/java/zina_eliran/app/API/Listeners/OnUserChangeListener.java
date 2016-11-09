package zina_eliran.app.API.Listeners;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 11/5/2016.
 */

public class OnUserChangeListener implements ChildEventListener {
    private BEUser user;
    private FireBaseHandler fireBaseHandler;


    public OnUserChangeListener(BEUser user, FireBaseHandler fireBaseHandler){
        this.user = user;
        this.fireBaseHandler = fireBaseHandler;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try{
            String changedValue = dataSnapshot.getKey();

                if (changedValue != "myTrainingIds"){
                    if (changedValue == "trainingRemainderNotification"){
                        BEUser.class.getDeclaredField(changedValue).set(user, dataSnapshot.getValue());
                        forwardResponse(DALActionTypeEnum.userRemainderChanged);
                    }
                    else if (changedValue.equals("trainingFullNotification")){
                        BEUser.class.getDeclaredField(changedValue).set(user, dataSnapshot.getValue());
                        forwardResponse(DALActionTypeEnum.userFullNotificationChanged);
                    }
                    else if (changedValue.equals("trainingCancelledNotification")){
                        BEUser.class.getDeclaredField(changedValue).set(user, dataSnapshot.getValue());
                        forwardResponse(DALActionTypeEnum.userCancelledNotificationChanged);
                    }
//                    BEUser.class.getDeclaredField(changedValue).set(user, dataSnapshot.getValue());
//                    CMNLogHelper.logError("modified, printing user:", user.toString());
                }
        }
        catch(Exception e){
            CMNLogHelper.logError("OnUserListenerFailed", e.getMessage());
        }

//        BETrainingStatusEnum status = dataSnapshot.getValue(BETrainingStatusEnum.class);
//        if (status == BETrainingStatusEnum.cancelled){
//            //update user if he registered to this training and notification flag in on
//            CMNLogHelper.logError("TrainingSTATUSchangedTO",BETrainingStatusEnum.cancelled.toString());
//            forwardResponse(DALActionTypeEnum.trainingCancelled);
//        }
    }

    private void forwardResponse(DALActionTypeEnum action) {
        if (fireBaseHandler != null){
            BEResponse response = new BEResponse();
            response.setStatus(BEResponseStatusEnum.success);
            response.setActionType(action);
            ArrayList<BEBaseEntity> entities = new ArrayList<>();
            entities.add(user);
            response.setEntities(entities);
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
}
