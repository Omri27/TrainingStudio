package zina_eliran.app.BusinessEntities;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import zina_eliran.app.BaseActivity;
import zina_eliran.app.R;

public class BETrainingAdapter extends RecyclerView.Adapter<BETrainingAdapter.TrainingViewHolder> {

    private List<BETraining> trainingList;
    private boolean isDisplayTrainingId = false;

    public List<BETraining> getTrainingList() {
        return trainingList;
    }

    public void setTrainingList(List<BETraining> trainingList) {
        this.trainingList = trainingList;
    }

    public class TrainingViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mainLl;
        public TextView id, description, level, duration, trainingDate, trainingTime, trainingCreationDate, numberOfParticipates, numberOfJoinedParticipates;

        public TrainingViewHolder(View view) {
            super(view);
            mainLl = (LinearLayout)view.findViewById(R.id.training_list_main_ll);
            id = (TextView) view.findViewById(R.id.training_list_training_id_tv);
            description = (TextView) view.findViewById(R.id.training_list_training_description_tv);
            level = (TextView) view.findViewById(R.id.training_list_training_level_tv);
            duration = (TextView) view.findViewById(R.id.training_list_duration_tv);
            trainingDate = (TextView) view.findViewById(R.id.training_list_training_date_tv);
            trainingTime = (TextView) view.findViewById(R.id.training_list_training_time_tv);
            trainingCreationDate = (TextView) view.findViewById(R.id.training_list_creation_date_tv);
            numberOfParticipates = (TextView) view.findViewById(R.id.training_list_num_of_participates);
            numberOfJoinedParticipates = (TextView) view.findViewById(R.id.training_list_num_of_joined_participates);
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

        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
            SimpleDateFormat timeFormatter = new SimpleDateFormat("kk:mm");

            BETraining training = trainingList.get(position);
            if (isDisplayTrainingId) {
                //dev use
                holder.id.setVisibility(View.VISIBLE);
                holder.id.setText(training.getId().substring(training.getId().length() - 7));
            }

            if(training.getTrainingDateTimeCalender().getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                holder.mainLl.setBackgroundColor(Color.parseColor("#6690EE90"));
            }

            holder.description.setText(training.getDescription());
            holder.level.setText((training.getLevel() == null ? "" : training.getLevel().toString()));
            holder.duration.setText(String.valueOf(training.getDuration()) + " Min");
            holder.trainingDate.setText(dateFormatter.format(training.getTrainingDateTimeCalender().getTime()));
            holder.trainingTime.setText(timeFormatter.format(training.getTrainingDateTimeCalender().getTime()));
            holder.trainingCreationDate.setText("CREATION DATE: " + dateFormatter.format(training.getCreationDateTimeCalender().getTime()));
            holder.numberOfParticipates.setText("OF " + training.getMaxNumberOfParticipants());
            holder.numberOfJoinedParticipates.setText("" + training.getCurrentNumberOfParticipants());

        } catch (Exception e) {
            CMNLogHelper.logError("BETrainingAdapter", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return trainingList.size();
    }
}
