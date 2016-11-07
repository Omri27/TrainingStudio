package zina_eliran.app.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import zina_eliran.app.API.DAL;
import zina_eliran.app.API.ServerAPI;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.LobbyActivity;
import zina_eliran.app.R;
import zina_eliran.app.Utils.FireBaseHandler;

public class DBMonitoringService extends Service implements FireBaseHandler{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private BEUser user;
    private BETraining training;

    @Override
    public void onActionCallback(BEResponse response) {
        if (response != null && response.getStatus() != BEResponseStatusEnum.error){
            if (response.getActionType() == DALActionTypeEnum.getUser  && response.getEntities().get(0) != null){
                setUser((BEUser)response.getEntities().get(0));
                Toast.makeText(this, user.toString(), Toast.LENGTH_LONG).show();
                DAL.addListenerToUser(user);

            }
            else if (response.getActionType() == DALActionTypeEnum.getTraining && response.getEntities().get(0) != null){
                setTraining((BETraining)response.getEntities().get(0));
                DAL.addListenerToTraining(training.getId(),this);

            }

            else if (response.getActionType() == DALActionTypeEnum.trainingCancelled){
                sendNotification("Training " + training.getName() + " has been cancelled", NotificationTypeEnum.trainingIsCancelled);
            }

            else if (response.getActionType() == DALActionTypeEnum.trainingFull){
                sendNotification("Training " + training.getName() + " is full", NotificationTypeEnum.trainingIsFull);
            }



        }
    }


    public void setUser(BEUser user){
        this.user = user;
    }

    public void setTraining(BETraining training){
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

        ServerAPI serverAPI = ServerAPI.getInstance();
        serverAPI.getUser("-KVnVQJFbX7DtWIlEAIS", this);
        serverAPI.getTraining("-KVzSvgE-pw_Ver-H2An", this);





// build notification
// the addAction re-use the same intent to keep the example short


        Toast.makeText(this, "service is running", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }


    public boolean shouldSendNotification(NotificationTypeEnum notificationType){
        try{
            if (user!= null){
                if (notificationType == NotificationTypeEnum.trainingIsFull && user.isTrainingFullNotification())
                    return true;
                else if (notificationType == NotificationTypeEnum.trainingIsCancelled && user.isTrainingCancelledNotification())
                    return true;
                else if (notificationType == NotificationTypeEnum.reminder && user.isTrainingRemainderNotification())
                    return true;
                return false;
            }
        } catch (Exception e){
            CMNLogHelper.logError("CheckNotificationFailed", e.getMessage());
        }
        //Zina change this to true
        return true;


    }



    private void sendNotification(String message, NotificationTypeEnum notificationType) {
        if (shouldSendNotification(notificationType)){
            NotificationManager nm = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this);
            Intent notificationIntent = new Intent(this, LobbyActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            //set notification details
            builder.setContentIntent(contentIntent);
            builder.setSmallIcon(R.drawable.app_icon);
            builder.setContentText(message);
            builder.setContentTitle("Training Studio");
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_ALL);

            //send notification
            Notification notification = builder.build();
            nm.notify((int)System.currentTimeMillis(),notification);
        }
    }

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