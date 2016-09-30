package zina_eliran.app.API.Listeners;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.ArrayList;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 9/23/2016.
 */

public class OnSetValueCompleteListener implements Firebase.CompletionListener{
    private FireBaseHandler fbHandler;
    private ArrayList<BEBaseEntity> entities;
    private DALActionTypeEnum action;
    private BETypesEnum objectType;

    /***No callback onComplete if FirebaseHandler is null***/
    public  OnSetValueCompleteListener(FireBaseHandler fbHandler, ArrayList<BEBaseEntity> entities, DALActionTypeEnum action, BETypesEnum objectType){
        this.fbHandler = fbHandler;
        this.entities = entities;
        this.action = action;
        this.objectType = objectType;
    }


    @Override
    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
        BEResponse response = new BEResponse();
        response.setActionType(action);
        response.setEntityType(objectType);

        //DB action completed with error
        if (firebaseError != null) {
            response.setStatus(BEResponseStatusEnum.error);
            response.setMessage(firebaseError.getMessage());
        }

        //DB action completed with no error
        else {
            response.setStatus(BEResponseStatusEnum.success);
            response.setEntity(entities);
            CMNLogHelper.logError("OnComplete", firebase.toString());

            //Print object after DB update
            CMNLogHelper.logError("OnSetValue-forward", entities.get(0).toString());
        }

        //No callback if Handler is null
        if (fbHandler != null)
            fbHandler.onActionCallback(response);
    }
}
