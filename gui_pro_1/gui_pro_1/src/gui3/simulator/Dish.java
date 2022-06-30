package gui3.simulator;

import gui3.internal.Entity;

import java.util.List;

public class Dish extends Entity {

    private final String name;
    private final String description;
    private final int price;
    private boolean available = true;

    Dish(long id, String name, String description, int price) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public int getPrice() {
        return price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public List<?> toRecord() {
        return List.of(getId(), getName(), getDescription(), getPrice(), isAvailable());
    }
}
