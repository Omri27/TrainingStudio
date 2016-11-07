package zina_eliran.app.API.Listeners;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;

/**
 * Created by Zina K on 11/5/2016.
 */

public class OnUserChangeListener implements ChildEventListener {
    private BEUser user;


    public OnUserChangeListener(BEUser user){
        this.user = user;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String str = dataSnapshot.getKey();
        CMNLogHelper.logError("OnChildChange", str);
        CMNLogHelper.logError("What is s??", s);
        try {
            BEUser.class.getDeclaredField(str).set(user, dataSnapshot.getValue());
            CMNLogHelper.logError("modified, printing user:", user.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        BETrainingStatusEnum status = dataSnapshot.getValue(BETrainingStatusEnum.class);
//        if (status == BETrainingStatusEnum.cancelled){
//            //update user if he registered to this training and notification flag in on
//            CMNLogHelper.logError("TrainingSTATUSchangedTO",BETrainingStatusEnum.cancelled.toString());
//            forwardResponse(DALActionTypeEnum.trainingCancelled);
//        }
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
