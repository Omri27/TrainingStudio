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
    private String trainingID;

    public OnTrainingChangeListener(FireBaseHandler fireBaseHandler,String trainingID){
        this.fireBaseHandler = fireBaseHandler;
        this.trainingID = trainingID;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try {

            if (dataSnapshot.getKey().toString().equals("currentNumberOfParticipants")){
                Integer currentNumberOfParticipants = dataSnapshot.getValue(Integer.class);
                forwardResponse(DALActionTypeEnum.numberOfParticipantsChanged, currentNumberOfParticipants);
            }
            else if (dataSnapshot.getKey().toString().equals("status")){
                BETrainingStatusEnum status = dataSnapshot.getValue(BETrainingStatusEnum.class);
                if (status == BETrainingStatusEnum.cancelled) {
                    //update user if he registered to this training and notification flag in on
                    forwardResponse(DALActionTypeEnum.trainingCancelled, -1);
                } else if (status == BETrainingStatusEnum.full) {
                    //update training owner
                    forwardResponse(DALActionTypeEnum.trainingFull, -1);
                }
            }
        }
        catch (Exception e){
            CMNLogHelper.logError("TrainingListener", e.getMessage());
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

    public void forwardResponse(DALActionTypeEnum action, Integer number){
        try{
            BEResponse res = new BEResponse();
            res.setActionType(action);
            res.setStatus(BEResponseStatusEnum.success);
            //Put training id in response
            res.setMessage(trainingID);

            //On change of joined user number should be 0 or more
            if (number >= 0) {
                //send training id and new number of joined user in message field with ; delimiter
                res.setMessage(trainingID + ";" + number.toString());
            }
            CMNLogHelper.logError("TrainingListenerMessage", res.getMessage());

            if (fireBaseHandler != null)
                fireBaseHandler.onActionCallback(res);
        }
        catch (Exception e){
            CMNLogHelper.logError("TrainingListenerFailedToForward", e.getMessage());
        }

    }
}
