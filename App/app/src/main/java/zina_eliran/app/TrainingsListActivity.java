package zina_eliran.app;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingAdapter;
import zina_eliran.app.BusinessEntities.BETrainingLevelEnum;
import zina_eliran.app.BusinessEntities.BETypesEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.BusinessEntities.DALActionTypeEnum;
import zina_eliran.app.Utils.FireBaseHandler;
import zina_eliran.app.Utils.Listeners.ClickListener;
import zina_eliran.app.Utils.Listeners.RecyclerTouchListener;

public class TrainingsListActivity extends BaseActivity implements View.OnClickListener, FireBaseHandler {

    RecyclerView trainingRv;
    List<BETraining> trainingsList = new ArrayList<>();
    List<BETraining> myJoinedTrainingsList = new ArrayList<>();
    List<BETraining> myCreatedTrainingList = new ArrayList<>();
    BETrainingAdapter trainingAdapter;
    RelativeLayout trainingListRl;
    FloatingActionButton createTrainingFab;
    boolean isMyTrainingMode;
    boolean isPublicTrainingMode;
    boolean isViewMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);
        intent = getIntent();
        onCreateUI();
    }

    private void onCreateUI() {
        try {

            initActivityFlags();

            initFabButton();

            trainingListRl = (RelativeLayout) findViewById(R.id.training_list_relative_layout);
            //call invalidate in order to float the create button in front of the list
            trainingListRl.invalidate();


            //init adapter & recycle view
            initTrainingAdapter();
            initTrainingRecycleView();

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
            if (getIntentParam(intent, _getString(R.string.training_list_manage_training_permission)).isEmpty()) {
                isViewMode = true;
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
                    intentParams.put(_getString(R.string.training_details_edit_mode), "true");
                    intentParams.put(_getString(R.string.training_details_training_id), training.getId());
                    navigateToActivity(TrainingsListActivity.this, TrainingDetailsActivity.class, false, intentParams);

                    Toast.makeText(getApplicationContext(), training.getName() + " is selected!", Toast.LENGTH_SHORT).show();
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
            setAdapterTrainingData();
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void setAdapterTrainingData() {

/*        try {
            //in case we in public trainings mode
            if (!getIntentParam(intent, _getString(R.string.training_list_public_mode)).isEmpty()) {
                sApi.setActionResponse(null);
                sApi.getPublicTrainings(new ArrayList<>());
            } else {
                sApi.setActionResponse(null);
                sApi.getTrainingsByUser(sApi.getAppUser().getId());
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }*/


        BETraining tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(104);
        tr.setName("fdg dg dg    dfg dg gdf");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(55);
        tr.setName("111111111111111");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Pro);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(104);
        tr.setName("fdg dg dg    dfg dg gdf");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(55);
        tr.setName("111111111111111");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Pro);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(104);
        tr.setName("fdg dg dg    dfg dg gdf");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(55);
        tr.setName("111111111111111");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Pro);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(104);
        tr.setName("fdg dg dg    dfg dg gdf");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(55);
        tr.setName("111111111111111");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Pro);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(104);
        tr.setName("fdg dg dg    dfg dg gdf");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(55);
        tr.setName("111111111111111");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Beginner);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(104);
        tr.setName("fdg dg dg    dfg dg gdf");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(55);
        tr.setName("111111111111111");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Beginner);
        trainingsList.add(tr);

        tr = new BETraining();
        tr.setDuration(34);
        tr.setName("sfddsfdsfdsfdsfs");
        tr.setTrainingDate(new Date());
        tr.setLevel(BETrainingLevelEnum.Hobby);
        trainingsList.add(tr);


        trainingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        //navigate to create new training activity
        Map<String, String> intentParams = new HashMap<>();
        intentParams.put(_getString(R.string.training_details_new_mode), "true");
        navigateToActivity(this, TrainingDetailsActivity.class, false, intentParams);
    }

    @Override
    public void onActionCallback(BEResponse response) {
        try {

            if (response != null) {
                if (response.getEntityType() == BETypesEnum.training) {
                    if (response.getActionType() == DALActionTypeEnum.getMyTrainings) {
                        if (isMyTrainingMode) {
/*                            myCreatedTrainingList = response.getEntities()
                                    .stream()
                                    .filter(t -> ((BETraining) t).getCreatorId() == sApi.getAppUser().getId())
                                    .map(object -> (BETraining) object)
                                    .sorted(Comparator.comparing(t -> t.getTrainingDate()))
                                    .collect(Collectors.toList());


                            myJoinedTrainingsList = response.getEntities()
                                    .stream()
                                    .filter(t -> ((BETraining) t).getCreatorId() != sApi.getAppUser().getId())
                                    .map(object -> (BETraining) object)
                                    .sorted(Comparator.comparing(t -> t.getTrainingDate()))
                                    .collect(Collectors.toList());


                            if (myCreatedTrainingList.size() > 0) {
                                trainingsList = myCreatedTrainingList;
                                //set the switch button status here
                            } else {
                                trainingsList = myJoinedTrainingsList;
                                //set the switch button status here
                            }

                            trainingAdapter.notifyDataSetChanged();*/
                        } else {
                            CMNLogHelper.logError("TrainingsListActivity", "wrong action type in get trainings callback - getMyTrainings");
                        }

                    } else if (response.getActionType() == DALActionTypeEnum.getPublicTrainings) {
                        if (isPublicTrainingMode) {
                            //filter out all joined trainings or all created by "me" trainings
/*                            trainingsList = response.getEntities()
                                    .stream()
                                    .filter(t -> !((BETraining) t).isUserParticipateInTraining(sApi.getAppUser().getId()))
                                    .filter(t -> ((BETraining) t).getCreatorId() != sApi.getAppUser().getId())
                                    .map(object -> (BETraining) object)
                                    .collect(Collectors.toList());
                            trainingAdapter.notifyDataSetChanged();*/
                        } else {
                            CMNLogHelper.logError("TrainingsListActivity", "wrong action type in get trainings callback - getPublicTrainings");
                        }
                    } else {
                        CMNLogHelper.logError("TrainingsListActivity", "wrong action type in get trainings callback");
                    }
                } else {
                    CMNLogHelper.logError("TrainingsListActivity", "wrong action type in callback" + response.getEntityType());
                }
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }
}
