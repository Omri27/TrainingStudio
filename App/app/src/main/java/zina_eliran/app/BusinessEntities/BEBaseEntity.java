package zina_eliran.app.BusinessEntities;

import java.io.Serializable;
import java.util.UUID;

public class BEBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;

    public BEBaseEntity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "BEBaseEntity{" +
                "id='" + id + '\'' +
                '}';
    }
}
