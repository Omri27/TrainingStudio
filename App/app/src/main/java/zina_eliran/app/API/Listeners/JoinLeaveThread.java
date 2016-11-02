package zina_eliran.app.API.Listeners;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;

import zina_eliran.app.API.DAL;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 11/2/2016.
 */

public class JoinLeaveThread implements FireBaseHandler {
    private  BETraining training;
    private  BEUser user;
    private  FireBaseHandler fb;
    private  DALActionTypeEnum action;

    public JoinLeaveThread(FireBaseHandler fb,DALActionTypeEnum action ){
        this.fb = fb;
        this.action = action;
    }



    @Override
    public void onActionCallback(BEResponse response) {
        if (response != null && response.getStatus() == BEResponseStatusEnum.success && response.getEntities() != null) {
            if (response.getEntityType() == BETypesEnum.Trainings)
                training = (BETraining) response.getEntities().get(0);
            else if (response.getEntityType() == BETypesEnum.Users)
                user = (BEUser) response.getEntities().get(0);
        }

        if (training != null && user != null){
            startJoinLeaveOperation(training,user);
        }
    }

    private void startJoinLeaveOperation(BETraining training, BEUser user) {
        BEResponseStatusEnum actionStatus = BEResponseStatusEnum.success;
        String message = "";
        if (action == DALActionTypeEnum.joinTraining && canJoin(training, user))
            joinOperation(training, user);
        else if (action == DALActionTypeEnum.leaveTraining && canLeave(training,user))
            leaveOperation(training,user);
        else {
            actionStatus = BEResponseStatusEnum.error;
            message = action.toString() + " failed, action cannot be performed";
        }


        //commit to DB
        Firebase trainingRef = DAL.getTrainingsRef().child(training.getId());
        trainingRef.setValue(training);
        Firebase userRef = DAL.getUsersRef().child(user.getId());
        userRef.setValue(user);

        //Add updated objects to array for response
        ArrayList<BEBaseEntity> e = new ArrayList<>();
        e.add(training);
        e.add(user);

        //Generate a response
        BEResponse res = new BEResponse();
        res.setEntityType(BETypesEnum.Trainings);
        res.setStatus(actionStatus);
        res.setActionType(action);
        res.setEntities(e);
        res.setMessage(message);

        //For test
        CMNLogHelper.logError("Response ", res.toString());

        if (fb != null)
            fb.onActionCallback(res);

        training = null;
        user = null;

    }

    private void leaveOperation(BETraining training, BEUser user) {
        //prepare training for commiting to DB: remove userID, update currentParticipantsNumber
        training.getPatricipatedUserIds().remove(user.getId());
        int currentNum = training.getCurrentNumberOfParticipants();
        currentNum--;
        training.setCurrentNumberOfParticipants(currentNum);

        //prepare user for commiting to DB: add trainingID
        if ( user.getMyTrainingIds() != null)
            user.getMyTrainingIds().remove(training.getId());
    }

    private void joinOperation(BETraining training, BEUser user) {
        //prepare training for commiting to DB: add userID, update currentParticipantsNumber
        //If reach maxNumOfParticipants, update trainingStatus to full
        training.getPatricipatedUserIds().add(user.getId());
        int currentNum = training.getCurrentNumberOfParticipants();
        currentNum++;
        training.setCurrentNumberOfParticipants(currentNum);
        if (training.getMaxNumberOfParticipants() == training.getCurrentNumberOfParticipants())
            training.setStatus(BETrainingStatusEnum.full);

        //prepare user for commiting to DB: add trainingID
        if (user.getMyTrainingIds()!= null)
            user.getMyTrainingIds().add(training.getId());
        else{
            user.setMyTrainingIds(new ArrayList<String>());
            user.getMyTrainingIds().add(training.getId());


        }
    }

    private boolean canJoin(BETraining training, BEUser user) {
        boolean canJoin = true;
        //check if training is valid for join
        if (training.getMaxNumberOfParticipants() == training.getCurrentNumberOfParticipants()
                || training.getStatus() == BETrainingStatusEnum.cancelled
                || training.getTrainingDateTimeCalender().after(new Date())
                || training.getPatricipatedUserIds().contains(user.getId())){
            canJoin = false;
        }

        //check if user can participate in this training
        else if (user.getMyTrainingIds() != null && user.getMyTrainingIds().contains(training.getId())){
            canJoin = false;
        }

        return canJoin;

    }

    private boolean canLeave(BETraining training, BEUser user){
        boolean canLeave = false;
        if (training.getPatricipatedUserIds() != null && training.getPatricipatedUserIds().contains(user.getId())
                && user.getMyTrainingIds() != null && user.getMyTrainingIds().contains(training.getId())){
            canLeave = true;
        }
        return canLeave;
    }


}
