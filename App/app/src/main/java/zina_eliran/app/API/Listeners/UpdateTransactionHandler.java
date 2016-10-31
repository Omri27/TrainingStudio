package zina_eliran.app.API.Listeners;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.ArrayList;

import zina_eliran.app.API.DAL;
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
 * Created by Zina K on 10/1/2016.
 */

public class UpdateTransactionHandler implements Transaction.Handler {
    BETraining originalTraining;
    BEUser originalUser;
    BETraining newTraining;
    BEUser newUser;
    FireBaseHandler fireBaseHandler;
    Transaction.Result transactionResult;
    ArrayList<BEBaseEntity> objects;
    DALActionTypeEnum action;
    BETypesEnum objectType;



    public UpdateTransactionHandler(BETypesEnum objectType, DALActionTypeEnum action, FireBaseHandler fireBaseHandler,
                                    BEUser user, BETraining training){
        this.objectType = objectType;
        this.action = action;
        this.fireBaseHandler = fireBaseHandler;
        this.newTraining = training;
        this.newUser = user;
        objects = new ArrayList<>();



    }


    @Override
    public Transaction.Result doTransaction(MutableData mutableData) {
        //Check id reference has data
        if(mutableData.getValue() != null){

            //Fetch training from DB and save as oldTraining
            originalTraining =  mutableData.getValue(BETraining.class);

            //Override flags fields and training status on each run
            overrideTrainingFields(mutableData);

            //Prepare IDlist and update in DB
            ArrayList<String> usersIds = mergeLists(originalTraining.getPatricipatedUserIds(), originalUser.getId());
            MutableData usersID = mutableData.child("patricipatedUserIds");
            usersID.setValue(usersIds);

            //Update num of participants in training
            MutableData numOfPartocipants = mutableData.child("currentNumberOfParticipants");
            numOfPartocipants.setValue(numOfPartocipants.getValue(Integer.class)+1);

            CMNLogHelper.logError("UpdateTransaction", "update participants succeed");
            return Transaction.success(mutableData);

        }
        else
            overrideTrainingFields(mutableData);
            CMNLogHelper.logError("UpdateTransaction", "update participants failed");
        return Transaction.abort();
    }

    @Override
    public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
        if (firebaseError != null){
            CMNLogHelper.logError("Update-transaction-failed", firebaseError.getMessage());
//            rollbackTransaction((MutableData)dataSnapshot);
            forwardResponse(BEResponseStatusEnum.error, firebaseError.getMessage());
        }

        else{
            Firebase userRef = DAL.getUsersRef().child(originalUser.getId());
            userRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    BEUser user = mutableData.getValue(BEUser.class);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });

        }

    }

    public void rollbackTransaction(MutableData data, Firebase userRef){
        data.setValue(originalTraining);
        userRef.setValue(originalUser);
    }


    public void forwardResponse(BEResponseStatusEnum status, String message){
        if (fireBaseHandler != null){
            BEResponse response = new BEResponse();
            response.setEntities(objects);
            response.setStatus(status);
            response.setMessage(message);
            response.setActionType(action);
            response.setEntityType(objectType);
            fireBaseHandler.onActionCallback(response);
        }
    }

    public void overrideTrainingFields(MutableData data){
        data.child("isJoinTrainingNotificationFlag").setValue(newTraining.isJoinTrainingNotificationFlag());
        data.child("isTrainingFullNotificationFlag").setValue(newTraining.isTrainingFullNotificationFlag());
        data.child("status").setValue(newTraining.getStatus());
    }


    public ArrayList<String> mergeLists(ArrayList<String> oldList, String userID) {
        if (action == DALActionTypeEnum.joinTraining)
            oldList.add(userID);
        else
            oldList.remove(userID);
        return oldList;
    }
}
