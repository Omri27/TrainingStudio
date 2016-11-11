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
            String key = dataSnapshot.getKey();
            if (key.equals("isTrainingRemainderNotification")){
                reminder = (boolean) dataSnapshot.getValue();
                forwardResponse(DALActionTypeEnum.userRemainderChanged,reminder);
                }
            else  if (key.equals("isTrainingFullNotification")){
                reminder = (boolean) dataSnapshot.getValue();
                forwardResponse(DALActionTypeEnum.userFullNotificationChanged,reminder);
            }

            else  if (key.equals("isTrainingCancelledNotification")){
                reminder = (boolean) dataSnapshot.getValue();
                forwardResponse(DALActionTypeEnum.userCancelledNotificationChanged,reminder);
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

    public void forwardResponse(DALActionTypeEnum actionTypeEnum, Boolean flagValue){
        if (fireBaseHandler != null){
            BEResponse res = new BEResponse();
            res.setStatus(BEResponseStatusEnum.success);
            res.setActionType(actionTypeEnum);
            res.setMessage(flagValue.toString());

        }
    }
}
