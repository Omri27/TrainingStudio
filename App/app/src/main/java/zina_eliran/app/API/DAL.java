package zina_eliran.app.API;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Date;

import zina_eliran.app.API.Listeners.OnSetValueCompleteListener;
import zina_eliran.app.API.Listeners.GetBEObjectEventListener;
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
 * Created by Zina K on 9/10/2016.
 */
public class DAL{

    //TODO Zina - please add try catch :)
    private static Firebase rootRef = new Firebase("https://trainingstudiofb.firebaseio.com");
    private static Firebase usersRef = rootRef.child("Users");
    private static Firebase trainingsRef = rootRef.child("Trainings");

    public DAL(){
        //tests();
    }

    public static void registerUser(BEUser user, FireBaseHandler fbHandler) {
        if (user != null) {
            user.setVerificationCode(ServerAPI.generateVerificationCode());
            createObject(BETypesEnum.Users, DALActionTypeEnum.registerUser, user, fbHandler);
        }
        else
            cannotPerformAction(fbHandler, DALActionTypeEnum.registerUser, "Cannot create null user");
    }


    public static void createTraining(BETraining training, FireBaseHandler fireBaseHandler){
        if (training != null)
            createObject(BETypesEnum.Trainings, DALActionTypeEnum.createTraining, training, fireBaseHandler);
        cannotPerformAction(fireBaseHandler, DALActionTypeEnum.createTraining, "Cannot create null object");

    }

    //Currenlty it just overrides the user
    public static void updateUser(BEUser user,FireBaseHandler fireBaseHandler){
        if (user != null)
            updateObject(BETypesEnum.Users, DALActionTypeEnum.updateUser, user, fireBaseHandler);
        cannotPerformAction(fireBaseHandler, DALActionTypeEnum.updateUser, "Cannot update user null");
    }

    //Currenlty it just overrides the training
    public static void updateTraining(BETraining training, FireBaseHandler fireBaseHandler){
        if (training != null)
            updateObject(BETypesEnum.Trainings, DALActionTypeEnum.updateTraining, training, fireBaseHandler);
        cannotPerformAction(fireBaseHandler, DALActionTypeEnum.updateTraining, "Cannot update training null");
    }


    public static void getUserByUID(String userId, FireBaseHandler fbHandler){
        getObject(BETypesEnum.Users, DALActionTypeEnum.getUser, userId, fbHandler);
    }


    public static void getTraining(String trainingId, FireBaseHandler fireBaseHandler) {
        getObject(BETypesEnum.Trainings, DALActionTypeEnum.getTraining, trainingId, fireBaseHandler);
    }

    //Not Ready
    public static void getUsersByTraining(String trainingId, FireBaseHandler fireBaseHandler){
        if (!trainingId.isEmpty()){

            try {

            }
            catch (Exception e){
                CMNLogHelper.logError("DAL", e.getMessage());
            }

        }
    }


    public static void getAllTrainings(FireBaseHandler fireBaseHandler){
        getList(BETypesEnum.Trainings, DALActionTypeEnum.getAllTrainings, fireBaseHandler);
    }

    //Not Ready
    public static void getPublicTrainings(final ArrayList<String> excludeTrainingIds, FireBaseHandler fireBaseHandler){

//        BEResponse response = new BEResponse();
//
//        try{
//            GetAllTrainingsListener listener = new GetAllTrainingsListener();
//            trainingsRef.addListenerForSingleValueEvent(listener);
//
//            //Get list of all existing trainings from listener
//            ArrayList<BETraining> arr = listener.getAllTrainings();
//            ArrayList<BEBaseEntity> filteredArr = new ArrayList<>();
//
//            //filter trainings accoring to exclude list
//            if (!arr.isEmpty()){
//                for (int i = 0; i < arr.size(); i++){
//                    if (!excludeTrainingIds.contains(arr.get(i).getId())){
//                        filteredArr.add(arr.get(i));
//                        CMNLogHelper.logError("publicTrainings", arr.get(i).toString());
//                    }
//                }
//            }
//            //Set response
//            response.setStatus(BEResponseStatusEnum.success);
//            response.setActionType(DALActionTypeEnum.getPublicTrainings);
//            response.setEntity(filteredArr);
//            fireBaseHandler.onActionCallback(response);
//        }
//        catch (Exception e){
//            CMNLogHelper.logError("DAL", e.getMessage());
//            cannotPerformAction(fireBaseHandler,DALActionTypeEnum.getPublicTrainings, e.getMessage());
//        }
    }

