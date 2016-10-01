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

public class DAL {

    private static Firebase rootRef = new Firebase("https://trainingstudiofb.firebaseio.com");
    private static Firebase usersRef = rootRef.child("Users");
    private static Firebase trainingsRef = rootRef.child("Trainings");


    public static void registerUser(BEUser user, FireBaseHandler fbHandler) {
        if (user != null) {
            user.setVerificationCode(ServerAPI.generateVerificationCode());
            createObject(BETypesEnum.Users, DALActionTypeEnum.registerUser, user, fbHandler);
        } else
            cannotPerformAction(fbHandler, DALActionTypeEnum.registerUser, "Cannot create null user");
    }


    public static void createTraining(BETraining training, FireBaseHandler fireBaseHandler) {
        if (training != null) {
            createObject(BETypesEnum.Trainings, DALActionTypeEnum.createTraining, training, fireBaseHandler);
        } else {
            cannotPerformAction(fireBaseHandler, DALActionTypeEnum.createTraining, "Cannot create null training");
        }


    }


    public static void updateUser(BEUser user, FireBaseHandler fireBaseHandler) {
        if (user != null) {
            updateObject(BETypesEnum.Users, DALActionTypeEnum.updateUser, user, fireBaseHandler);
        } else {
            cannotPerformAction(fireBaseHandler, DALActionTypeEnum.updateUser, "Cannot update user null");
        }
    }


    public static void updateTraining(BETraining training, FireBaseHandler fireBaseHandler) {
        if (training != null) {
            updateObject(BETypesEnum.Trainings, DALActionTypeEnum.updateTraining, training, fireBaseHandler);
        } else {
            cannotPerformAction(fireBaseHandler, DALActionTypeEnum.updateTraining, "Cannot update training null");
        }
    }


    public static void getUserByUID(String userId, FireBaseHandler fbHandler) {
        getObject(BETypesEnum.Users, DALActionTypeEnum.getUser, userId, fbHandler);
    }


    public static void getTraining(String trainingId, FireBaseHandler fireBaseHandler) {
        getObject(BETypesEnum.Trainings, DALActionTypeEnum.getTraining, trainingId, fireBaseHandler);
    }

    //*
    public static void getUsersByTraining(String trainingId, FireBaseHandler fireBaseHandler) {
        if (!trainingId.isEmpty()) {

            try {

            } catch (Exception e) {
                CMNLogHelper.logError("DAL", e.getMessage());
            }

        }
    }


    public static void getAllTrainings(FireBaseHandler fireBaseHandler) {
        getList(BETypesEnum.Trainings, DALActionTypeEnum.getAllTrainings, fireBaseHandler);
    }


    //*
    public static void joinTraining(String trainingId, String userId, FireBaseHandler fireBaseHandler) {
        //Ask Eliran if he can send objects instead of IDs, then i can just run update
        try {
            if (trainingId != null && userId != null) {
                //Create DB reference
                Firebase userRef = usersRef.child(userId);
                Firebase trainingRef = trainingsRef.child(trainingId);

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
//                //Save objects in DB and return response via setActionResponce
//                updateTraining(training, null);
//                updateUser(user, null);

            } else
                cannotPerformAction(fireBaseHandler, DALActionTypeEnum.joinTraining, "One of the parameters invalid");
        } catch (Exception e) {
            CMNLogHelper.logError("joinTraining", e.getMessage());
        }
    }


    //////////////////////////////////////////////////////////////////
    //////////// Generic DAL actions /////////////////////////////////


    //Send Negative response
    private static void cannotPerformAction(FireBaseHandler fireBaseHandler, DALActionTypeEnum action, String message) {
        BEResponse res = new BEResponse();
        res.setStatus(BEResponseStatusEnum.error);
        res.setActionType(action);
        res.setMessage(message);
        if (fireBaseHandler != null)
            fireBaseHandler.onActionCallback(res);
    }


