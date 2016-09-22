package zina_eliran.app.BusinessEntities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import java.util.List;

import zina_eliran.app.R;

public class BETrainingAdapter extends RecyclerView.Adapter<BETrainingAdapter.TrainingViewHolder> {

    private List<BETraining> trainingList;

    public class TrainingViewHolder extends RecyclerView.ViewHolder {
        public TextView name, level, duration, location, trainingDate;

        public TrainingViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.training_list_training_name_tv);
            level = (TextView) view.findViewById(R.id.training_list_training_level_tv);
            //duration = (TextView) view.findViewById(R.id.);
            //location = (TextView) view.findViewById(R.id.training_list_training_location_tv);
            trainingDate = (TextView) view.findViewById(R.id.training_list_training_date_tv);
        }
    }


    public BETrainingAdapter(List<BETraining> trainingList) {
        this.trainingList = trainingList;
    }

    @Override
    public TrainingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.training_layout_row, parent, false);

        return new TrainingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrainingViewHolder holder, int position) {
        BETraining training = trainingList.get(position);
        holder.name.setText(training.getName());
        holder.level.setText(training.getLevel().toString());
        //holder.location.setText(training.getLocation());
        holder.trainingDate.setText(training.getTrainingDate().toString());
    }

    @Override
    public int getItemCount() {
        return trainingList.size();
    }
}