    //Get all trainings, filter by userID - not ready
    public static void getTrainingsByUser(String userId, FireBaseHandler fireBaseHandler){
//        Firebase trainingListRef = trainingsRef;
//        BEResponse response = new BEResponse();
//        try{
//            GetAllTrainingsListener listener = new GetAllTrainingsListener();
//            trainingListRef.addListenerForSingleValueEvent(listener);
//
//            //Get list of all existing trainings from listener
//            ArrayList<BETraining> arr = listener.getAllTrainings();
//            ArrayList<BEBaseEntity> filteredArr = new ArrayList<>();
//
//            //filter trainings according to userId in creator or participant fields
//            if (!arr.isEmpty()){
//                for (int i = 0; i < arr.size(); i++){
//                    if (arr.get(i).getCreatorId().equals(userId) || arr.get(i).getPatricipatedUserIds().contains(userId)){
//                        filteredArr.add(arr.get(i));
//                        CMNLogHelper.logError("getTrainingByUser", arr.get(i).toString());
//                    }
//                }
//
//            }
//            //Set response
//            response.setStatus(BEResponseStatusEnum.success);
//            response.setEntity(filteredArr);
//            setActionResponse(response);
//
//
//        }
//        catch (Exception e){
//            CMNLogHelper.logError("DAL", e.getMessage());
//            response.setStatus(BEResponseStatusEnum.error);
//            setActionResponse(response);
//
//        }


    }

    public static void joinTraining(BEUser user, BETraining training, FireBaseHandler fireBaseHandler){
        try {
            if (user != null && training != null){
                /**Allow user to join if:
                 * user is not training creator
                 * user not participated to this training yet
                 * training is not in user's trainings list*/
                String userID = user.getId();
                String trainingID = training.getId();
                if (!training.getCreatorId().equals(userID) && !training.getPatricipatedUserIds().contains(userID) && !user.getMyTrainingIds().contains(trainingID)){
                    training.addUserToUsersList(userID);
                    user.addTrainingToTrainingList(trainingID);

                    //update user and training in DB
                    updateObject(BETypesEnum.Users, DALActionTypeEnum.joinTraining, user, fireBaseHandler);
                    updateObject(BETypesEnum.Trainings, DALActionTypeEnum.joinTraining, training, fireBaseHandler);

                }
                else
                    cannotPerformAction(fireBaseHandler, DALActionTypeEnum.joinTraining, "User cannot join this training");

            }
            else
                cannotPerformAction(fireBaseHandler,DALActionTypeEnum.joinTraining, "Cannot join training, wrong parameters");

        }
        catch (Exception e){
            CMNLogHelper.logError("joinTraining", e.getMessage());
            cannotPerformAction(fireBaseHandler,DALActionTypeEnum.joinTraining, e.getMessage());
        }

    }

    public static void leaveTraining(BEUser user, BETraining training, FireBaseHandler fireBaseHandler){
        try {
            if (user != null && training != null){
                String userID = user.getId();
                String trainingID = training.getId();
                if (!training.getCreatorId().equals(userID) && training.getPatricipatedUserIds().contains(userID) && user.getMyTrainingIds().contains(trainingID)){
                    training.removeUserFromTraining(userID);
                    user.removeTrainingFromUser(trainingID);

                    //update user and training in DB
                    updateObject(BETypesEnum.Users, DALActionTypeEnum.joinTraining, user, fireBaseHandler);
                    updateObject(BETypesEnum.Trainings, DALActionTypeEnum.joinTraining, training, fireBaseHandler);

                }
                else
                    cannotPerformAction(fireBaseHandler, DALActionTypeEnum.joinTraining, "User cannot leave this training");

            }
            else
                cannotPerformAction(fireBaseHandler,DALActionTypeEnum.joinTraining, "Cannot leave training, wrong parameters");

        }
        catch (Exception e){
            CMNLogHelper.logError("joinTraining", e.getMessage());
            cannotPerformAction(fireBaseHandler,DALActionTypeEnum.joinTraining, e.getMessage());
        }


    }

