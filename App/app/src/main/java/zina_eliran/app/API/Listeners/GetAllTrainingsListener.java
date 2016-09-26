package zina_eliran.app.API.Listeners;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.CMNLogHelper;

/**
 * Created by Zina K on 9/24/2016.
 */

public class GetAllTrainingsListener implements ValueEventListener {
    private ArrayList<BETraining> allTrainings;

    public GetAllTrainingsListener(){
        allTrainings = new ArrayList<>();
    }


    public ArrayList<BETraining> getAllTrainings() {
        return allTrainings;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        allTrainings = new ArrayList<>();
        CMNLogHelper.logError("All trainings count ", ""+dataSnapshot.getChildrenCount());
        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
            BETraining t = postSnapshot.getValue(BETraining.class);
            allTrainings.add(t);
        }
//        for (int i = 0; i < allTrainings.size(); i++)
//                    CMNLogHelper.logError("All trainings", allTrainings.get(i).toString());
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
