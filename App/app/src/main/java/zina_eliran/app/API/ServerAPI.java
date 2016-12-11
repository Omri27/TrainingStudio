package zina_eliran.app.API;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingStatusEnum;
import zina_eliran.app.BusinessEntities.BETrainingViewDetails;
import zina_eliran.app.BusinessEntities.BETrainingViewStatusEnum;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.TrainingComparator;

//singleton class
public class ServerAPI {

    final int xMinutesefore = 15;

    private static ServerAPI instance = null;

    private BEResponse actionResponse;

    private BEUser appUser;

    private BETraining nextTraining;

    ArrayList<BETraining> myJoinedTrainingsList;

    ArrayList<BETraining> myCreatedTrainingsList;

    ArrayList<BETraining> myEndedTrainingsList;

    ArrayList<BETrainingViewDetails> myEndedTrainingsViewList;

    ArrayList<BETraining> publicTrainingsList;


    protected ServerAPI() {
        // Exists only to defeat instantiation.
        appUser = new BEUser();
        nextTraining = new BETraining();
        myJoinedTrainingsList = new ArrayList<>();
        myCreatedTrainingsList = new ArrayList<>();
        myEndedTrainingsList = new ArrayList<>();
        myEndedTrainingsViewList = new ArrayList<>();
        publicTrainingsList = new ArrayList<>();
    }

    public static ServerAPI getInstance() {
        if (instance == null) {
            instance = new ServerAPI();
        }
        return instance;
    }

    public int getxMinutesefore() {
        return xMinutesefore;
    }

    public BEUser getAppUser() {
        return appUser;
    }

    public void setAppUser(BEUser currentUser) {
        this.appUser = currentUser;
    }

    public BETraining getNextTraining() {
        return nextTraining;
    }

    public void setNextTraining(BETraining nextTraining) {
        this.nextTraining = nextTraining;
    }

    public ArrayList<BETraining> getMyJoinedTrainingsList() {
        return myJoinedTrainingsList;
    }

    public void setMyJoinedTrainingsList(ArrayList<BETraining> myJoinedTrainingsList) {
        this.myJoinedTrainingsList = myJoinedTrainingsList;
    }

    public ArrayList<BETraining> getMyCreatedTrainingsList() {
        return myCreatedTrainingsList;
    }

    public void setMyCreatedTrainingsList(ArrayList<BETraining> myCreatedTrainingsList) {
        this.myCreatedTrainingsList = myCreatedTrainingsList;
    }

    public ArrayList<BETraining> getPublicTrainingsList() {
        return publicTrainingsList;
    }

    public void setPublicTrainingsList(ArrayList<BETraining> publicTrainingsList) {
        this.publicTrainingsList = publicTrainingsList;
    }

    public ArrayList<BETraining> getMyEndedTrainingsList() {
        return myEndedTrainingsList;
    }

    public void setMyEndedTrainingsList(ArrayList<BETraining> myEndedTrainingsList) {
        this.myEndedTrainingsList = myEndedTrainingsList;
    }

    public ArrayList<BETrainingViewDetails> getMyEndedTrainingsViewList() {
        return myEndedTrainingsViewList;
    }

