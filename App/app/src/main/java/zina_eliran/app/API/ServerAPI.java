package zina_eliran.app.API;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import zina_eliran.app.API.EmailSender.EmailSender;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingAdapter;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;

//singleton class
public class ServerAPI {

    private static ServerAPI instance = null;

    private BEResponse actionResponse;

    private BEUser appUser;

    protected ServerAPI() {
        // Exists only to defeat instantiation.
    }

    public static ServerAPI getInstance() {
        if (instance == null) {
            instance = new ServerAPI();
        }
        return instance;
    }

    public BEUser getAppUser() {
        return appUser;
    }

    public void setAppUser(BEUser currentUser) {
        this.appUser = currentUser;
    }


    public BEResponse getActionResponse() {
        return actionResponse;
    }

    public void setActionResponse(BEResponse actionResponse) {
        this.actionResponse = actionResponse;
    }



    //business logic
    //***************


    public void registerUser(BEUser user, FireBaseHandler fbHandler) {
        DAL.registerUser(user, fbHandler);
    }


    public void getUser(String userId, FireBaseHandler fbHandler) {
        DAL.getUserByUID(userId, fbHandler);
    }


    public void updateUser(BEUser user, FireBaseHandler fbHandler) {
        DAL.updateUser(user, fbHandler);
    }


    public void getUsersByTraining(String trainingId, FireBaseHandler fbHandler) {
        DAL.getUsersByTraining(trainingId, fbHandler);
    }


    public void getTraining(String trainingId, FireBaseHandler fbHandler) {
        DAL.getTraining(trainingId, fbHandler);
    }


    public void getAllTrainings(FireBaseHandler fbHandler) {
        DAL.getAllTrainings(fbHandler);
    }



    public void createTraining(BETraining training, FireBaseHandler fbHandler) {
        DAL.createTraining(training, fbHandler);
    }


    public void updateTraining(BETraining training, FireBaseHandler fbHandler) {
        DAL.updateTraining(training, fbHandler);
    } //num of participants and status can be changed


    public void joinTraining(String trainingId, String userId, FireBaseHandler fbHandler) {
        DAL.joinTraining(trainingId, userId, fbHandler);
    }

    public void leaveTraining(String trainingId, String userId, FireBaseHandler fbHandler) {
        DAL.leaveTraining(trainingId,userId,fbHandler);
    }


    //extra functions
    //**************


    //Generate 6 digit ramdon verification code
    public static String generateVerificationCode() {
        Random rand = new Random();
        Integer num = rand.nextInt(900000) + 100000;
        return num.toString();
    }


    public ArrayList<BETraining> filterPublicTrainings(String userId, ArrayList<BEBaseEntity> trainings) {
        ArrayList<BETraining> publicTrainings = new ArrayList<>();

        //Filter trainings to exclude current user, cancelled trainings, and past trainings
        if (!trainings.isEmpty()) {
            for (int i = 0; i < trainings.size(); i++) {
                BETraining training = (BETraining) trainings.get(i);
                if (!training.getCreatorId().equals(userId) ||
                        (training.getPatricipatedUserIds().contains(userId)) &&
                        (training.getStatus() != BETrainingStatusEnum.cancelled) &&
                                (training.getTrainingDateTimeCalender().after(new Date()))) {
                    publicTrainings.add(((BETraining) trainings.get(i)));
//                    CMNLogHelper.logError("publicTrainings", trainings.get(i).toString());
                }
            }
        }

        return publicTrainings;

    }

    public ArrayList<BETraining> filterMyTrainings(String userId, ArrayList<BEBaseEntity> trainings, boolean isCreatedByMe) {
        ArrayList<BETraining> myTrainings = new ArrayList<>();
        //Filter all trainings that user participeted in or creator
        if (!trainings.isEmpty()) {
            for (int i = 0; i < trainings.size(); i++) {
                BETraining currentTraining = ((BETraining) trainings.get(i));
                if ((currentTraining.getPatricipatedUserIds().contains(userId) && !isCreatedByMe &&
                        ((BETraining) trainings.get(i)).getStatus() != BETrainingStatusEnum.cancelled) ||
                        (currentTraining.getCreatorId().equals(userId) && isCreatedByMe &&
                                ((BETraining) trainings.get(i)).getStatus() != BETrainingStatusEnum.cancelled)) {
                    myTrainings.add(currentTraining);
//                    CMNLogHelper.logError("myTrainings", currentTraining.toString());
                }
            }
        }
        return myTrainings;

    }


}
