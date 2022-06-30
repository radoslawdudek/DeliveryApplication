package gui3.simulator;

import gui3.internal.Ticks;

import java.util.List;

public class OfflineOrder extends Order {

    private final int table;

    public OfflineOrder(long id, Dish dish, int table) {
        super(id, OrderType.OFFLINE, dish);
        this.table = table;
    }

    public int getTable() {
        return table;
    }

    @Override
    public List<?> toRecord() {
        return List.of(getId(), getType().name().toLowerCase(), getDish().getId(),  Ticks.toTime(getCreateTick()), isLate(), getTable());
    }
}
