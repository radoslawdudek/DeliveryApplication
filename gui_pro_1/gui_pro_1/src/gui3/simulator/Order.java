package gui3.simulator;

import gui3.internal.Engine;
import gui3.internal.Entity;

public abstract class Order extends Entity {

    private final OrderType type;
    private final Dish dish;
    private final long createTick;
    private long liveTicks = 0;
    private boolean late = false;

    Order(long id, OrderType type, Dish dish) {
        super(id);
        this.type = type;
        this.dish = dish;
        this.createTick = Engine.getInstance().getTick();
    }

    public OrderType getType() {
        return type;
    }

    public Dish getDish() {
        return dish;
    }

    public long getCreateTick() {
        return createTick;
    }

    public long getLiveTicks() {
        return liveTicks;
    }

    public void setLiveTicks(long liveTicks) {
        this.liveTicks = liveTicks;
    }

    public void setLate(boolean late) {
        this.late = late;
    }

    public boolean isLate() {
        return late;
    }
}
