package zina_eliran.app.API;

import java.util.ArrayList;
import java.util.Random;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;

//singleton class
public class ServerAPI {

    private static ServerAPI instance = null;

    private BEResponse actionResponse;

    private BEUser appUser;
    private ArrayList<BEUser> trainingUsers;
    private ArrayList<BETraining> myTrainings;
    private ArrayList<BETraining> publicTrainings;

    protected ServerAPI() {
        // Exists only to defeat instantiation.
    }

    public static ServerAPI getInstance() {
        if(instance == null) {
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

    public ArrayList<BEUser> getTrainingUsers() {
        return trainingUsers;
    }

    public void setTrainingUsers(ArrayList<BEUser> trainingUsers) {
        this.trainingUsers = trainingUsers;
    }

    public ArrayList<BETraining> getMyTrainings() {
        return myTrainings;
    }

    public void setMyTrainings(ArrayList<BETraining> myTrainings) {
        this.myTrainings = myTrainings;
    }

    public ArrayList<BETraining> getPublicTrainings() {
        return publicTrainings;
    }

    public void setPublicTrainings(ArrayList<BETraining> publicTrainings) {
        this.publicTrainings = publicTrainings;
    }

    public BEResponse getActionResponse() {
        return actionResponse;
    }

    public void setActionResponse(BEResponse actionResponse) {
        this.actionResponse = actionResponse;
    }

//business logic
    //**************


    public void registerUser(BEUser user){} //onUpdateUserCallback

    public void getUser(String userId){} //onUpdateUserCallback

    public void updateUser(BEUser user){} //onUpdateUserCallback

    public void getUsersByTraining(String trainingId){} //onGetUsersByTrainingCallback

    public void getTraining(String trainingId){} //?

    public void getPublicTrainings(ArrayList<String> excludeTrainingIds){} //onGetTrainingsCallback

    public void getTrainingsByUser(String userId){} //onGetTrainingsCallback

    public void createTraining(BETraining training){} //onGetTrainingsCallback

    public void updateTraining(BETraining training){} //num of participants and status can be changed

    public void joinTraining(String trainingId, String userId){}


    //extra functions
    //**************

    public static String generateVerificationCode(){
        Random rand = new Random();
        Integer num = rand.nextInt(900000) + 100000;
        return num.toString();
    }



}
