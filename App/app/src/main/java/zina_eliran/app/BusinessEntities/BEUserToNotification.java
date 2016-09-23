package zina_eliran.app.BusinessEntities;

/**
 * Created by Zina K on 9/23/2016.
 */

public class BEUserToNotification {
    private String userId;
    private String trainingId;
    private BENotificationTypeEnum notificationType;

    public BEUserToNotification() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public BENotificationTypeEnum getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(BENotificationTypeEnum notificationType) {
        this.notificationType = notificationType;
    }
}
