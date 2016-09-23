package zina_eliran.app.BusinessEntities;

import android.location.Location;

import java.util.Date;

/**
 * Created by Zina K on 9/10/2016.
 */
public class BETraining extends BEBaseEntity{

    private String name;
    private BETrainingLevelEnum level;
    private int duration;
    private Date trainingDate;
    private Location location;


    public BETraining() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BETrainingLevelEnum getLevel() {
        return level;
    }

    public void setLevel(BETrainingLevelEnum level) {
        this.level = level;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