    private static void createObject(BETypesEnum objectType, DALActionTypeEnum action, BEBaseEntity object, FireBaseHandler fbHandler) {
        try {

            //Set DB ref
            Firebase ref = rootRef.child(objectType.toString());

            //Check if already exists
            if (object.getId() != null) {
                CMNLogHelper.logError("CREATE-OBJECT", "Object Already exists");
                cannotPerformAction(fbHandler, action, "Object already exists");
            }

            //Object does not exist in DB, so it will be created
            else {
                Firebase newObject = ref.push();

                // Get the unique ID generated by push()
                String generatedUniqueID = newObject.getKey();
                object.setId(generatedUniqueID);

                //Save including ID
                ArrayList<BEBaseEntity> entities = new ArrayList<>();
                entities.add(object);
                OnSetValueCompleteListener listener = new OnSetValueCompleteListener(fbHandler, entities, action, objectType);
                newObject.setValue(object, listener);
                CMNLogHelper.logError("Object created", object.toString());
            }
        } catch (Exception e) {
            cannotPerformAction(fbHandler, action, "Could not create new object");
            CMNLogHelper.logError("Create-Object-DB", e.getMessage());
        }
    }


    private static void updateObject(BETypesEnum objectType, DALActionTypeEnum action, BEBaseEntity object, FireBaseHandler fbHandler) {
        try {
            if (objectType != null && object.getId() != null) {
                //Set DB ref
                Firebase ref = rootRef.child(objectType.toString());
                Firebase objectRef = ref.child(object.getId());
                ArrayList<BEBaseEntity> entities = new ArrayList<>();
                entities.add(object);
                OnSetValueCompleteListener listener = new OnSetValueCompleteListener(fbHandler, entities, action, objectType);
                objectRef.setValue(object, listener);
                CMNLogHelper.logError("DAL", "Object updated" + object.toString());
            } else {
                cannotPerformAction(fbHandler, action, "Could not update training");
            }

        } catch (Exception e) {
            cannotPerformAction(fbHandler, action, "Could not update object");
            CMNLogHelper.logError("Create-Object-DB", e.getMessage());
        }
    }


    private static void getObject(BETypesEnum objectType, DALActionTypeEnum action, String uid, FireBaseHandler fireBaseHandler) {

        if (!uid.isEmpty() && objectType != null) {
            try {
                Firebase specificObjectRef = rootRef.child(objectType.toString()).child(uid);
                GetBEObjectEventListener listener = new GetBEObjectEventListener(objectType, fireBaseHandler, action);
                specificObjectRef.addListenerForSingleValueEvent(listener);
            } catch (Exception e) {
                CMNLogHelper.logError("GETOBJECT", e.getMessage());
                cannotPerformAction(fireBaseHandler, action, "Cannot get object");
            }

        } else {
            cannotPerformAction(fireBaseHandler, action, "Cannot get object, invalid parameters");
        }
    }


    private static void getList(BETypesEnum objectType, DALActionTypeEnum action, FireBaseHandler fireBaseHandler) {
        try {
            if (objectType != null && action != null) {
                //Set DB ref
                Firebase ref = rootRef.child(objectType.toString());

                //Set listener
                GetBEObjectEventListener listener = new GetBEObjectEventListener(objectType, fireBaseHandler, action);
                ref.addListenerForSingleValueEvent(listener);
                CMNLogHelper.logError("GET-LIST-FUNC", "Listener called");
            } else
                cannotPerformAction(fireBaseHandler, action, "Could get training list, invalid parameters");

        } catch (Exception e) {
            cannotPerformAction(fireBaseHandler, action, "Could get training list");
            CMNLogHelper.logError("GET-LIST-FUNC", e.getMessage());
        }
    }


    ////////////////////////////////////////////////////////////////
    //Zina's tests

    public static void tests(FireBaseHandler fireBaseHandler) {

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
