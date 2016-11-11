package zina_eliran.app;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.HorizontalAdapter;

public class TrainingProgressActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener {

    private RecyclerView horizontalRv;
    private List<BETraining> trainingList;
    private HorizontalAdapter horizontalAdapter;

    TextView joinedDateTv;
    TextView trainingsCountTv;
    TextView totalDistanceTv;
    TextView totalCaloriesTv;
    TextView maxSpeedTv;
    TextView avgSpeedTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_progress);

        //test!
        trainingList = sApi.getMyJoinedTrainingsList();

        onCreateUI();

    }

    private void onCreateUI() {
        try {

            joinedDateTv = (TextView) findViewById(R.id.training_progress_joining_date_tv);
            trainingsCountTv = (TextView) findViewById(R.id.training_progress_trainings_count_tv);
            totalDistanceTv = (TextView) findViewById(R.id.training_progress_total_distance_tv);
            totalCaloriesTv = (TextView) findViewById(R.id.training_progress_total_calories_tv);
            maxSpeedTv = (TextView) findViewById(R.id.training_progress_max_speed_tv);
            avgSpeedTv = (TextView) findViewById(R.id.training_progress_avg_speed_tv);

            initTrainingHorizontalRv();
            sApi.getAllTrainingViewsByUserId(sApi.getAppUser().getId(), this);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingProgressActivity", e.getMessage());
        }
    }

    private void initTrainingHorizontalRv() {
        try {

            horizontalRv = (RecyclerView) findViewById(R.id.training_progress_horizontal_rv);
            horizontalAdapter = new HorizontalAdapter(trainingList, this);

            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            horizontalRv.setLayoutManager(horizontalLayoutManagaer);
            horizontalRv.setAdapter(horizontalAdapter);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingProgressActivity", e.getMessage());
        }
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            //get all training views and filter all ended.
            //then - display statistics & scroll
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingProgressActivity", e.getMessage());
        }
    }
}
