package gui3.simulator;

import gui3.internal.Ticks;

import java.util.List;

public class OnlineOrder extends Order {

    private final String address;

    OnlineOrder(long id, Dish dish, String address) {
        super(id, OrderType.ONLINE, dish);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public List<?> toRecord() {
        return List.of(getId(), getType().name().toLowerCase(), getDish().getId(),  Ticks.toTime(getCreateTick()), isLate(), getAddress());
    }
}
