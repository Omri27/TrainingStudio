package zina_eliran.app;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import zina_eliran.app.BusinessEntities.BEBaseEntity;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingViewDetails;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.HorizontalAdapter;

public class TrainingProgressActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener {

    private RecyclerView horizontalRv;
    private List<BETraining> trainingList;
    private List<BEBaseEntity> trainingViewList;
    private HorizontalAdapter horizontalAdapter;

    TextView joinedDateTv;
    TextView trainingsCountTv;
    TextView totalDistanceTv;
    TextView totalCaloriesTv;
    TextView maxSpeedTv;
    TextView avgSpeedTv;

    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_progress);

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

            pBar = (ProgressBar) findViewById(R.id.training_progress_pbar);
            pBar.setVisibility(View.VISIBLE);
            pBar.bringToFront();

            sApi.getAllTrainingViews(this);

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

            if (response != null) {
                if (response.getStatus() == BEResponseStatusEnum.error) {
                    CMNLogHelper.logError("TrainingProgressActivity", "error in get user callback on app load | err:" + response.getMessage());
                    Toast.makeText(_getAppContext(), "Error while retrieving trainings view data, please try again later.", Toast.LENGTH_LONG).show();
                } else if (response.getActionType() == DALActionTypeEnum.getAllTrainings && response.getEntityType() == BETypesEnum.TrainingViewDetails) {

                    //get all trainings data
                    sApi.setMyEndedTrainingsViewList(sApi.filterMyEndedTrainings(sApi.getAppUser().getId(), response.getEntities()));
                    trainingList = sApi.getMyEndedTrainingsList();

                    //bind elements to the object fields
                    bindTrainingViewDetails();

                    pBar.setVisibility(View.GONE);

                } else {
                    CMNLogHelper.logError("TrainingProgressActivity", "wrong action type in callback" + response.getEntityType() + ", " + response.getActionType());
                }
            }


            //get all training views and filter all ended.
            //then - display statistics & scroll
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingProgressActivity", e.getMessage());
        }
    }

    private void bindTrainingViewDetails() {
        try {

            joinedDateTv.setText("Joining Date: " + dateFormatter.format(sApi.getAppUser().getRegisteredDate()));
            trainingsCountTv.setText("Total Trainings: " + sApi.getMyEndedTrainingsList().size());
            totalDistanceTv.setText("Total Distance: " + BETrainingViewDetails.getDistanceSum(sApi.getMyEndedTrainingsViewList()));
            totalCaloriesTv.setText("Total Calories: " + BETrainingViewDetails.getCaloriesSum(sApi.getMyEndedTrainingsViewList()));
            maxSpeedTv.setText("Max speed: " + BETrainingViewDetails.getMaxSpeed(sApi.getMyEndedTrainingsViewList()));
            avgSpeedTv.setText("Average Speed: " + BETrainingViewDetails.getAvgSpeed(sApi.getMyEndedTrainingsViewList()));

            initTrainingHorizontalRv();

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingProgressActivity", e.getMessage());
        }
    }
}
