package zina_eliran.app;


import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import zina_eliran.app.BusinessEntities.BEResponse;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.Utils.FireBaseHandler;

public class TrainingProgressActivity  extends BaseActivity implements View.OnClickListener, FireBaseHandler, CompoundButton.OnCheckedChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_progress);

        onCreateUI();
    }

    private void onCreateUI() {
        try {


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
