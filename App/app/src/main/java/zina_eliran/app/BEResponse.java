package zina_eliran.app;

/**
 * Created by eli on 10/09/2016.
 */
public class BEResponse {

    BEResponseStatusEnum status;
    BEBaseEntity entity;

    public BEResponse(){

    }

    public BEResponse(BEResponseStatusEnum status, BEBaseEntity entity) {
        this.status = status;
        this.entity = entity;
    }

    public BEResponseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BEResponseStatusEnum status) {
        this.status = status;
    }

    public BEBaseEntity getEntity() {
        return entity;
    }

    public void setEntity(BEBaseEntity entity) {
        this.entity = entity;
    }
}
