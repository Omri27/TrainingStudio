package zina_eliran.app.Notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import zina_eliran.app.API.DAL;
import zina_eliran.app.API.ServerAPI;
import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.LobbyActivity;
import zina_eliran.app.R;
import zina_eliran.app.Utils.FireBaseHandler;

public class DBMonitoringService extends Service implements FireBaseHandler {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BEUser user;
    private BETraining training;
    private TimerThread reminderThread;
    private PeriodicalUpdateTrainingListThread periodicalUpdateTrainingListThread;
    private ArrayList<BETraining> myCreatedTrainings;
    private ArrayList<BETraining> myJoinedTrainings;
    private Bundle extras;
    private static ServerAPI serverAPI;


    @Override
    public void onActionCallback(BEResponse response) {
        if (response != null && response.getStatus() != BEResponseStatusEnum.error) {

            //Get user fired - update service user, set listener on user's fields
            if (response.getActionType() == DALActionTypeEnum.getUser){
                try {
                    if (response.getEntities().get(0) != null) {
                        setUser((BEUser) response.getEntities().get(0));
                        DAL.addListenerToUser(user, this);
                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("SERVICEgetUserFailed", e.getMessage());
                }

            }

            //Get training fired
            else if (response.getActionType() == DALActionTypeEnum.getTraining) {
                try {
                    if (response.getEntities().get(0) != null) {
                        setTraining((BETraining) response.getEntities().get(0));
                        //DAL.addListenerToTraining(training.getId(), this);

                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("SERVICEgetTrainingFailed", e.getMessage());
                }

            }

            //Listener fires status change to trainingCancelled - if the training is joined send notification
            else if (response.getActionType() == DALActionTypeEnum.trainingCancelled) {
                try {
                    String trainingID = response.getMessage();
                    for (BETraining training : myJoinedTrainings) {
                        if (training.getId().equals(trainingID)) {
                            NotificationSender sender = new NotificationSender(user, training, NotificationTypeEnum.trainingIsCancelled, this);
                            new Thread(sender).start();
                        }
                    }
                } catch (Exception e) {
                    CMNLogHelper.logError("SERVICEtrainingCancelledFailed", e.getMessage());
                }
            }

            //Listener fires status change to trainingFull - if the training is full send notification
            else if (response.getActionType() == DALActionTypeEnum.trainingFull) {

                try {
                    String trainingID = response.getMessage();
                    BETraining trainingObjFound = findTrainingByID(trainingID, myCreatedTrainings);
                    if (trainingObjFound != null) {
                        NotificationSender sender = new NotificationSender(user, trainingObjFound, NotificationTypeEnum.trainingIsFull, this);
                        new Thread(sender).start();
                    } else {
                        trainingObjFound = findTrainingByID(trainingID, myJoinedTrainings);
                        if (trainingObjFound != null) {
                            NotificationSender sender = new NotificationSender(user, trainingObjFound, NotificationTypeEnum.trainingIsFull, this);
                            new Thread(sender).start();
                        }
                    }
                } catch (Exception e) {
                    CMNLogHelper.logError("SERVICEtrainingFullFailed", e.getMessage());
                }
            }

            /*User subscribed to training reminder notifications
            * if set true - start reminder thread
            * if set false - stop reminder thread*/
            else if (response.getActionType() == DALActionTypeEnum.userRemainderChanged) {
                try {
                    boolean reminder = Boolean.parseBoolean(response.getMessage());
                    if (reminder) {
                        reminderThread = TimerThread.getInstance(user, joinTrainings(myJoinedTrainings, myCreatedTrainings), this);
                        new Thread(reminderThread).start();
                    } else
                        reminderThread.stopRunning();

                } catch (Exception e) {
                    CMNLogHelper.logError("UserReminderFailed", e.getMessage());
                }
            }

            /*User changed trainingCancelled notification flag
            * Update service user with relevant flag
            * Update reminder thread user */
            else if (response.getActionType() == DALActionTypeEnum.userCancelledNotificationChanged){
                try{
                    if (response.getEntities().get(0) != null){
                        BEUser userFromResponse = (BEUser)response.getEntities().get(0);
                        user.setTrainingCancelledNotification(userFromResponse.isTrainingCancelledNotification());
                        reminderThread.updateUser(user);
                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("TrainingCancelledFailed", e.getMessage());
                }
            }

            /*User changed trainingIsFull notification flag
            * Update service user with relevant flag
            * Update reminder thread user */
            else if (response.getActionType() == DALActionTypeEnum.userFullNotificationChanged){
                try{
                    if (response.getEntities().get(0) != null){
                        BEUser userFromResponse = (BEUser)response.getEntities().get(0);
                        user.setTrainingCancelledNotification(userFromResponse.isTrainingFullNotification());
                        reminderThread.updateUser(user);
                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("TrainingFullFailed", e.getMessage());
                }
            }

            /*AllTrainingListener fires change when appUser creates a new training
            * */
            else if (response.getActionType() == DALActionTypeEnum.iCreatedTraining){
                try{
                    if (response.getEntities().get(0) != null){
                        BETraining training = (BETraining)response.getEntities().get(0);
                        //myCreatedTrainings.add(training);
                        //DAL.addListenerToTraining(training.getId(), this);
                        //reminderThread.setTrainings(joinTrainings(myJoinedTrainings,myCreatedTrainings));
                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("SERVICEMyCreatedTrainingAddFailed", e.getMessage());
                }

            }

            /*AllTrainingListener fires change when appUser joines to a new training
            * */
            else if (response.getActionType() == DALActionTypeEnum.iJoinedToTraining){
                try{
                    if (response.getEntities().get(0) != null){
                        BETraining training = (BETraining)response.getEntities().get(0);
                        //myJoinedTrainings.add(training);
                        //CMNLogHelper.logError("SERVICEMyJoinedTrainingAdded", training.toString());
                        //DAL.addListenerToTraining(training.getId(), this);
                        //reminderThread.setTrainings(joinTrainings(myJoinedTrainings,myCreatedTrainings));
                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("SERVICEMyCreatedTrainingAddFailed", e.getMessage());
                }

            }


            /*TrainingDetailsListener fires change when number of participants changes
            * if the training is my training and number has increased send notification*/
            else if (response.getActionType() == DALActionTypeEnum.numberOfParticipantsChanged) {
                try {
                    String[] data = response.getMessage().split(";");
                    String trainingID = data[0];
                    Integer numOfJoined = Integer.parseInt(data[1]);
                    for (BETraining training : myCreatedTrainings) {
                        if (training.getId().equals(trainingID)) {
                            if (training.getCurrentNumberOfParticipants() < numOfJoined) {
                                NotificationSender sender = new NotificationSender(user, training, NotificationTypeEnum.userJoinedToTraining, this);
                                new Thread(sender).start();
                            }
                            training.setCurrentNumberOfParticipants(numOfJoined);
                        }
                    }

                } catch (Exception e) {
                    CMNLogHelper.logError("SERVICENumOfJoinedChangeFailed", e.getMessage());
                }
            }

            /*Get All trainings, sort by type my/joined
            * if this is the fisrt time of fetching trainings, register all to listener
            * otherwise register only new added trainings*/
            else if (response.getActionType() == DALActionTypeEnum.getAllTrainings) {
                try {
                    if (user != null) {
                        if (myCreatedTrainings != null && !myCreatedTrainings.isEmpty())
                            addNewTrainings(myCreatedTrainings, serverAPI.filterMyTrainings(user.getId(), (ArrayList<BEBaseEntity>) response.getEntities(), true) );
                        else{
                            myCreatedTrainings = serverAPI.filterMyTrainings(user.getId(), (ArrayList<BEBaseEntity>) response.getEntities(), true);
                            registerToNotifications(myCreatedTrainings);
                        }


                        if (myJoinedTrainings != null && !myJoinedTrainings.isEmpty())
                            addNewTrainings(myJoinedTrainings, serverAPI.filterMyTrainings(user.getId(), (ArrayList<BEBaseEntity>) response.getEntities(), false) );
                        else{
                            myJoinedTrainings = serverAPI.filterMyTrainings(user.getId(), (ArrayList<BEBaseEntity>) response.getEntities(), false);
                            registerToNotifications(myJoinedTrainings);
                        }

                        //If user subscribed to reminder notifications, send updated trainings list to thread
                        if (user.isTrainingRemainderNotification() && (myCreatedTrainings != null || myJoinedTrainings != null)) {
                            reminderThread = TimerThread.getInstance(user, joinTrainings(myJoinedTrainings, myCreatedTrainings), this);
                            new Thread(reminderThread).start();
                        }

                        //If periodic thread not started, start
                        periodicalUpdateTrainingListThread = PeriodicalUpdateTrainingListThread.getInstance(this);
                        if (!periodicalUpdateTrainingListThread.isRunning())
                            new Thread(periodicalUpdateTrainingListThread).start();

                    } else
                        CMNLogHelper.logError("SERVICE", "User is NULL");
                } catch (Exception e) {
                    CMNLogHelper.logError("ServiceGETTrainingsFailed", e.getMessage());
                }
            }
        }
    }

    public ArrayList<BETraining> addNewTrainings(ArrayList<BETraining> oldList, ArrayList<BETraining> newList){
        try{
            for (BETraining training: newList){
                if (!oldList.contains(training)){
                    oldList.add(training);
                    DAL.addListenerToTraining(training.getId(), this);
                }
            }
        }
        catch (Exception e){
            CMNLogHelper.logError("FailedUpdateTrainingsList", e.getMessage());
        }

        return oldList;
    }



    private ArrayList<BETraining> joinTrainings(ArrayList<BETraining> myJoinedTrainings, ArrayList<BETraining> myCreatedTrainings) {
        try {
            if (myCreatedTrainings != null && myJoinedTrainings != null) {
                ArrayList<BETraining> allTrainings = myJoinedTrainings;
                for (BETraining training : myCreatedTrainings) {
                    allTrainings.add(training);
                }
                return allTrainings;
            } else if (myCreatedTrainings != null)
                return myCreatedTrainings;
            else if (myJoinedTrainings != null)
                return myJoinedTrainings;
            else
                return null;

        } catch (Exception e) {
            CMNLogHelper.logError("JoinTrainingArraysFailed", e.getMessage());
        }
        return null;
    }


    public BETraining findTrainingByID(String trainingID, ArrayList<BETraining> trainings) {
        try{
            for (BETraining training : trainings) {
                if (training.getId().equals(trainingID)) {
                    return training;
                }
            }
        }
        catch (Exception e){
            CMNLogHelper.logError("findTrainingByIDFailed", e.getMessage());
        }

        return null;
    }

    public void registerToNotifications(ArrayList<BETraining> trainings) {
        try{
            for (BETraining training : trainings) {
                DAL.addListenerToTraining(training.getId(), this);
            }
        }
        catch (Exception e){
            CMNLogHelper.logError("registerToNotificationsFailed", e.getMessage());
        }


    }

    public void setUser(BEUser user) {
        this.user = user;
    }

    public void setTraining(BETraining training) {
        this.training = training;
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
//            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 10);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);

            // If we get killed, after returning from here, restart



            // Tapping the notification will open the specified Activity.
            Intent activityIntent = new Intent(this, LobbyActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // This always shows up in the notifications area when this Service is running.
            // TODO: String localization
            Notification not = new Notification.Builder(this).
                    setContentTitle(getText(R.string.app_name)).
                    setContentInfo("I'm alive...").setSmallIcon(R.drawable.app_notification_icon).
                    setContentIntent(pendingIntent).build();
            startForeground(1, not);

            serverAPI = ServerAPI.getInstance();

            extras = intent.getExtras();

            if (extras == null) {
                CMNLogHelper.logError("GET-EXTRA-OnStart", "is null");
            } else {
                serverAPI.getUser(extras.getString("UserID").toString(), this);
            }

            periodicalUpdateTrainingListThread = PeriodicalUpdateTrainingListThread.getInstance(this);
            new Thread(periodicalUpdateTrainingListThread).start();


        }

        catch (Exception e){
            CMNLogHelper.logError("SERVICE-onStartCommandFailed", e.getMessage());
        }


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

//        Toast.makeText(this, "service killed", Toast.LENGTH_SHORT).show();

    }

}