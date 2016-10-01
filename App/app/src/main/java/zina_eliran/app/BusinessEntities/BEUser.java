package zina_eliran.app.BusinessEntities;


import android.location.Location;

import java.util.ArrayList;
import java.util.Date;

public class BEUser extends BEBaseEntity {

    private String name;
    private String email;
    private String verificationCode;
    private boolean isActive;
    private boolean isMale;
    private BETrainingLevelEnum trainingLevel;
    private float heigth;
    private float weigth;
    private boolean isPrivateProfile;
    private Location myLocation;
    private Date registeredDate;
    private ArrayList<String> myTrainingIds; //All training that user chas created and user participates
    private boolean isTrainingCancelledNotification;
    private boolean isTrainingFullNotification;
    private boolean isTrainingRemainderNotification;



    public BEUser() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public BETrainingLevelEnum getTrainingLevel() {
        return trainingLevel;
    }

    public void setTrainingLevel(BETrainingLevelEnum trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public float getHeigth() {
        return heigth;
    }

    public void setHeigth(float heigth) {
        this.heigth = heigth;
    }

    public float getWeigth() {
        return weigth;
    }

    public void setWeigth(float weigth) {
        this.weigth = weigth;
    }

    public boolean isPrivateProfile() {
        return isPrivateProfile;
    }

    public void setPrivateProfile(boolean privateProfile) {
        isPrivateProfile = privateProfile;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(Location myLocation) {
        this.myLocation = myLocation;
    }

    public Date getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(Date registeredDate) {
        this.registeredDate = registeredDate;
    }

    public ArrayList<String> getMyTrainingIds() {
        return myTrainingIds;
    }

    public void setMyTrainingIds(ArrayList<String> myTrainingIds) {
        this.myTrainingIds = myTrainingIds;
    }

    public boolean isTrainingCancelledNotification() {
        return isTrainingCancelledNotification;
    }

    public void setTrainingCancelledNotification(boolean trainingCancelledNotification) {
        isTrainingCancelledNotification = trainingCancelledNotification;
    }

    public boolean isTrainingFullNotification() {
        return isTrainingFullNotification;
    }

    public void setTrainingFullNotification(boolean trainingFullNotification) {
        isTrainingFullNotification = trainingFullNotification;
    }

    public boolean isTrainingRemainderNotification() {
        return isTrainingRemainderNotification;
    }

    public void setTrainingRemainderNotification(boolean trainingRemainderNotification) {
        isTrainingRemainderNotification = trainingRemainderNotification;
    }

    @Override
    public String toString() {
        return "BEUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", verificationCode='" + verificationCode + '\'' +
                ", isActive=" + isActive +
                '}';
    }


    public void addTrainingToTrainingList(String trainingId){
        this.myTrainingIds.add(trainingId);
    }


    public void removeTrainingFromUser(String trainingID) {
        if (myTrainingIds.contains(trainingID))
            myTrainingIds.remove(trainingID);
    }

}
