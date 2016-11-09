package zina_eliran.app.BusinessEntities;

/**
 * Created by eli on 26/09/2016.
 */

public enum DALActionTypeEnum {
    registerUser,
    updateUser,
    getUser,
    createTraining,
    getTraining,
    joinTraining,
    leaveTraining,
    updateTraining,
    getPublicTrainings,
    getMyTrainings,
    trainingCancelled,
    trainingFull,
    getAllTrainings,
    getTrainingViewDetails,
    createTrainingViewDetails,
    updateTrainingViewDetails,
    userRemainderChanged,
    userFullNotificationChanged,
    userCancelledNotificationChanged,
    numberOfParticipantsChanged,
    iCreatedTraining,
    iJoinedToTraining
}
