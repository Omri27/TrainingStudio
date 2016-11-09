package zina_eliran.app.API.Listeners;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 11/8/2016.
 */

public class UserReminderFlagListener implements ChildEventListener{
    private FireBaseHandler fireBaseHandler;

    public UserReminderFlagListener(FireBaseHandler fireBaseHandler){
        this.fireBaseHandler = fireBaseHandler;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        try{
            Boolean reminder;
            if (dataSnapshot.getKey().equals("isTrainingRemainderNotification")){
                reminder = (boolean) dataSnapshot.getValue();
                if (fireBaseHandler != null){
                    BEResponse res = new BEResponse();
                    res.setStatus(BEResponseStatusEnum.success);
                    res.setActionType(DALActionTypeEnum.userRemainderChanged);
                    res.setMessage(reminder.toString());

                }

            }
        }
        catch (Exception e){
            CMNLogHelper.logError("UserFlagListenerFailed",e.getMessage() );
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
