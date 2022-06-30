package gui3.simulator;

import gui3.internal.Tickable;
import gui3.internal.Ticks;

import java.util.concurrent.TimeUnit;

public class Kitchen implements Tickable {

    private static final long ORDER_COMPLETE_TICKS = Ticks.toTick(15, TimeUnit.SECONDS); //Ticks.toTick(30, TimeUnit.SECONDS);

    private final Workers workers;
    private final Delivery delivery;

    private final OrderQueue nextOrders;
    private Order currentOrder = null;

    public Kitchen(Workers workers, Delivery delivery, OrderQueue nextOrders) {
        this.workers = workers;
        this.delivery = delivery;
        this.nextOrders = nextOrders;
    }

    private long scaleSeconds() {
        return (long) Math.floor(Math.pow(Math.log(workers.getWorkers(WorkerType.CHEF).size() + 2), 2));
    }

    private long scaleTicks() {
        return Ticks.toTick(scaleSeconds(), TimeUnit.SECONDS);
    }

    private long nextTicks() {
        return ORDER_COMPLETE_TICKS - scaleTicks();
    }

    @Override
    public void tick() {
        if (currentOrder == null) {
            currentOrder = nextOrders.pollOrder();

            if (currentOrder == null)
                return;

            currentOrder.setLiveTicks(nextTicks());
        }

        long currentTicks = currentOrder.getLiveTicks();
        currentTicks--;

        if (currentTicks <= 0) {
            delivery.receiveOrder(currentOrder);
            currentOrder = null;

        } else {
            currentOrder.setLiveTicks(currentTicks);
        }
    }
}
