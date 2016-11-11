package zina_eliran.app.Notifications;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;

/**
 * Created by Zina K on 11/8/2016.
 */

public class TimerThread implements Runnable {
    private static BEUser user;
    private ArrayList<BETraining> trainings;
    private boolean isRunning = true;
    private static TimerThread instance;
    private static Context context;
    private static ArrayList<String> sentNotifications;

    protected TimerThread(BEUser user, ArrayList<BETraining> trainings, Context context) {
        this.user = user;
        this.trainings = trainings;
        this.context = context;
        this.sentNotifications = new ArrayList<>();
    }

    public static synchronized TimerThread getInstance(BEUser user, ArrayList<BETraining> trainings, Context context) {
        if (instance == null) {
            instance = new TimerThread(user,trainings,context);
        }

        return instance;
    }

    public void setTrainings(ArrayList<BETraining> trainings){
        this.trainings = trainings;
    }

    public void updateUser(BEUser user){
        this.user = user;
    }


    public void stopRunning() {
        this.isRunning = false;
    }

    //Max capacity of sent Notifications is 10, when it reaches 10 clear arraylist
    public static void addToSentNotifications(String trainingID){
        if (sentNotifications.size() > 10)
            sentNotifications.clear();
        sentNotifications.add(trainingID);
    }







    @Override
    public void run() {
        while (isRunning) {
            try {
                //reset sent notifications
                //sentNotifications.clear();

                checkUpcomingTrainings(trainings,user);
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void checkUpcomingTrainings(ArrayList<BETraining> trainings, BEUser user) {
        try {
            //CMNLogHelper.logError("ReminderThread", "Checking upcoming trainings");
            if (trainings!= null && user!= null) {
                //CMNLogHelper.logError("ReminderThread", "User and Trainings not null");
                Calendar cal = Calendar.getInstance(); // creates calendar
                cal.setTime(new Date()); // sets calendar time/date
                cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
                cal.getTime(); // returns new date object, one hour in the future

                Calendar cal1 = Calendar.getInstance(); // creates calendar
                cal1.setTime(new Date()); // sets calendar time/date

                //CMNLogHelper.logError("ReminderThread", "1 hour later and now " + cal.toString() + " " + cal1.toString());



                for (BETraining training : trainings) {
                    CMNLogHelper.logError("ReminderThreadTrainingCheckedInList", training.toString());
                    //CMNLogHelper.logError("ReminderThread time of training ", training.getTrainingDateTimeCalender().toString());
                    if (training.getTrainingDateTimeCalender().getTime().after(cal1.getTime()) && training.getTrainingDateTimeCalender().getTime().before(cal.getTime())){
//                    if (training.getTrainingDateTimeCalender().after(cal) && training.getTrainingDateTimeCalender().before(cal1)) {
                        if (!sentNotifications.contains(training.getId())){
                            //CMNLogHelper.logError("ReminderThread", "Should send");
                            //CMNLogHelper.logError("ReminderCheckedTraining", training.toString());
                            NotificationSender sender = new NotificationSender(user,training,NotificationTypeEnum.reminder,context);
                            new Thread(sender).start();

                        }
                        //else CMNLogHelper.logError("ReminderThread Should not send, already sent", sentNotifications.toString());
                    }
                    //else
                       //CMNLogHelper.logError("ReminderThread", "Should not send");

                }
            }
            else
                CMNLogHelper.logError("ReminderThread", "User or Trainings are null");

        } catch (Exception e) {
            CMNLogHelper.logError("FailedToCheckDate", e.getMessage());
        }

    }
}
