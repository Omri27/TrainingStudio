package zina_eliran.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BEResponseStatusEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingAdapter;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.Listeners.ClickListener;
import zina_eliran.app.Utils.Listeners.RecyclerTouchListener;

public class TrainingsListActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener {

    RecyclerView trainingRv;
    List<BETraining> trainingsList = new ArrayList<>();
    BETrainingAdapter trainingAdapter;
    RelativeLayout trainingListRl;
    FloatingActionButton createTrainingFab;
    ToggleButton myTrainingListModeTb;
    View myTrainingHeaderView;
    View publicTrainingHeaderView;
    boolean isMyTrainingMode;
    boolean isPublicTrainingMode;
    boolean isMyTrainingJoinMode;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_trainings_list);
            intent = getIntent();
            onCreateUI();
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void onCreateUI() {
        try {

            pBar = (ProgressBar) findViewById(R.id.training_list_pbar);
            pBar.bringToFront();

            initActivityFlags();
            initFabButton();
            sApi.getAllTrainings(this);

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void initActivityFlags() {
        try {
            if (!getIntentParam(intent, _getString(R.string.training_list_public_mode)).isEmpty()) {
                isPublicTrainingMode = true;
            }
            if (!getIntentParam(intent, _getString(R.string.training_list_my_trainings_mode)).isEmpty()) {
                isMyTrainingMode = true;
            }
            if (!getIntentParam(intent, _getString(R.string.training_list_join_mode)).isEmpty()) {
                isMyTrainingJoinMode = true;
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void initFabButton() {
        try {
            createTrainingFab = (FloatingActionButton) findViewById(R.id.fab);
            createTrainingFab.setOnClickListener(this);
            createTrainingFab.bringToFront();

            //allow to create new trainings in public mode only.
            if (isPublicTrainingMode) {
                createTrainingFab.setVisibility(View.VISIBLE);
            } else {
                createTrainingFab.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void initActivityMode() {
        try {

            trainingListRl = (RelativeLayout) findViewById(R.id.training_list_relative_layout);
            //call invalidate in order to float the create button in front of the list
            trainingListRl.invalidate();

            publicTrainingHeaderView = findViewById(R.id.training_list_public_trainings_header_view);
            myTrainingHeaderView = findViewById(R.id.training_list_my_trainings_header_view);
            myTrainingListModeTb = (ToggleButton) findViewById(R.id.training_list_my_training_header_title_tb);
            myTrainingListModeTb.setChecked(false);

            if (isPublicTrainingMode) {
                trainingsList = sApi.getPublicTrainingsList();
                myTrainingHeaderView.setVisibility(View.INVISIBLE);
                publicTrainingHeaderView.setVisibility(View.VISIBLE);

            } else {
                myTrainingHeaderView.setVisibility(View.VISIBLE);
                publicTrainingHeaderView.setVisibility(View.INVISIBLE);
                if (!isMyTrainingJoinMode) {
                    trainingsList = sApi.getMyCreatedTrainingsList();
                    myTrainingListModeTb.setChecked(true);
                } else {
                    trainingsList = sApi.getMyJoinedTrainingsList();
                }
                myTrainingListModeTb.setOnCheckedChangeListener(this);
            }
            if (trainingsList == null) {
                trainingsList = new ArrayList<>();
            }
            trainingAdapter.setTrainingList(trainingsList);
            trainingAdapter.notifyDataSetChanged();


        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void initTrainingRecycleView() {
        try {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

            trainingRv = (RecyclerView) findViewById(R.id.training_list_recycle_view);
            trainingRv.setLayoutManager(mLayoutManager);
            trainingRv.setItemAnimator(new DefaultItemAnimator());
            trainingRv.setAdapter(trainingAdapter);

            trainingRv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), trainingRv, new ClickListener() {
                @Override
                public void onClick(View view, int position) {

                    BETraining training = trainingsList.get(position);
                    //navigate to training details activity
                    Map<String, String> intentParams = new HashMap<>();
                    intentParams.put(_getString(R.string.training_details_training_id), training.getId());

                    if (isMyTrainingMode) {
                        //created mode
                        if (myTrainingListModeTb.isChecked()) {
                            intentParams.put(_getString(R.string.training_details_activity_mode), _getString(R.string.training_details_cancel_mode));
                        }
                        //joined mode
                        else {
                            intentParams.put(_getString(R.string.training_details_activity_mode), _getString(R.string.training_details_leave_mode));
                        }
                    }
                    //public mode
                    else if (isPublicTrainingMode) {
                        intentParams.put(_getString(R.string.training_details_activity_mode), _getString(R.string.training_details_join_mode));
                    }

                    navigateToActivity(TrainingsListActivity.this, TrainingDetailsActivity.class, true, intentParams);

                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void initTrainingAdapter() {
        try {
            trainingAdapter = new BETrainingAdapter(trainingsList);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }


    @Override
    public void onClick(View v) {
        try {
            //navigate to create new training activity
            Map<String, String> intentParams = new HashMap<>();
            intentParams.put(_getString(R.string.training_details_activity_mode), _getString(R.string.training_details_create_mode));
            navigateToActivity(this, TrainingDetailsActivity.class, true, intentParams);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            if (response != null) {
                if (response.getStatus() == BEResponseStatusEnum.error) {
                    CMNLogHelper.logError("TrainingsListActivity", "error in get trainings callback | err:" + response.getMessage());
                    Toast.makeText(_getAppContext(), "Error while retrieving trainings data, please try again later.", Toast.LENGTH_LONG).show();
                    //navigate to lobby
                    navigateToActivity(this, LobbyActivity.class, true, null);
                } else if (response.getEntityType() == BETypesEnum.Trainings) {
                    if (response.getActionType() == DALActionTypeEnum.getAllTrainings) {
                        sApi.updateAppTrainingsData(response.getEntities());

                        initActivityMode();
                        initTrainingAdapter();
                        initTrainingRecycleView();
                        pBar.setVisibility(View.GONE);
                    } else {
                        CMNLogHelper.logError("TrainingsListActivity", "wrong action type in callback" + response.getActionType());
                    }
                } else {
                    CMNLogHelper.logError("TrainingsListActivity", "wrong entity type in callback" + response.getEntityType());
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        try {

            if (myTrainingListModeTb.isChecked()) {
                trainingsList = sApi.getMyCreatedTrainingsList();
            } else {
                trainingsList = sApi.getMyJoinedTrainingsList();
            }

            if (trainingsList == null) {
                trainingsList = new ArrayList<>();
            }
            trainingAdapter.setTrainingList(trainingsList);
            trainingAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sApi.getAllTrainings(this);
        try {
            pBar.setVisibility(View.VISIBLE);
            sApi.getAllTrainings(this);
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }
}
