package zina_eliran.app.BusinessEntities;

/**
 * Created by Zina K on 9/23/2016.
 */

//trainingCancelled - owner cancelled manually
//training full - all available places are full
//participantJoined - sent notification to owner if have new participant
public enum BENotificationTypeEnum {
    trainingCancelled, trainingFull, participantJoined
}