    public void setMyEndedTrainingsViewList(ArrayList<BETrainingViewDetails> myEndedTrainingsViewList) {
        this.myEndedTrainingsViewList = myEndedTrainingsViewList;
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

    public void resendUserRegistrationEmail(String email, String name, String verificationCode) {
        DAL.resendUserRegistrationEmail(email, name, verificationCode);
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

    public void getTrainingView(String trainingId, String userId, FireBaseHandler fbHandler) {
        DAL.getTrainingView(trainingId, userId, fbHandler);
    }

    public void createTrainingView(BETrainingViewDetails trainingView, FireBaseHandler fbHandler) {
        DAL.createTrainingView(trainingView, fbHandler);
    }

    public void updateTrainingView(BETrainingViewDetails trainingView, FireBaseHandler fbHandler) {
        DAL.updateTrainingView(trainingView, fbHandler);
    }

    public void getAllTrainings(FireBaseHandler fbHandler) {
        DAL.getAllTrainings(fbHandler);
    }

    public void getAllTrainingViews(FireBaseHandler fbHandler) {
        DAL.getAllTrainingViewDetails(fbHandler);
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
        DAL.leaveTraining(trainingId, userId, fbHandler);
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
            Calendar c = Calendar.getInstance();
            //15 minutes before
            c.setTimeInMillis(c.getTimeInMillis() - 1000 * 60 * xMinutesefore);

            for (int i = 0; i < trainings.size(); i++) {
                BETraining training = (BETraining) trainings.get(i);
                if (!training.getCreatorId().equals(userId) &&
                        !(training.getPatricipatedUserIds().contains(userId)) &&
                        (training.getStatus() != BETrainingStatusEnum.cancelled) &&
                        (training.getTrainingDateTimeCalender().getTimeInMillis() >= c.getTimeInMillis())) {
                    publicTrainings.add(((BETraining) trainings.get(i)));
                }
            }
        }

        Collections.sort(publicTrainings, new TrainingComparator());
        return publicTrainings;

    }

    public ArrayList<BETraining> filterMyTrainings(String userId, ArrayList<BEBaseEntity> trainings, boolean isCreatedByMe) {
        ArrayList<BETraining> myTrainings = new ArrayList<>();
        ArrayList<BETraining> myEndedTrainings = new ArrayList<>();
        //Filter all trainings that user participated in or creator
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() + 15 * 1000 * 60);
        BETraining next = null;

        if (trainings != null && !trainings.isEmpty()) {
            for (int i = 0; i < trainings.size(); i++) {
                BETraining currentTraining = ((BETraining) trainings.get(i));
                if (((currentTraining.getPatricipatedUserIds().contains(userId) && !isCreatedByMe &&
                        currentTraining.getStatus() != BETrainingStatusEnum.cancelled) ||

                        (currentTraining.getCreatorId().equals(userId) && isCreatedByMe &&
                                currentTraining.getStatus() != BETrainingStatusEnum.cancelled))) {


                    myTrainings.add(currentTraining);
                    //if training date is passed (up to 15 minutes before "now")
                    if (currentTraining.getTrainingDateTimeCalender().getTimeInMillis() < cal.getTimeInMillis() &&
                            !isCreatedByMe) {
                        //if the training suppose to start within the next 15 minutes - set as next training.
                        if (currentTraining.getTrainingDateTimeCalender().getTimeInMillis() >= Calendar.getInstance().getTimeInMillis())
                        {
                            next = currentTraining;
                        }
                        myEndedTrainings.add(currentTraining);
                    }
                }
            }

            if(getNextTraining() != null && getNextTraining().getTrainingDateTimeCalender() != null &&
                    (getNextTraining().getTrainingDateTimeCalender().getTimeInMillis() + getNextTraining().getDuration()*1000*60) < Calendar.getInstance().getTimeInMillis()){
                setNextTraining(null);
            }

            if (next != null) {
                //if the current next training is passed + its duration
                if((getNextTraining() == null || getNextTraining().getTrainingDateTimeCalender() == null) ||
                        (getNextTraining().getTrainingDateTimeCalender().getTimeInMillis() <= next.getTrainingDateTimeCalender().getTimeInMillis()
                        && next.getTrainingDateTimeCalender().getTimeInMillis() > (getNextTraining().getTrainingDateTimeCalender().getTimeInMillis() + (getNextTraining().getDuration()-5)*1000*60))){
                    setNextTraining(next);
                }
            }

        } else {
            CMNLogHelper.logError("myTrainingsFilter", "No training found");
        }


        if (!isCreatedByMe) {
            Collections.sort(myEndedTrainings, new TrainingComparator());
            setMyEndedTrainingsList(myEndedTrainings);
        }

        Collections.sort(myTrainings, new TrainingComparator());
        return myTrainings;

    }

    public ArrayList<BETraining> filterMyEndedTrainings(String userId) {

        //extract all actually ended trainings
        ArrayList<BETrainingViewDetails> myEndedTrainingsViews = getMyEndedTrainingsViewList();
        ArrayList<BETraining> myResultEndedTrainingsList = new ArrayList<>();

        for (int i = 0; i < myEndedTrainingsViews.size(); i++) {
            for (int j = 0; j < myEndedTrainingsList.size(); j++) {
                if (myEndedTrainingsList.get(j).getId().equals(myEndedTrainingsViews.get(i).getTrainingId())) {
                    myResultEndedTrainingsList.add(myEndedTrainingsList.get(j));
                }
            }
        }

        Collections.sort(myResultEndedTrainingsList, new TrainingComparator());
        return myResultEndedTrainingsList;

    }

    public ArrayList<BETrainingViewDetails> filterMyEndedTrainingsView(String userId, ArrayList<BEBaseEntity> trainingViews) {

        ArrayList<BETrainingViewDetails> myResultTrainingsViews = new ArrayList<>();

        //extract all *my* ended trainings view
        for (int i = 0; i < trainingViews.size(); i++) {
            BETrainingViewDetails currentTrainingView = (BETrainingViewDetails) trainingViews.get(i);
            if (currentTrainingView.getStatus() == BETrainingViewStatusEnum.ended &&
                    currentTrainingView.getUserId().equals(userId)) {
                myResultTrainingsViews.add(currentTrainingView);
            }
        }

        return myResultTrainingsViews;

    }

    public BETraining getMyNextTraining(ArrayList<BETraining> trainings) {
        BETraining nextTraining = null;
        if (trainings.size() > 0) {
            Collections.sort(trainings, new TrainingComparator());
            nextTraining = trainings.get(0);
        }
        return nextTraining;

    }

    public void updateAppTrainingsData(ArrayList<BEBaseEntity> entities) {
        //update server api

        setMyJoinedTrainingsList(filterMyTrainings(getAppUser().getId(), entities, false));
        setMyCreatedTrainingsList(filterMyTrainings(getAppUser().getId(), entities, true));
        setPublicTrainingsList(filterPublicTrainings(getAppUser().getId(), entities));
    }

    public boolean isMyTrainingExist(BETraining training, boolean isCreatedByMe) {

        List<BETraining> trainingList = myJoinedTrainingsList;

        if (isCreatedByMe) {
            trainingList = myCreatedTrainingsList;
        }

        for (int i = 0; i < trainingList.size(); i++) {
            if (trainingList.get(i).isTrainingDatesOverlap(training)) {
                return true;
            }
        }

        return false;
    }
}
