package zina_eliran.app.API.Listeners;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;

/**
 * Created by Zina K on 11/2/2016.
 */

public class onTrainingChangeListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        BETrainingStatusEnum status = dataSnapshot.getValue(BETrainingStatusEnum.class);
        if (status == BETrainingStatusEnum.cancelled){
            //update all users
            CMNLogHelper.logError("TrainingSTATUSchangedTO",BETrainingStatusEnum.cancelled.toString() );
        }

        else if (status == BETrainingStatusEnum.full){
            //update training owner
            CMNLogHelper.logError("TrainingSTATUSchangedTO",BETrainingStatusEnum.full.toString() );

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