    //Not Ready
    public static void joinTraining(String trainingId, String userId, FireBaseHandler fireBaseHandler){
        //Ask Eliran if he can send objects instead of IDs, then i can just run update
        try{

//            if (trainingId != null && userId != null){
//                //Create DB reference
//                Firebase userRef = usersRef.child(userId);
//                Firebase trainingRef = trainingsRef.child(trainingId);
//
//                //Set up listeners
//
//                //Save objects in DB and return response via  GetBEObjectEventListener listenerUser = new GetBEObjectEventListener(ReadDataTypeEnum.user, false);
//                GetBEObjectEventListener listenerTraining = new GetBEObjectEventListener(BETypesEnum.Trainings, null, DALActionTypeEnum.getTraining );
//                GetBEObjectEventListener listenerUser = new GetBEObjectEventListener(BETypesEnum.Users, null, DALActionTypeEnum.getUser );
//
//                //Add listeners to DB references
//                userRef.addListenerForSingleValueEvent(listenerUser);
//                trainingRef.addListenerForSingleValueEvent(listenerTraining);
//
//                //Get relevant data from listener
//                BEUser user = (BEUser)listenerUser.getObject();
//                BETraining training = (BETraining)listenerTraining.getObject();
//                CMNLogHelper.logError("joinTraining-user", user.toString());
//                CMNLogHelper.logError("joinTraining-training", training.toString());
//
//                //Update objects with new IDs
//                if (user != null && training != null){
//                    user.addTrainingToTrainingList(trainingId);
//                    training.addUserToUsersList(userId);
//                    //Update changes in DB
//                    updateTraining(training, fireBaseHandler);
//                    updateUser(user, fireBaseHandler);
//                }
//
//            }
//            else
//                cannotPerformAction(fireBaseHandler, DALActionTypeEnum.joinTraining, "One of the parameters invalid");
        }
        catch (Exception e){
            CMNLogHelper.logError("joinTraining", e.getMessage());
        }
    }

    //Send Negative response
    private static void cannotPerformAction(FireBaseHandler fireBaseHandler, DALActionTypeEnum action, String message){
        BEResponse res = new BEResponse();
        res.setStatus(BEResponseStatusEnum.error);
        res.setActionType(action);
        res.setMessage(message);
        if (fireBaseHandler != null)
            fireBaseHandler.onActionCallback(res);
    }

    //Not Ready
    public static boolean checkIfExists(BETypesEnum objectType, String id){
        try{
            Firebase ref;
            if (objectType != null && id != null){
                ref = rootRef.child(objectType.toString()).child(id);
                CMNLogHelper.logError("IF-EXISTS", ref.toString());
                return true;
            }

            return false;
        }

        catch (Exception e){
            CMNLogHelper.logError("IF-EXISTS", e.getMessage());
        }
        return true;
    }


    //////////////////////////////////////////////////////////////////
    //register to listeners here

    //this function will be called when server API initiate
    private static void registerToEvents(){

    }


    //////////////////////////////////////////////////////////////////
    //////////// Generic DAL actions /////////////////////////////////

    private static void createObject(BETypesEnum objectType, DALActionTypeEnum action, BEBaseEntity object, FireBaseHandler fbHandler){
        try{

            //Set DB ref
            Firebase ref = rootRef.child(objectType.toString());

            //Check if already exists
            if (object.getId() != null){
                CMNLogHelper.logError("CREATE-OBJECT", "Already exists");
                cannotPerformAction(fbHandler, action, "Object already exists");
            }

            //Object does not exist in DB, so it will be created
            else{
                Firebase newObject = ref.push();

                // Get the unique ID generated by push()
                String generatedUniqueID = newObject.getKey();
                object.setId(generatedUniqueID);

                //Save including ID
                ArrayList<BEBaseEntity> entities = new ArrayList<>();
                entities.add(object);
                OnSetValueCompleteListener listener = new OnSetValueCompleteListener(fbHandler,entities, action, objectType);
                newObject.setValue(object, listener);
                CMNLogHelper.logError("Object created", object.toString());
            }
        }

        catch (Exception e){
            cannotPerformAction(fbHandler, action, "Could not create new object");
            CMNLogHelper.logError("Create-Object-DB", e.getMessage());
        }
    }


    private static void updateObject(BETypesEnum objectType, DALActionTypeEnum action, BEBaseEntity object, FireBaseHandler fbHandler){
        try{
            if (objectType != null && object.getId() != null ){
                //Set DB ref
                Firebase ref = rootRef.child(objectType.toString());
                Firebase objectRef = ref.child(object.getId());
                ArrayList<BEBaseEntity> entities = new ArrayList<>();
                entities.add(object);
                OnSetValueCompleteListener listener = new OnSetValueCompleteListener(fbHandler, entities, action, objectType);
                objectRef.setValue(object, listener);
                CMNLogHelper.logError("DAL", "object updated" + object.toString());
            }

            else
                cannotPerformAction(fbHandler, action, "Could not update training");

        }

        catch(Exception e){
            cannotPerformAction(fbHandler, action, "Could not update object");
            CMNLogHelper.logError("Create-Object-DB", e.getMessage());
        }
    }


