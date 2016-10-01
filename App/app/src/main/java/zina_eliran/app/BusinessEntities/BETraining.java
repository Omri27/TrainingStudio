package zina_eliran.app.BusinessEntities;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zina K on 9/10/2016.
 */
public class BETraining extends BEBaseEntity{
    private String creatorId;
    private String name;
    private BETrainingLevelEnum level;
    private int duration;
    private Date trainingDate;
    private Location location;
    private int maxNumberOfParticipants;
    private int currentNumberOfParticipants;
    private BETrainingStatusEnum status;
    private boolean isJoinTrainingNotificationFlag;
    private boolean isTrainingFullNotificationFlag;
    private ArrayList<String> patricipatedUserIds;



    public BETraining() {
        super();
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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

    public int getMaxNumberOfParticipants() {
        return maxNumberOfParticipants;
    }

    public void setMaxNumberOfParticipants(int maxNumberOfParticipants) {
        this.maxNumberOfParticipants = maxNumberOfParticipants;
    }

    public int getCurrentNumberOfParticipants() {
        return currentNumberOfParticipants;
    }

    public void setCurrentNumberOfParticipants(int currentNumberOfParticipants) {
        this.currentNumberOfParticipants = currentNumberOfParticipants;
    }

    public BETrainingStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BETrainingStatusEnum status) {
        this.status = status;
    }

    public ArrayList<String> getPatricipatedUserIds() {
        return patricipatedUserIds;
    }

    public void setPatricipatedUserIds(ArrayList<String> patricipatedUserIds) {
        this.patricipatedUserIds = patricipatedUserIds;
    }

    public boolean isJoinTrainingNotificationFlag() {
        return isJoinTrainingNotificationFlag;
    }

    public void setJoinTrainingNotificationFlag(boolean joinTrainingNotificationFlag) {
        isJoinTrainingNotificationFlag = joinTrainingNotificationFlag;
    }

    public boolean isTrainingFullNotificationFlag() {
        return isTrainingFullNotificationFlag;
    }

    public void setTrainingFullNotificationFlag(boolean trainingFullNotificationFlag) {
        isTrainingFullNotificationFlag = trainingFullNotificationFlag;
    }

    @Override
    public String toString() {
        return super.toString() + "BETraining{" +
                "creatorId='" + creatorId + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", trainingDate=" + trainingDate +
                '}';
    }

    public void addUserToUsersList(String userId){
        this.patricipatedUserIds.add(userId);
    }

    public void removeUserFromTraining(String userID) {
        if (patricipatedUserIds.contains(userID))
            patricipatedUserIds.remove(userID);
    }

    public boolean isUserParticipateInTraining(String userId) {
        return getPatricipatedUserIds().contains(userId);
    }
}
