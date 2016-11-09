package zina_eliran.app.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.LobbyActivity;
import zina_eliran.app.R;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Zina K on 11/9/2016.
 */

public class NotificationSender implements Runnable {
    private BEUser user;
    private BETraining training;
    private NotificationTypeEnum notificationType;
    private Context context;
    private String message;


    public NotificationSender(BEUser user, BETraining training, NotificationTypeEnum notificationType, Context context) {
        this.user = user;
        this.training = training;
        this.notificationType = notificationType;
        this.context = context;
    }


    @Override
    public void run() {
        try {
            sendNotification();
        } catch (Exception e) {
            CMNLogHelper.logError("FailedToSendNotification", e.getMessage());
        }
    }

    public boolean shouldSendNotification() {
        Boolean shouldSend = false;
        try {
            CMNLogHelper.logError("SERVICE", "CHECKIfshouldSend");
            if (user != null && training != null) {

                //Check if should send trainingIsFull notification
                if (notificationType == NotificationTypeEnum.trainingIsFull) {

                    //Send trainingIsFull Notification to training participant
                    if (user.isTrainingFullNotification() && !training.getCreatorId().equals(user.getId()))
                        shouldSend = true;
                        //Send trainingIsFull Notification to training to training creator
                    else if (training.getCreatorId().equals(user.getId()) && training.isTrainingFullNotificationFlag())
                        shouldSend = true;
                }


                //Check if should send trainingIsCalcelled notification - only for participants and not for owners
                else if (notificationType == NotificationTypeEnum.trainingIsCancelled) {
                    if (user.isTrainingCancelledNotification() && !training.getCreatorId().equals(user.getId()))
                        shouldSend = true;
                }


                //Check if should send userJoinedToTraining notification - only for training creators
                else if (notificationType == NotificationTypeEnum.userJoinedToTraining) {
                    if (training.getCreatorId().equals(user.getId()) && training.isJoinTrainingNotificationFlag())
                        shouldSend = true;
                }

                //Check if user subscribed to reminder notification
                else if (notificationType == NotificationTypeEnum.reminder && user.isTrainingRemainderNotification())
                    shouldSend = true;
                CMNLogHelper.logError("SERVICECHECKIfshouldSend", shouldSend.toString());
                return shouldSend;
            }
        } catch (Exception e) {
            CMNLogHelper.logError("CheckNotificationFailed", e.getMessage());
        }

        CMNLogHelper.logError("SERVICECHECKIfshouldSend", shouldSend.toString());
        return shouldSend;
    }


    private void sendNotification() {
        if (shouldSendNotification()) {
            setMessage();
            NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(context);
            Intent notificationIntent = new Intent(context, LobbyActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            //set notification details
            builder.setContentIntent(contentIntent);
            builder.setSmallIcon(R.drawable.app_notification_icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_notification_icon));
            builder.setSmallIcon(R.drawable.app_notification_small_icon);
            builder.setContentText(message);
            builder.setContentTitle("Training Studio");
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_ALL);

            //send notification
            Notification notification = builder.build();
            nm.notify((int) System.currentTimeMillis(), notification);
            if (notificationType == NotificationTypeEnum.reminder){
                TimerThread.addToSentNotifications(training.getId());
            }
        }
    }

    private void setMessage() {
        if (notificationType == NotificationTypeEnum.trainingIsCancelled)
            message = "Training " + training.getName() + " has been cancelled";

        else if (notificationType == NotificationTypeEnum.trainingIsFull)
            message = "Training " + training.getName() + " is full";

        else if (notificationType == NotificationTypeEnum.userJoinedToTraining)
            message = "User joined to training " + training.getName() + ". Current number of participants is " + training.getCurrentNumberOfParticipants();

        else if (notificationType == NotificationTypeEnum.reminder) {
            try {
                android.text.format.DateFormat df = new android.text.format.DateFormat();
                String trainingTime = df.format("HH:mm", training.getTrainingDateTimeCalender()).toString();
                String[] time = trainingTime.split(":");
                int hours = Integer.parseInt(time[0]);
                if (hours == 24)
                    trainingTime = "02:" + time[1];
                if (hours == 23)
                    trainingTime = "02:" + time[1];
                else
                    trainingTime = hours+2 + ":" + time[1];

                CMNLogHelper.logError("Training time", trainingTime);
                message = "Hurry up! The training " + training.getName() + " starts at " + trainingTime;
            } catch (Exception e) {
                CMNLogHelper.logError("FailedGenerateReminderNitofocation", e.getMessage());
            }

        }


    }

}

