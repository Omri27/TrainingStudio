package zina_eliran.app;

import java.util.UUID;

public class BEBaseEntity {

    private UUID id;

    public BEBaseEntity() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
