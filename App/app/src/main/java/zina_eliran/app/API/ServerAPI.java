package zina_eliran.app.API;

import java.util.ArrayList;

import zina_eliran.app.API.BL;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingListTypeEnum;
import zina_eliran.app.BusinessEntities.BEUser;

/**
 * Created by Zina K on 9/10/2016.
 */
public class ServerAPI {

    public static void registerUser(BEUser user){} //onUpdateUserCallback

    public static BEResponse verifyUser(BEUser user){
        return BL.verifyUser(user);
    } //? onUpdateUserCallback

    public static void getUser(String userId){} //onUpdateUserCallback

    public static void updateUser(BEUser user){} //onUpdateUserCallback

    public static void getUsersByTraining(String trainingId){} //onGetUsersByTrainingCallback

    public static void getTraining(String trainingId){} //?

    public static void getPublicTrainings(ArrayList<String> excludeTrainingIds){} //onGetTrainingsCallback

    public static void getTrainingsByUser(String userId){} //onGetTrainingsCallback

    public static void createTraining(BETraining training){} //onGetTrainingsCallback

    public static void updateTraining(BETraining training){} //num of participants and status can be changed

    public static void joinTraining(String trainingId, String userId){}




    /////////////////////////////////////////////////////////////////
    //callbacks

    public void actionResult(BEResponse response) {}

    public void onUpdateUserCallback(BEResponse response) {}

    public void onGetUsersByTrainingCallback(BEResponse response) {}

    public void onGetTrainingsCallback(BEResponse response , BETrainingListTypeEnum type) {}


}
