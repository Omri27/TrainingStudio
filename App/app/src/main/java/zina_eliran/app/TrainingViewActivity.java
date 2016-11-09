package zina_eliran.app;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.List;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingLocation;
import zina_eliran.app.BusinessEntities.BETrainingViewDetails;
import zina_eliran.app.BusinessEntities.BETrainingViewModeEnum;
import zina_eliran.app.BusinessEntities.BETrainingViewStatusEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.AppLocationChangedHandler;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.GoogleMapHandler;

public class TrainingViewActivity extends BaseActivity
        implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener,
        AppLocationChangedHandler {

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
    Button startTrainingBtn;

    LineChart chart;
    LineDataSet chartDataSet;
    LineData chartLineData;

    GoogleMapHandler gmh;
    MapFragment trainingMapFragment;
    SensorManager sensorManager;
    Sensor sensor;

    BETrainingViewModeEnum activityMode;
    List<Entry> chartData;
    BETraining training;
    BETrainingViewDetails trainingView = new BETrainingViewDetails();
    boolean isAllowBackButton = true;
    int locationChangedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_view);

        intent = getIntent();
        chartData = new ArrayList<>();
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

            String _activityMode = getIntentParam(intent, _getString(R.string.training_view_activity_mode));
            activityMode = BETrainingViewModeEnum.valueOf(_activityMode);

            if (activityMode != BETrainingViewModeEnum.training_view_run_mode ||
                    activityMode != BETrainingViewModeEnum.training_view_read_only_mode) {
                sApi.getTraining(sApi.getNextTraining().getId(), this);
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
            startTrainingBtn = (Button) findViewById(R.id.training_view_start_btn);
            trainingMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.training_view_location_map_f);


            //set events
            endTrainingBtn.setOnClickListener(this);
            endTrainingBtn.setEnabled(false);
            startTrainingBtn.setOnClickListener(this);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void initTrainingLocation() {
        try {
            if (activityMode == BETrainingViewModeEnum.training_view_run_mode) {
                gmh = new GoogleMapHandler(this, this, trainingMapFragment, training.getLocation(), 100, 50);
            } else {
                //in case of view only
                gmh = new GoogleMapHandler(this, trainingMapFragment, trainingView.getTrainingLocationRoute());
                sApi.getTrainingView(sApi.getNextTraining().getId(), sApi.getAppUser().getId(), this);
            }
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

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(8000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onClick(View view) {
        try {

            switch (view.getId()) {
                case R.id.training_view_start_btn:
                    startTrainingBtn.setEnabled(false);
                    isAllowBackButton = false;
                    trainingView.setStatus(BETrainingViewStatusEnum.started);
                    endTrainingBtn.setEnabled(true);

                    break;
                case R.id.training_view_end_btn:

                    //TODO Eliran remove after zina push
                    isAllowBackButton = true;
                    endTrainingBtn.setEnabled(false);
                    gmh.drawOnMap(BETrainingLocation.getLatLngList(trainingView.getTrainingLocationRoute()));
                    gmh.stopListener();
                    //calculate all here:
                    //max speed

                    //avg speed

                    //distance
                    trainingView.setTotalDistance(BETrainingLocation.getLocationRouteDistance(trainingView.getTrainingLocationRoute(), true));
                    trainingDistanceTv.setText(trainingView.getTotalDistance() + " Km");

                    //calories

                    //actual duration
                    trainingView.setActualDuration((int)BETrainingLocation.getLocationRouteDuration(trainingView.getTrainingLocationRoute(), 3600));
                    trainingDurationTv.setText(trainingView.getActualDuration() + " Hr");
                    //status
                    trainingView.setStatus(BETrainingViewStatusEnum.ended);

                    sApi.updateTrainingView(trainingView, this);
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

                    //TODO Eliran - remove this after zina push
                    //after we init the training objects - init the map & location service
                    trainingView = new BETrainingViewDetails();
                    initTrainingLocation();

                } else if (response.getEntityType() == BETypesEnum.Trainings && response.getActionType() == DALActionTypeEnum.getTrainingViewDetails) {

                    trainingView = ((BETrainingViewDetails) response.getEntities().get(0));
                    trainingMaxSpeedTv.setText(trainingView.getMaxSpeed() + "");
                    trainingAvgSpeedTv.setText(trainingView.getAvgSpeed() + "");
                    trainingCaloriesTv.setText(trainingView.getTotalCalories() + "");
                    initChartData(trainingView.getTrainingLocationRoute());

                    //after we init the training objects - init the map & location service
                    initTrainingLocation();
                } else if (response.getEntityType() == BETypesEnum.Trainings && response.getActionType() == DALActionTypeEnum.updateTrainingViewDetails) {
                    isAllowBackButton = true;
                } else {
                    CMNLogHelper.logError("TrainingViewActivity", "error in training view callbacks | err:" + response.getMessage());
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }


    private void setChartData(BETrainingLocation l1, BETrainingLocation l2) {
        try {


            float distance = BETrainingLocation.getDistance(l1, l2);
            float time = BETrainingLocation.getTimeMeasureDiff(l1, l2, 3600);
            float speed = (float)(distance/(time + 0.0001));


            chartData.add(new Entry(time, speed));
            chartDataSet = new LineDataSet(chartData, "Route | Time"); // add entries to dataset
            chartDataSet.setColor(Color.argb(159, 255, 106, 0));
            chartDataSet.setValueTextColor(Color.WHITE);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (!isAllowBackButton && trainingView.getStatus() == BETrainingViewStatusEnum.started) {  //we don't allow user to go back
                Toast.makeText(this, "Please End the training or wait until we save your training data successfully.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Yes!!! Great training, Well Done " + sApi.getAppUser().getName() + "!", Toast.LENGTH_LONG).show();
                super.onBackPressed(); // Process Back key default behavior.
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                // your code
                onBackPressed();
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocationChangedCallback(Location location) {
        try {
            if (location != null && trainingView.getStatus() == BETrainingViewStatusEnum.started) {
                BETrainingLocation trainingLocation = new BETrainingLocation();
                trainingLocation = new BETrainingLocation();
                trainingLocation.setLatitude(location.getLatitude());
                trainingLocation.setLongitude(location.getLongitude());
                trainingLocation.setAltitude(location.getAltitude());

                trainingView.addTrainingLocationRoute(trainingLocation);

                locationChangedCount++;

                int listSize = trainingView.getTrainingLocationRoute().size();
                if (locationChangedCount == 2) {
                    //setChartData(trainingView.getTrainingLocationRoute().get(listSize-2),trainingView.getTrainingLocationRoute().get(listSize-1));
                    gmh.drawOnMap(BETrainingLocation.getLatLngList(trainingView.getTrainingLocationRoute()));
                    locationChangedCount = 0;
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }
}
