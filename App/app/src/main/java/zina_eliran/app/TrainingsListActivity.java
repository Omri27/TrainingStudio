package zina_eliran.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.BETrainingAdapter;
import zina_eliran.app.BusinessEntities.BETrainingLevelEnum;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.ClickListener;
import zina_eliran.app.Utils.RecyclerTouchListener;

public class TrainingsListActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView trainingRv;
    List<BETraining> trainingsList = new ArrayList<>();
    BETrainingAdapter trainingAdapter;
    RelativeLayout rv;
    TextView titleTv;
    ImageButton deleteTrainingBtn;
    FloatingActionButton createTrainingFab;
    boolean isManageTrainingPermission;
    boolean isViewMode;
    boolean isPublicTrainingMode;
    boolean isMyTrainingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_list);
        intent = getIntent();
        onCreateUI();
    }

    private void onCreateUI() {
        try {

            initActivityMode();

            initFabButton();

            rv = (RelativeLayout) findViewById(R.id.training_list_relative_layout);
            //call invalidate in order to float the create button in front of the list
            rv.invalidate();

            initDeleteButton();

            initActivityTitle();

            //init adapter & recycle view
            initTrainingAdapter();
            initTrainingRecycleView();

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    private void initActivityMode() {

        try {

            String publicTrainingsMode = getIntentParam(intent, _getString(R.string.training_list_public_mode));
            String myTrainingsMode = getIntentParam(intent, _getString(R.string.training_list_my_trainings_mode));

            if (publicTrainingsMode != null) {
                isPublicTrainingMode = true;
            } else if (myTrainingsMode != null) {
                isMyTrainingMode = true;
            } else {
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
            String hasManageTrainingPermission = getIntentParam(intent, _getString(R.string.manage_training_permission));
            if (hasManageTrainingPermission != null) {
                isManageTrainingPermission = true;
                createTrainingFab.setVisibility(View.VISIBLE);
            } else {
                createTrainingFab.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void initDeleteButton() {
        try {
            deleteTrainingBtn = (ImageButton) findViewById(R.id.training_list_delete_training_btn);
            deleteTrainingBtn.setOnClickListener(this);
            deleteTrainingBtn.setVisibility(View.INVISIBLE);

            //set when long click, do the save when create
            /*String hasManageTrainingPermission = getIntentParam(intent, _getString(R.string.manage_training_permission));
            if (hasManageTrainingPermission != null && isMyTrainingMode) {
                isManageTrainingPermission = true;
                createTrainingFab.setVisibility(View.VISIBLE);
            } else {
                createTrainingFab.setVisibility(View.INVISIBLE);
            }*/

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

    public void initActivityTitle() {
        try {
            titleTv = (TextView) findViewById(R.id.training_list_title_tv);
            String title = getIntentParam(intent, _getString(R.string.training_list_title));
            if (title != null) {
                titleTv.setText(title);
            }
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }
    }

    public void setAdapterTrainingData() {

        try {
            //get data from db here
            //what about listeners for creating new trainings?
        } catch (Exception e) {
            CMNLogHelper.logError("TrainingsListActivity", e.getMessage());
        }


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
        tr.setLevel(BETrainingLevelEnum.mazeRunner);
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
        tr.setLevel(BETrainingLevelEnum.mazeRunner);
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
        tr.setLevel(BETrainingLevelEnum.mazeRunner);
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
        tr.setLevel(BETrainingLevelEnum.mazeRunner);
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
        tr.setLevel(BETrainingLevelEnum.mazeRunner);
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
        tr.setLevel(BETrainingLevelEnum.mazeRunner);
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
        Toast.makeText(getApplicationContext(), "!!!!!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
    }
}