    private static void getObject(BETypesEnum objectType, DALActionTypeEnum action, String uid,FireBaseHandler fireBaseHandler){
        BEBaseEntity obj = new BEBaseEntity();
        if (!uid.isEmpty() && objectType != null){
            try {
                Firebase specificObjectRef = rootRef.child(objectType.toString()).child(uid);
                GetBEObjectEventListener listener = new GetBEObjectEventListener(objectType, fireBaseHandler, action);
                specificObjectRef.addListenerForSingleValueEvent(listener);
                CMNLogHelper.logError("GETOBJECT" + new Date(), listener.getObject().toString());
                obj = listener.getObject();

            }
            catch (Exception e){
                CMNLogHelper.logError("GETOBJECT", e.getMessage());
                cannotPerformAction(fireBaseHandler,action, "Cannot get object" );
            }

        }
        else{
            cannotPerformAction(fireBaseHandler, action, "Cannot get object, invalid parameters");
        }
    }


    private static void getList(BETypesEnum objectType, DALActionTypeEnum action, FireBaseHandler fireBaseHandler){
        try{
            if (objectType != null && action != null){
                //Set DB ref
                Firebase ref = rootRef.child(objectType.toString());

                //Set listener
                GetBEObjectEventListener listener = new GetBEObjectEventListener(objectType, fireBaseHandler, action);
                ref.addListenerForSingleValueEvent(listener);
                CMNLogHelper.logError("GET-LIST-FUNC", "Listener called");
            }

            else
                cannotPerformAction(fireBaseHandler, action, "Could get training list, invalid parameters");

        }

        catch(Exception e){
            cannotPerformAction(fireBaseHandler, action, "Could get training list");
            CMNLogHelper.logError("GET-LIST-FUNC", e.getMessage());
        }
    }



    /////////////////////////////////////////////////////////////////
    //callbacks

    public static void setActionResponse(BEResponse response) {

        //Register usersRef to childEventListener
//        usersRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                CMNLogHelper.logError("ChildListenerAdd", dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                CMNLogHelper.logError("ChildListenerChange", dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        //use this to update App entities when need
        ServerAPI sApi = ServerAPI.getInstance();

        //set here response with error message which will get from firebase error if need
        sApi.setActionResponse(response);
    }



    ////////////////////////////////////////////////////////////////
    //Zina's tests

    public static void tests(FireBaseHandler fireBaseHandler){

        //Create user test
        BEUser user = new BEUser();
        user.setEmail("llaaa@whatever.com");
        user.setName("lala");
        CMNLogHelper.logError("Create-user", "test");
        registerUser(user, fireBaseHandler);

        //Find user by id test

//        BEUser getUser = getUserByUID(user.getId(), fireBaseHandler);
        getUserByUID(user.getId(), fireBaseHandler);
//        CMNLogHelper.logError("GET USER", getUser.toString());
        CMNLogHelper.logError("GET USER", "tsts");
        getUserByUID(user.getId(), fireBaseHandler);


        //Find user by training id


        //Update user
        user.setHeigth(180);
        user.setName("NEWuser name");
        updateUser(user, fireBaseHandler);


        //Create training test
        BETraining t = new BETraining();
        t.setName("NewTraining");
        t.setCreatorId(user.getId());
        t.setTrainingDate(new Date());
        CMNLogHelper.logError("Create-training", "test");
        createTraining(t, fireBaseHandler);

        //Find training by id test
        getTraining(t.getId(), fireBaseHandler);
        CMNLogHelper.logError("GET TRAINING", "test");

        //Update training
        t.setName("NEWUpdatedName");
        updateTraining(t, fireBaseHandler);

        //Find training by user id
//        CMNLogHelper.logError("GET TRAINING By user", "test");
//        getTrainingsByUser(user.getId(), fireBaseHandler);

        //join training
        CMNLogHelper.logError("JOIN TRAINING", "test");
        joinTraining(user.getId(), t.getId(), fireBaseHandler);

        //get all public trainings
        CMNLogHelper.logError("GET ALL TRAININGS", "test");
        getAllTrainings(fireBaseHandler);
//        ArrayList<String> arr = new ArrayList<>();
//        arr.add("-KSNfQcBV2d9ESKsQqic");
//        arr.add("-KSNfUF-UvM66pv4ZzRt");
//        arr.add("-KSNfUInzvpo27SmmZDS");
//        getPublicTrainings(arr, fireBaseHandler);


    }

}
