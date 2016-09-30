package zina_eliran.app.API;

import java.util.ArrayList;
import java.util.Random;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.Utils.FireBaseHandler;

//singleton class
public class ServerAPI {

    private static ServerAPI instance = null;

    private BEResponse actionResponse;

    private BEUser appUser;
    private ArrayList<BEUser> trainingUsers;
    private ArrayList<BEUser> selectedTrainingUsers;
    private BETraining selectedTraining;
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

    //Get response via getActionResponse
    public void registerUser(BEUser user, FireBaseHandler fbHandler){
        DAL.registerUser(user, fbHandler);
    }

    //Get response via getActionResponse
    public void getUser(String userId){
        DAL.getUserByUID(userId);
    }

    //Get response via getActionResponse
    public void updateUser(BEUser user){
        DAL.updateUser(user);
    }

    //Get response via getActionResponse
    public void getUsersByTraining(String trainingId){
        DAL.getUsersByTraining(trainingId);
    }

    //Get response via getActionResponse
    public void getTraining(String trainingId){
        DAL.getTraining(trainingId);
    }

    //Get response via getActionResponse
    public void getPublicTrainings(ArrayList<String> excludeTrainingIds){
        DAL.getPublicTrainings(excludeTrainingIds);
    }

    //Get response via getActionResponse
    public void getTrainingsByUser(String userId){
        DAL.getTrainingsByUser(userId);
    }

    //Get response via getActionResponse
    public void createTraining(BETraining training){
        DAL.createTraining(training);
    }

    //Get response via getActionResponse
    public void updateTraining(BETraining training){
        DAL.updateTraining(training);
    } //num of participants and status can be changed

    //Get response via getActionResponse
    public void joinTraining(String trainingId, String userId){
        DAL.joinTraining(trainingId,userId);
    }

    public void leaveTraining(String trainingId, String userId){

    }


    //extra functions
    //**************


    //Generate 6 digit ramdon verification code
    public static String generateVerificationCode(){
        Random rand = new Random();
        Integer num = rand.nextInt(900000) + 100000;
        return num.toString();
    }



}
