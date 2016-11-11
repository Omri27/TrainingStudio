package zina_eliran.app.API.Listeners;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;

import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 11/8/2016.
 */

public class AllTrainingsListener implements ChildEventListener{
    private String userID;
    private FireBaseHandler fireBaseHandler;

    public AllTrainingsListener(String userID, FireBaseHandler fireBaseHandler){
        this.fireBaseHandler = fireBaseHandler;
        this.userID = userID;
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        try{
            BETraining training = dataSnapshot.getValue(BETraining.class);
            if (training.getCreatorId().equals(userID)){
                forwardResponse(DALActionTypeEnum.iCreatedTraining,training);
                //CMNLogHelper.logError("AllTrainingsListener", DALActionTypeEnum.iCreatedTraining.toString());
            }
            else if (training.getPatricipatedUserIds()!= null && training.getPatricipatedUserIds().contains(userID)){
                forwardResponse(DALActionTypeEnum.iJoinedToTraining,training);
                //CMNLogHelper.logError("AllTrainingsListener", DALActionTypeEnum.iJoinedToTraining.toString());
            }
        }
        catch(Exception e){
            CMNLogHelper.logError("TrainingAddListener", e.getMessage());
        }

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void forwardResponse(DALActionTypeEnum action, BETraining training){
        if (fireBaseHandler != null){
            BEResponse response = new BEResponse();
            response.setStatus(BEResponseStatusEnum.success);
            response.setActionType(action);
            ArrayList<BEBaseEntity> entities = new ArrayList<>();
            entities.add(training);
            response.setEntities(entities);
        }
    }
}
