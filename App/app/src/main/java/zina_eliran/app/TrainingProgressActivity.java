package zina_eliran.app;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;

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

            initTrainingHorizontalRv();

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

    }
}
