package zina_eliran.app.Notifications;

import android.content.Context;

import java.util.ArrayList;

import zina_eliran.app.API.DAL;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BEUser;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;

/**
 * Created by Zina K on 11/10/2016.
 */

public class PeriodicalUpdateTrainingListThread implements Runnable {
    private FireBaseHandler fireBaseHandler;
    private boolean isRunning = true;
    private static PeriodicalUpdateTrainingListThread instance;


    protected PeriodicalUpdateTrainingListThread(FireBaseHandler fireBaseHandler) {
        this.fireBaseHandler = fireBaseHandler;
    }

    public static synchronized PeriodicalUpdateTrainingListThread getInstance(FireBaseHandler fireBaseHandler) {
        if (instance == null) {
            instance = new PeriodicalUpdateTrainingListThread(fireBaseHandler);
        }

        return instance;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void stopRunning() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                CMNLogHelper.logError("PeriodicalThread", "started running");
                DAL.getAllTrainings(fireBaseHandler);
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
