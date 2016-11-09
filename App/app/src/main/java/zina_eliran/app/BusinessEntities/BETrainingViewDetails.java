package zina_eliran.app.BusinessEntities;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.jar.Pack200;

public class BETrainingViewDetails extends BEBaseEntity {

    private String trainingId;
    private String userId;
    private Calendar trainingStartDateTimeCalender;
    private Calendar trainingEndDateTimeCalender;
    private ArrayList<BETrainingLocation> trainingLocationRoute;
    private BETrainingViewStatusEnum status;
    private float avgSpeed;
    private float maxSpeed;
    private float totalDistance;
    private int totalCalories;
    private int actualDuration;


    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Calendar getTrainingStartDateTimeCalender() {
        return trainingStartDateTimeCalender;
    }

    public void setTrainingStartDateTimeCalender(Calendar trainingStartDateTimeCalender) {
        this.trainingStartDateTimeCalender = trainingStartDateTimeCalender;
    }

    public Calendar getTrainingEndDateTimeCalender() {
        return trainingEndDateTimeCalender;
    }

    public void setTrainingEndDateTimeCalender(Calendar trainingEndDateTimeCalender) {
        this.trainingEndDateTimeCalender = trainingEndDateTimeCalender;
    }

    public ArrayList<BETrainingLocation> getTrainingLocationRoute() {
        return trainingLocationRoute;
    }

    public void setTrainingLocationRoute(ArrayList<BETrainingLocation> trainingLocationRoute) {
        this.trainingLocationRoute = trainingLocationRoute;
    }

    public void addTrainingLocationRoute(BETrainingLocation trainingLocation) {
        if(this.trainingLocationRoute == null){
            this.trainingLocationRoute = new ArrayList<>();
        }
        this.trainingLocationRoute.add(trainingLocation);
    }

    public BETrainingViewStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BETrainingViewStatusEnum status) {
        this.status = status;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    @Override
    public String toString() {
        return "BETrainingViewDetails{" +
                "trainingId='" + trainingId + '\'' +
                ", userId='" + userId + '\'' +
                ", trainingStartDateTimeCalender=" + trainingStartDateTimeCalender +
                ", trainingEndDateTimeCalender=" + trainingEndDateTimeCalender +
                ", trainingLocationRoute=" + trainingLocationRoute +
                ", status=" + status +
                ", avgSpeed=" + avgSpeed +
                ", maxSpeed=" + maxSpeed +
                ", totalDistance=" + totalDistance +
                ", totalCalories=" + totalCalories +
                '}';
    }

    public int getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(int actualDuration) {
        this.actualDuration = actualDuration;
    }

    public int setTrainingCaloriesBurn(BEUser user){
        float poundFactor = 22/10;
        float distanceFactor = (10/16)*this.totalDistance;
        float speedFactor = (10/8)*this.avgSpeed;
        float calManFactor = 63/100;
        float calWomanFactor = 57/100;
        return  (int)(user.getWeigth()* poundFactor*(user.isMale() ? calManFactor : calWomanFactor)*speedFactor*distanceFactor);

    }
}
