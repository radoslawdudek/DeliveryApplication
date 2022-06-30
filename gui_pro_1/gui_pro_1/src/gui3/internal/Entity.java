package gui3.internal;

import gui3.pretty.PrettyEntity;

import java.io.Serializable;
import java.util.Objects;

public abstract class Entity implements PrettyEntity, Serializable {

    private final long id;

    public Entity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        Entity entity = (Entity) object;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
