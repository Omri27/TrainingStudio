package zina_eliran.app.Utils;

import java.util.Comparator;

import zina_eliran.app.BusinessEntities.BETraining;

/**
 * Created by eli on 17/11/2016.
 */

public class TrainingComparator implements Comparator<BETraining> {

    @Override
    public int compare(BETraining t1, BETraining t2) {
        return t1.getTrainingDateTimeCalender().before(t2.getCreationDateTimeCalender()) ? 1 : -1;
    }
}
