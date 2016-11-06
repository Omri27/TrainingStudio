package zina_eliran.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.List;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingDetailsModeEnum;
import zina_eliran.app.BusinessEntities.BETrainingLocation;
import zina_eliran.app.BusinessEntities.BETrainingViewDetails;
import zina_eliran.app.BusinessEntities.BETrainingViewModeEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.GoogleMapHandler;

public class TrainingViewActivity extends BaseActivity
        implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener,
        SensorEventListener {

    TextView trainingDescriptionTv;
    TextView trainingDateTv;
    TextView trainingTimeTv;
    TextView trainingLevelTv;
    TextView trainingDurationTv;
    TextView trainingAvgSpeedTv;
    TextView trainingMaxSpeedTv;
    TextView trainingCaloriesTv;
    TextView trainingDistanceTv;

    Button endTrainingBtn;
    Button startPauseTrainingBtn;


    LineChart chart;
    LineDataSet chartDataSet;
    LineData chartLineData;

    GoogleMapHandler gmh;
    MapFragment trainingMapFragment;
    SensorManager sensorManager;
    Sensor sensor;
    float altitude = 0;


    BETrainingViewModeEnum activityMode;
    BETraining training;
    BETrainingViewDetails trainingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_view);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        onCreateUI();
    }

    private void onCreateUI() {
        try {
            initActivityElements();
            initActivityMode();
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void initActivityMode() {

        try {

            if (activityMode != BETrainingViewModeEnum.training_view_run_mode ||
                    activityMode != BETrainingViewModeEnum.training_view_read_only_mode) {
                sApi.getTraining(getIntentParam(intent, _getString(R.string.training_view_training_id)), this);
                //sApi.getTrainingView(getIntentParam(intent, _getString(R.string.training_view_training_id)), sApi.getAppUser().getId(), this);
            } else {
                //handle error
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }

    }

    private void initActivityElements() {
        try {

            trainingDescriptionTv = (TextView) findViewById(R.id.training_view_description_tv);
            trainingDateTv = (TextView) findViewById(R.id.training_view_date_tv);
            trainingTimeTv = (TextView) findViewById(R.id.training_view_time_tv);
            trainingLevelTv = (TextView) findViewById(R.id.training_view_level_tv);
            trainingDurationTv = (TextView) findViewById(R.id.training_view_duration_tv);
            trainingAvgSpeedTv = (TextView) findViewById(R.id.training_view_avg_speed_tv);
            trainingMaxSpeedTv = (TextView) findViewById(R.id.training_view_max_speed_tv);
            trainingCaloriesTv = (TextView) findViewById(R.id.training_view_calories_tv);
            trainingDistanceTv = (TextView) findViewById(R.id.training_view_Distance_tv);

            endTrainingBtn = (Button) findViewById(R.id.training_view_end_btn);
            startPauseTrainingBtn = (Button) findViewById(R.id.training_view_action_btn);

            endTrainingBtn.setOnClickListener(this);
            startPauseTrainingBtn.setOnClickListener(this);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void initTrainingRouteMap() {
        try {

            trainingMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.training_view_location_map_f);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void initTrainingLocation(ArrayList<BETrainingLocation> trainingLocations) {
        try {
            gmh = new GoogleMapHandler(this, trainingMapFragment, trainingLocations);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }

    }

    private void initChartData(ArrayList<BETrainingLocation> trainingLocations) {
        try {
            chart = (LineChart) findViewById(R.id.training_view_chart);
            chartLineData = new LineData(chartDataSet);
            chart.setData(chartLineData);
            chart.invalidate(); // refresh the chart
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        try {

            switch (view.getId()) {
                case R.id.training_view_action_btn:
                    break;
                case R.id.training_view_end_btn:
                    break;
            }


        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try {
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            if (response != null) {
                if (response.getStatus() == BEResponseStatusEnum.error) {
                    CMNLogHelper.logError("TrainingViewActivity", "error in training details callbacks | err:" + response.getMessage());
                    Toast.makeText(_getAppContext(), "Error while performing training view action, please try again later.", Toast.LENGTH_LONG).show();

                } else if (response.getEntityType() == BETypesEnum.Trainings && response.getActionType() == DALActionTypeEnum.getTraining) {
                    training = ((BETraining) response.getEntities().get(0));
                    //bind elements to the object fields
                    trainingDescriptionTv.setText(training.getDescription());
                    trainingLevelTv.setText(("Level: " + training.getLevel().toString()));
                    trainingDurationTv.setText(training.getDuration() + " Min");
                    trainingDateTv.setText(dateFormatter.format(training.getTrainingDateTimeCalender().getTime()));
                    trainingTimeTv.setText(timeFormatter.format(training.getTrainingDateTimeCalender().getTime()));

                } else if (response.getEntityType() == BETypesEnum.Trainings && response.getActionType() == DALActionTypeEnum.getTrainingViewDetails) {

                    trainingView = ((BETrainingViewDetails) response.getEntities().get(0));
                    trainingMaxSpeedTv.setText(trainingView.getMaxSpeed() + "");
                    trainingAvgSpeedTv.setText(trainingView.getAvgSpeed() + "");
                    trainingCaloriesTv.setText(trainingView.getTotalCalories() + "");
                    initTrainingLocation(trainingView.getTrainingLocationRoute());
                    initChartData(trainingView.getTrainingLocationRoute());

                } else {
                    CMNLogHelper.logError("TrainingViewActivity", "error in training view callbacks | err:" + response.getMessage());
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void setChartData() {
        try {

            List<Entry> entries = new ArrayList<>();

            //for (YourData data : dataObjects) {

            // turn your data into Entry objects
            //entries.add(new Entry(data.getValueX(), data.getValueY()));
            //}

            chartDataSet = new LineDataSet(entries, "Label"); // add entries to dataset
            //chartDataSet.setColor(...);
            //chartDataSet.setValueTextColor(...); // styling, ...
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            float presure = sensorEvent.values[0];
            altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, presure);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onResume() {

        super.onResume();
        try {
            if (sensor != null)
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        // your code.

//!!!!!!!!!!!!!!!!!!
        //TODO Eliran
        //handle "end training if need here before close

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
