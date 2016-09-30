package zina_eliran.app.API;

import java.util.ArrayList;
import java.util.Random;

import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;

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


    public void registerUser(BEUser user, FireBaseHandler fbHandler){
        DAL.registerUser(user, fbHandler);
    }


    public void getUser(String userId, FireBaseHandler fbHandler){
        DAL.getUserByUID(userId, fbHandler);
    }


    public void updateUser(BEUser user, FireBaseHandler fbHandler){
        DAL.updateUser(user, fbHandler);
    }


    public void getUsersByTraining(String trainingId, FireBaseHandler fbHandler){
        DAL.getUsersByTraining(trainingId, fbHandler);
    }


    public void getTraining(String trainingId, FireBaseHandler fbHandler){
        DAL.getTraining(trainingId, fbHandler);
    }


    public void getPublicTrainings(ArrayList<String> excludeTrainingIds, FireBaseHandler fbHandler){
        DAL.getPublicTrainings(excludeTrainingIds, fbHandler);
    }

    public void getAllTrainings(FireBaseHandler fbHandler){
        DAL.getAllTrainings(fbHandler);
    }


    public void getTrainingsByUser(String userId, FireBaseHandler fbHandler){
        DAL.getTrainingsByUser(userId, fbHandler);
    }


    public void createTraining(BETraining training, FireBaseHandler fbHandler){
        DAL.createTraining(training, fbHandler);
    }


    public void updateTraining(BETraining training, FireBaseHandler fbHandler){
        DAL.updateTraining(training, fbHandler);
    } //num of participants and status can be changed


    public void joinTraining(String trainingId, String userId, FireBaseHandler fbHandler){
        DAL.joinTraining(trainingId,userId, fbHandler);
    }

    public void leaveTraining(String trainingId, String userId, FireBaseHandler fbHandler){

    }


    //extra functions
    //**************


    //Generate 6 digit ramdon verification code
    public static String generateVerificationCode(){
        Random rand = new Random();
        Integer num = rand.nextInt(900000) + 100000;
        return num.toString();
    }


    public void filterPublicTrainings(ArrayList<String> excludeTrainingIds, ArrayList<BETraining> trainings){
        ArrayList<BETraining> publicTrainings = new ArrayList<>();

        //Filter trainings to exlude ones in excludeTrainingIds
        if (!trainings.isEmpty()){
            for (int i = 0; i < trainings.size(); i++){
                if (!excludeTrainingIds.contains(trainings.get(i).getId())){
                    publicTrainings.add(trainings.get(i));
                    CMNLogHelper.logError("publicTrainings", trainings.get(i).toString());
                }
            }
        }

        this.publicTrainings = publicTrainings;

    }

    public void filterMyTrainings(String userId, ArrayList<BETraining> trainings){
        ArrayList<BETraining> myTrainings = new ArrayList<>();
        //Filter all trainings that user participeted in or creator
        if (!trainings.isEmpty()){
            for (int i = 0; i < trainings.size(); i++){
                BETraining currectTraining = trainings.get(i);
                if (currectTraining.getPatricipatedUserIds().contains(userId) || currectTraining.getCreatorId().equals(userId)){
                    myTrainings.add(trainings.get(i));
                    CMNLogHelper.logError("myTrainings", currectTraining.toString());
                }
            }
        }
        this.myTrainings = myTrainings;


    }



}
