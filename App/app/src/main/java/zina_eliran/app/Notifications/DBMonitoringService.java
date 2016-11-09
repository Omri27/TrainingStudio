package zina_eliran.app.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import zina_eliran.app.API.DAL;
import zina_eliran.app.API.Listeners.OnTrainingChangeListener;
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
    private static ArrayList<BETraining> myCreatedTrainings;
    private static ArrayList<BETraining> myJoinedTrainings;
    private static ArrayList<BETraining> myTrainings;
    private Bundle extras;
    private static ServerAPI serverAPI;


    @Override
    public void onActionCallback(BEResponse response) {
        if (response != null && response.getStatus() != BEResponseStatusEnum.error) {
            if (response.getActionType() == DALActionTypeEnum.getUser && response.getEntities().get(0) != null) {
                setUser((BEUser) response.getEntities().get(0));
                Toast.makeText(this, user.toString(), Toast.LENGTH_LONG).show();
                DAL.addListenerToUser(user, this);


            } else if (response.getActionType() == DALActionTypeEnum.getTraining && response.getEntities().get(0) != null) {
                setTraining((BETraining) response.getEntities().get(0));
                DAL.addListenerToTraining(training.getId(), this);

            } else if (response.getActionType() == DALActionTypeEnum.trainingCancelled) {
                try {
                    String trainingID = response.getMessage();
                    for (BETraining training : myJoinedTrainings) {
                        if (training.getId().equals(trainingID)) {
//                            sendNotification("Training " + training.getName() + " has been cancelled", NotificationTypeEnum.trainingIsCancelled, user, training);
                            NotificationSender sender = new NotificationSender(user, training, NotificationTypeEnum.trainingIsCancelled, this);
                            new Thread(sender).start();
                        }
                    }
                } catch (Exception e) {
                    CMNLogHelper.logError("SERVICEtrainingCancelledFailed", e.getMessage());
                }

            } else if (response.getActionType() == DALActionTypeEnum.trainingFull) {

                try {
                    String trainingID = response.getMessage();
                    BETraining trainingObjFound = new BETraining();
                    trainingObjFound = findTrainingByID(trainingID, myCreatedTrainings);
                    if (trainingObjFound != null) {
//                        sendNotification("Training " + trainingObjFound.getName() + " is full.", NotificationTypeEnum.trainingIsFull, user, training);
                        NotificationSender sender = new NotificationSender(user, trainingObjFound, NotificationTypeEnum.trainingIsFull, this);
                        new Thread(sender).start();
                    } else {
                        trainingObjFound = findTrainingByID(trainingID, myJoinedTrainings);
                        if (trainingObjFound != null) {
//                            sendNotification("Training " + trainingObjFound.getName() + " is full.", NotificationTypeEnum.trainingIsFull, user, training);
                            NotificationSender sender = new NotificationSender(user, trainingObjFound, NotificationTypeEnum.trainingIsFull, this);
                            new Thread(sender).start();
                        }
                    }

                } catch (Exception e) {
                    CMNLogHelper.logError("SERVICEtrainingFullFailed", e.getMessage());
                }


            } else if (response.getActionType() == DALActionTypeEnum.userRemainderChanged) {
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

            else if (response.getActionType() == DALActionTypeEnum.userCancelledNotificationChanged){
                try{
                    if ((BEUser)response.getEntities().get(0) != null){
                        BEUser userFromResponse = (BEUser)response.getEntities().get(0);
                        user.setTrainingCancelledNotification(userFromResponse.isTrainingCancelledNotification());
                        reminderThread.updateUser(user);
                    }

                }
                catch (Exception e){
                    CMNLogHelper.logError("TrainingCancelledFailed", e.getMessage());
                }

            }

            else if (response.getActionType() == DALActionTypeEnum.userFullNotificationChanged){
                try{
                    if ((BEUser)response.getEntities().get(0) != null){
                        BEUser userFromResponse = (BEUser)response.getEntities().get(0);
                        user.setTrainingCancelledNotification(userFromResponse.isTrainingFullNotification());
                        reminderThread.updateUser(user);
                    }

                }
                catch (Exception e){
                    CMNLogHelper.logError("TrainingFullFailed", e.getMessage());
                }
            }

            else if (response.getActionType() == DALActionTypeEnum.iCreatedTraining){
                try{
                    if (response.getEntities().get(0) != null){
                        BETraining training = (BETraining)response.getEntities().get(0);
                        myCreatedTrainings.add(training);
                        CMNLogHelper.logError("SERVICEMyCreatedTrainingAdded", training.toString());
                        DAL.addListenerToTraining(training.getId(), this);
                        reminderThread.setTrainings(joinTrainings(myJoinedTrainings,myCreatedTrainings));
                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("SERVICEMyCreatedTrainingAddFailed", e.getMessage());
                }

            }

            else if (response.getActionType() == DALActionTypeEnum.iJoinedToTraining){
                try{
                    if (response.getEntities().get(0) != null){
                        BETraining training = (BETraining)response.getEntities().get(0);
                        myJoinedTrainings.add(training);
                        CMNLogHelper.logError("SERVICEMyJoinedTrainingAdded", training.toString());
                        DAL.addListenerToTraining(training.getId(), this);
                        reminderThread.setTrainings(joinTrainings(myJoinedTrainings,myCreatedTrainings));

                    }
                }
                catch (Exception e){
                    CMNLogHelper.logError("SERVICEMyCreatedTrainingAddFailed", e.getMessage());
                }

            }


            else if (response.getActionType() == DALActionTypeEnum.numberOfParticipantsChanged) {
                try {
                    String[] data = response.getMessage().split(";");
                    String trainingID = data[0];
                    Integer numOfJoined = Integer.parseInt(data[1]);
                    for (BETraining training : myCreatedTrainings) {
                        if (training.getId().equals(trainingID)) {
                            if (training.getCurrentNumberOfParticipants() < numOfJoined) {
//                                sendNotification("User joined to training" + training.getName() + ". Current number of participants is " + numOfJoined,
//                                        NotificationTypeEnum.userJoinedToTraining, user, training);
                                training.setCurrentNumberOfParticipants(numOfJoined);
                                NotificationSender sender = new NotificationSender(user, training, NotificationTypeEnum.userJoinedToTraining, this);
                                new Thread(sender).start();
                            }
                        }
                    }
                    CMNLogHelper.logError("SERVICEnumberOfParticipantsChanged", numOfJoined.toString());

                } catch (Exception e) {
                    CMNLogHelper.logError("SERVICENumOfJoinedChangeFailed", e.getMessage());
                }
            } else if (response.getActionType() == DALActionTypeEnum.getAllTrainings) {
                try {
                    if (user != null) {
                        CMNLogHelper.logError("SERVICEPrintResponse", response.toString());
                        myCreatedTrainings = serverAPI.filterMyTrainings(user.getId(), (ArrayList<BEBaseEntity>) response.getEntities(), true);
                        myJoinedTrainings = serverAPI.filterMyTrainings(user.getId(), (ArrayList<BEBaseEntity>) response.getEntities(), false);

//                        CMNLogHelper.logError("SERVICE", "myCreatedTrainings");
//                        for (BETraining training : myCreatedTrainings)
//                            CMNLogHelper.logError("MyTraining", training.toString());
//                        CMNLogHelper.logError("SERVICE", "myJoinedTrainings");
//                        for (BETraining training : myJoinedTrainings)
//                            CMNLogHelper.logError("MyJoinedTraining", training.toString());

                        if (user.isTrainingRemainderNotification() && (myCreatedTrainings != null || myJoinedTrainings != null)) {
                            reminderThread = TimerThread.getInstance(user, joinTrainings(myJoinedTrainings, myCreatedTrainings), this);
                            new Thread(reminderThread).start();
                            CMNLogHelper.logError("SERVICE", "Started reminder thread");
                        }
                        registerToNotifications(joinTrainings(myJoinedTrainings, myCreatedTrainings));
                    } else
                        CMNLogHelper.logError("SERVICE", "User is NULL");
                } catch (Exception e) {
                    CMNLogHelper.logError("ServiceGETTrainingsFailed", e.getMessage());
                }


            }


        }
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
        for (BETraining training : trainings) {
            if (training.getId().equals(trainingID)) {
                return training;
            }
        }
        return null;
    }

    public void registerToNotifications(ArrayList<BETraining> trainings) {
//        OnTrainingChangeListener listener = new OnTrainingChangeListener(this);
        for (BETraining training : trainings) {
//            OnTrainingChangeListener listener = new OnTrainingChangeListener(this, training.getId());
//            DAL.getTrainingsRef().child(training.getId()).addChildEventListener(listener);
            DAL.addListenerToTraining(training.getId(), this);

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
        Toast.makeText(this, "service create done", Toast.LENGTH_LONG).show();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);


        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        Toast.makeText(this, "service start done", Toast.LENGTH_LONG).show();

        extras = intent.getExtras();  // <-- this is undefined?

        if (extras == null) {
            Toast.makeText(this, "Service created... extras still null", Toast.LENGTH_SHORT).show();
            CMNLogHelper.logError("GET-EXTRA-OnStart", "is null");
        } else {
            Toast.makeText(this, extras.getString("UserID"), Toast.LENGTH_SHORT).show();
            CMNLogHelper.logError("GET-EXTRA-OnStart", extras.getString("UserID").toString());
            DAL.getUserByUID(extras.getString("UserID").toString(), this);
        }

        serverAPI = ServerAPI.getInstance();
//        serverAPI.getUser("-KVnVQJFbX7DtWIlEAIS", this);
//        serverAPI.getTraining("-KVzSvgE-pw_Ver-H2An", this);

        serverAPI.getAllTrainings(this);


// build notification
// the addAction re-use the same intent to keep the example short


        Toast.makeText(this, "service is running", Toast.LENGTH_LONG).show();
//        runTimerTask(this);

        return START_STICKY;
    }


//    public boolean shouldSendNotification(NotificationTypeEnum notificationType, BEUser user, BETraining training) {
//        try {
//            CMNLogHelper.logError("SERVICE", "CHECKIfshouldSend");
//            if (user != null && training != null) {
//                Boolean shouldSend = false;
//                //Check if should send trainingIsFull notification
//                if (notificationType == NotificationTypeEnum.trainingIsFull) {
//
//                    //Send trainingIsFull Notification to training participant
//                    if (user.isTrainingFullNotification() && !training.getCreatorId().equals(user.getId()))
//                        shouldSend = true;
//                        //Send trainingIsFull Notification to training to training creator
//                    else if (training.getCreatorId().equals(user.getId()) && training.isTrainingFullNotificationFlag())
//                        shouldSend = true;
//                    CMNLogHelper.logError("SERVICE", "CHECKIfshouldSend " + shouldSend.toString());
//                    return shouldSend;
//                }
//
//
//                //Check if should send trainingIsCalcelled notification - only for participants and not for owners
//                else if (notificationType == NotificationTypeEnum.trainingIsCancelled) {
//                    if (user.isTrainingCancelledNotification() && !training.getCreatorId().equals(user.getId()))
//                        return true;
//                }
//
//
//                //Check if should send userJoinedToTraining notification - only for training creators
//                else if (notificationType == NotificationTypeEnum.userJoinedToTraining) {
//                    if (training.getCreatorId().equals(user.getId()) && training.isJoinTrainingNotificationFlag())
//                        return true;
//                } else if (notificationType == NotificationTypeEnum.reminder && user.isTrainingRemainderNotification())
//                    return true;
//                return false;
//            }
//        } catch (Exception e) {
//            CMNLogHelper.logError("CheckNotificationFailed", e.getMessage());
//        }
//        //Zina change this to true
//        return true;
//
//
//    }
//
//
//    private void sendNotification(String message, NotificationTypeEnum notificationType, BEUser user, BETraining training) {
//        if (shouldSendNotification(notificationType, user, training)) {
//            NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
//            Notification.Builder builder = new Notification.Builder(this);
//            Intent notificationIntent = new Intent(this, LobbyActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//            //set notification details
//            builder.setContentIntent(contentIntent);
//            builder.setSmallIcon(R.drawable.app_notification_icon);
//            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_notification_icon));
//            builder.setSmallIcon(R.drawable.app_notification_small_icon);
//            builder.setContentText(message);
//            builder.setContentTitle("Training Studio");
//            builder.setAutoCancel(true);
//            builder.setDefaults(Notification.DEFAULT_ALL);
//
//            //send notification
//            Notification notification = builder.build();
//            nm.notify((int) System.currentTimeMillis(), notification);
//        }
//    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "service killed", Toast.LENGTH_SHORT).show();

    }

}