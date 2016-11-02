package zina_eliran.app.BusinessEntities;

import java.util.ArrayList;

/**
 * Created by eli on 10/09/2016.
 */
public class BEResponse {


    ArrayList<BEBaseEntity> entities;
    BETypesEnum entityType;
    DALActionTypeEnum actionType;
    String message;
    BEResponseStatusEnum status;


    public BEResponse(){

    }

    public BEResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BEResponseStatusEnum status) {
        this.status = status;
    }

    public ArrayList<BEBaseEntity> getEntities() {
        return this.entities;
    }

    public void setEntities(ArrayList<BEBaseEntity> entities) {
        this.entities = entities;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DALActionTypeEnum getActionType() {
        return actionType;
    }

    public void setActionType(DALActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public BETypesEnum getEntityType() {
        return entityType;
    }

    public void setEntityType(BETypesEnum entityType) {
        this.entityType = entityType;
    }

    @Override
    public String toString() {
        return "BEResponse{" +
                "entities=" + entities +
                ", entityType=" + entityType +
                ", actionType=" + actionType +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }
}
