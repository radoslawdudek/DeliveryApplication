package gui3.simulator;

import gui3.internal.Entity;

import java.util.List;

public class Worker extends Entity {

    private final WorkerType type;
    private final String name;
    private final String surname;
    private final String phone;

    Worker(long id, WorkerType type, String name, String surname, String phone) {
        super(id);
        this.type = type;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
    }

    public WorkerType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public List<?> toRecord() {
        return List.of(getId(), getType().name().toLowerCase(), getName(), getSurname(), getPhone());
    }
}
