package zina_eliran.app;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
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
    List<BETrainingLocation> lastChartLocationRoute = new ArrayList<>();
    boolean isAllowBackButton = true;
    boolean isTrainingEnded = false;
    int locationChangedCount = 0;
    PowerManager.WakeLock wl;

    float avgSpeed = 0;
    long measureCount = 0;
    float maxSpeed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_training_view);

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
            wl.acquire();
            // do your things, even when screen is off

            intent = getIntent();
            chartData = new ArrayList<>();
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

            onCreateUI();
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void onCreateUI() {
        try {
            initActivityElements();
            initActivityMode();
            initChartData();
            setChartDataTest();
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void initActivityMode() {

        try {

            String _activityMode = getIntentParam(intent, _getString(R.string.training_view_activity_mode));
            activityMode = BETrainingViewModeEnum.valueOf(_activityMode);

            if (activityMode == BETrainingViewModeEnum.training_view_run_mode) {
                sApi.getTraining(sApi.getNextTraining().getId(), this);
            } else if (activityMode == BETrainingViewModeEnum.training_view_read_only_mode) {
                sApi.getTraining(getIntentParam(intent, _getString(R.string.training_id_view_mode)), this);
                startTrainingBtn.setEnabled(false);
                endTrainingBtn.setEnabled(false);
            } else {//handle error
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
                gmh = new GoogleMapHandler(this, this, trainingMapFragment, training.getLocation(), 3000, 1000);
            } else {
                //in case of view only
                gmh = new GoogleMapHandler(this, trainingMapFragment, trainingView.getTrainingLocationRoute());
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingDetailsActivity", e.getMessage());
        }

    }

    private void initChartData() {
        try {
            chart = (LineChart) findViewById(R.id.training_view_chart);
            chart.setDescription(new Description());
            Description d = new Description();
            d.setText("");
            chart.setDescription(d);

            chart.setEnabled(false);
            chart.animateY(5000, Easing.EasingOption.EaseOutBack);
            chart.animateX(2000, Easing.EasingOption.EaseOutBack);
            XAxis xAxis = chart.getXAxis();
            YAxis leftYAxis = chart.getAxisLeft();
            YAxis rightYAxis = chart.getAxisRight();
            Legend legend = chart.getLegend();
            chart.disableScroll();
            chart.setTouchEnabled(false);

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            legend.setTextColor(Color.WHITE);
            leftYAxis.setTextColor(Color.WHITE);

            xAxis.setDrawAxisLine(true);
            rightYAxis.setEnabled(false); // no right axis

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void setTrainingStatistics() {
        try {
            //set data into training view object
            List<BETrainingLocation> route = trainingView.getTrainingLocationRoute();
            trainingView.setTrainingCaloriesBurn(sApi.getAppUser());
            trainingView.setActualDuration((int) route.get(0).getTimeMeasureDiff(route.get(route.size() - 1), 3600));
            trainingView.setTotalDistance(route.get(0).getDistance(route.get(route.size() - 1)));
            trainingView.setAvgSpeed(avgSpeed / measureCount);
            trainingView.setMaxSpeed(maxSpeed);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void setChartData(List<BETrainingLocation> locations) {
        try {

            chartData = new ArrayList<>();

            //add those point to the data set
            for (int i = 0; i < locations.size() - 1; i++) {
                measureCount++;
                float distance = locations.get(i).getDistance(locations.get(i + 1));
                float time = locations.get(i).getTimeMeasureDiff(locations.get(i + 1), 3600);
                float speed = (float) (distance / (time + 0.0001));
                avgSpeed += speed;

                if (maxSpeed < speed) {
                    maxSpeed = speed;
                }

                chartData.add(new Entry(time, speed));
                chartDataSet = new LineDataSet(chartData, "Route(M) | Time(m)"); // add entries to dataset
                chartDataSet.setColor(Color.argb(159, 255, 106, 0));
                chartDataSet.setValueTextColor(Color.WHITE);
                chartDataSet.setDrawValues(false);
            }

            chart.notifyDataSetChanged(); // let the chart know it's data changed
            chart.invalidate(); // refresh

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    private void setChartDataTest() {
        try {

            chartData = new ArrayList<>();

            /*int j = 0;
            for (int i = 0; i < 20; i++) {

                measureCount++;
                float distance = (((i + 15) * 7) / 9) * 13;
                float time = (j / 13) * 14;
                float speed = (float) (distance / (time + 0.0001));
                avgSpeed += speed;

                if (maxSpeed < speed) {
                    maxSpeed = speed;
                }

                chartData.add(new Entry(time, speed));
                chartDataSet = new LineDataSet(chartData, "Route | Time"); // add entries to dataset
                chartDataSet.setColor(Color.argb(159, 255, 106, 0));
                chartDataSet.setValueTextColor(Color.WHITE);
                j++;
            }*/

            chartData.add(new Entry(0, 120f));
            chartData.add(new Entry(0.50f, 100f));
            chartData.add(new Entry(0.90f, 90f));
            chartData.add(new Entry(1.10f, 90f));
            chartData.add(new Entry(2.00f, 100f));
   /*         chartData.add(new Entry(2.20f, 100f));
            chartData.add(new Entry(5.30f, 110f));
            chartData.add(new Entry(6.00f, 110f));
            chartData.add(new Entry(6.30f, 110f));
            chartData.add(new Entry(6.880f, 110f));
            chartData.add(new Entry(9.10f, 110f));
            chartData.add(new Entry(12.10f, 70f));
            chartData.add(new Entry(13.50f, 60f));
            chartData.add(new Entry(14.320f, 50f));
            chartData.add(new Entry(15.10f, 70f));
            chartData.add(new Entry(17.70f, 90f));
            chartData.add(new Entry(18.50f, 90f));
            chartData.add(new Entry(19.70f, 90f));
            chartData.add(new Entry(20.70f, 90f));
            chartData.add(new Entry(21.00f, 90f));
            chartData.add(new Entry(23.00f, 90f));
            chartData.add(new Entry(24.00f, 90f));
            chartData.add(new Entry(25.00f, 90f));*/
            chartDataSet = new LineDataSet(chartData, "Route(M) | Time(m)"); // add entries to dataset
            chartDataSet.setColor(Color.argb(159, 255, 106, 0));
            chartDataSet.setValueTextColor(Color.WHITE);
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
            isAllowBackButton = false;

            switch (view.getId()) {
                case R.id.training_view_start_btn:
                    startTrainingBtn.setEnabled(false);
                    trainingView.setTrainingStartDateTimeCalender(Calendar.getInstance());
                    trainingView.setStatus(BETrainingViewStatusEnum.started);
                    trainingView.setUserId(sApi.getAppUser().getId());
                    trainingView.setTrainingId(training.getId());

                    sApi.createTrainingView(trainingView, this);

                    break;
                case R.id.training_view_end_btn:

                    isTrainingEnded = true;
                    endTrainingBtn.setEnabled(false);
                    trainingView.setTrainingEndDateTimeCalender(Calendar.getInstance());
                    gmh.drawOnMap(BETrainingLocation.getLatLngList(trainingView.getTrainingLocationRoute()));
                    gmh.stopListener();
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
                    //navigate to lobby
                    navigateToActivity(this, LobbyActivity.class, true, null);
                } else if (response.getEntityType() == BETypesEnum.Trainings && response.getActionType() == DALActionTypeEnum.getTraining) {
                    training = ((BETraining) response.getEntities().get(0));
                    //bind elements to the object fields
                    trainingDescriptionTv.setText(training.getDescription());
                    trainingLevelTv.setText(("Level: " + training.getLevel().toString()));
                    trainingDateTv.setText("On: " + dateFormatter.format(training.getTrainingDateTimeCalender().getTime()));
                    trainingTimeTv.setText("Start at: " + timeFormatter.format(training.getTrainingDateTimeCalender().getTime()));

                    if (activityMode == BETrainingViewModeEnum.training_view_read_only_mode) {
                        //get training view data in case of read only mode
                        sApi.getTrainingView(sApi.getNextTraining().getId(), sApi.getAppUser().getId(), this);
                    }
                    //after we init the training objects - init the map & location service
                    initTrainingLocation();

                } else if (response.getEntityType() == BETypesEnum.TrainingViewDetails && response.getActionType() == DALActionTypeEnum.getTrainingViewDetails) {

                    trainingView = ((BETrainingViewDetails) response.getEntities().get(0));
                    //initChartData(trainingView.getTrainingLocationRoute());

                } else if (response.getEntityType() == BETypesEnum.TrainingViewDetails && response.getActionType() == DALActionTypeEnum.createTrainingViewDetails) {
                    endTrainingBtn.setEnabled(true);
                } else if (response.getEntityType() == BETypesEnum.TrainingViewDetails && response.getActionType() == DALActionTypeEnum.updateTrainingViewDetails) {
                    if (((BETrainingViewDetails) response.getEntities().get(0)).getStatus() == BETrainingViewStatusEnum.ended) {
                        isAllowBackButton = true;
                        sApi.setNextTraining(null);
                    }
                } else {
                    CMNLogHelper.logError("TrainingViewActivity", "error in training view callbacks | err:" + response.getMessage());
                }
            }

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
                if (isTrainingEnded) {
                    Toast.makeText(this, "Yes!!! Great training, Well Done " + sApi.getAppUser().getName() + "!", Toast.LENGTH_LONG).show();
                }
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
                lastChartLocationRoute.add(trainingLocation);
                locationChangedCount++;

                if (locationChangedCount == 10) {
                    setChartData(lastChartLocationRoute);
                    setTrainingStatistics();

                    //setMaxSpeed
                    trainingMaxSpeedTv.setText("Max speed: " + Float.toString(trainingView.getMaxSpeed()) + " Km/h");
                    //set avgSpeed
                    trainingAvgSpeedTv.setText("Avg speed: " + Float.toString(trainingView.getAvgSpeed()) + " Km/h");
                    //set calories
                    trainingCaloriesTv.setText(Float.toString(trainingView.getTotalCalories()) + " Cal");
                    //set distance
                    trainingDistanceTv.setText("Dist: " + Float.toString(trainingView.getTotalDistance()) + " Km");
                    //set duration
                    trainingDurationTv.setText("Time: " + Float.toString(trainingView.getActualDuration()) + " Hr");
                    gmh.drawOnMap(BETrainingLocation.getLatLngList(lastChartLocationRoute));
                    locationChangedCount = 0;
                    lastChartLocationRoute = new ArrayList<>();

                    sApi.updateTrainingView(trainingView, this);
                }

            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            wl.release();
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingViewActivity", e.getMessage());
        }
    }

}
