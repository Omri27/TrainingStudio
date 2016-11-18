package zina_eliran.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zina_eliran.app.BusinessEntities.BEAddressPartsEnum;
import zina_eliran.app.BusinessEntities.BETraining;
import zina_eliran.app.BusinessEntities.CMNLogHelper;
import zina_eliran.app.R;
import zina_eliran.app.TrainingViewActivity;


public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    public List<BETraining> trainingList;
    public Activity activity;
    DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
    DateFormat timeFormatter = new SimpleDateFormat("kk:mm"); //kk = 1-24

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout leftLl;
        public LinearLayout centerLl;
        public LinearLayout rightLl;
        public LinearLayout connectorLl;
        public TextView cityTv;
        public TextView dateHourTv;
        public TextView levelTv;
        public View firstView;

        public MyViewHolder(View view) {
            super(view);

            leftLl = (LinearLayout) view.findViewById(R.id.horizontal_item_left_ll);
            centerLl = (LinearLayout) view.findViewById(R.id.horizontal_item_center_ll);
            rightLl = (LinearLayout) view.findViewById(R.id.horizontal_item_right_ll);
            connectorLl = (LinearLayout) view.findViewById(R.id.horizontal_item_connector_ll);
            cityTv = (TextView) view.findViewById(R.id.horizontal_item_city_tv);
            dateHourTv = (TextView) view.findViewById(R.id.horizontal_item_date_hour_tv);
            levelTv = (TextView) view.findViewById(R.id.horizontal_item_level_tv);
            firstView = (View) view.findViewById(R.id.horizontal_item_first_view);
        }
    }


    public HorizontalAdapter(List<BETraining> trainingList, Activity activity) {
        this.trainingList = trainingList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            BETraining currTraining = trainingList.get(position);

            if (position > 0) {
                holder.firstView.setVisibility(View.GONE);
            }

            if (position == trainingList.size() - 1) {
                if (trainingList.size() != 1) {
                    holder.rightLl.setVisibility(View.GONE);
                }
                holder.connectorLl.setVisibility(View.GONE);
            }

            holder.cityTv.setText("City: " + currTraining.getLocation().getAddressPart(activity, BEAddressPartsEnum.city));
            holder.dateHourTv.setText("Date: " + dateFormatter.format(currTraining.getTrainingDateTimeCalender().getTime()) +
                    " | " + timeFormatter.format(currTraining.getTrainingDateTimeCalender().getTime()));
            holder.levelTv.setText("Level: " + currTraining.getLevel());

            holder.centerLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        //navigate to view activity
                        Intent openActivity = new Intent(activity, TrainingViewActivity.class);
                        openActivity.putExtra("training_view_activity_mode", "training_view_read_only_mode");
                        openActivity.putExtra("training_id_view_mode", trainingList.get(position).getId());
                        openActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(openActivity);

                    } catch (Exception e) {
                        CMNLogHelper.logError("HorizontalAdapter", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            CMNLogHelper.logError("HorizontalAdapter", e.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return trainingList.size();
    }
}
