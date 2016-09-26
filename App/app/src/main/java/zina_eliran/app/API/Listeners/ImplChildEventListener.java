package zina_eliran.app.API.Listeners;


import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

/**
 * Created by Zina K on 9/23/2016.
 */

public class ImplChildEventListener implements ChildEventListener {
    private ReadDataTypeEnum dataType;

    public ImplChildEventListener(ReadDataTypeEnum readDataType){
        dataType = readDataType;
    }



    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
