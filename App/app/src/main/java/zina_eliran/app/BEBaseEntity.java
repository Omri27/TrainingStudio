package zina_eliran.app;

public class BEBaseEntity {

    private String id;

    public BEBaseEntity() {

    }

    public BEBaseEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
