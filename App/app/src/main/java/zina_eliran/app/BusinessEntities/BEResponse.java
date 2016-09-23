package zina_eliran.app.BusinessEntities;

import java.util.ArrayList;

/**
 * Created by eli on 10/09/2016.
 */
public class BEResponse {

    BEResponseStatusEnum status;
    ArrayList<BEBaseEntity> entity;
    private String message;


    public BEResponse(){

    }

    public BEResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BEResponseStatusEnum status) {
        this.status = status;
    }

    public ArrayList<BEBaseEntity> getEntity() {
        return entity;
    }

    public void setEntity(ArrayList<BEBaseEntity> entity) {
        this.entity = entity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}