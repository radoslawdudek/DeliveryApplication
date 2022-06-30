package gui3.simulator;

import gui3.internal.Tickable;
import gui3.internal.Ticks;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Delivery implements Tickable {

    private static final long ONLINE_DELIVERY_TICKS = Ticks.toTick(5, TimeUnit.SECONDS);//Ticks.toTick(2, TimeUnit.MINUTES);
    private static final long OFFLINE_DELIVERY_TICKS = Ticks.toTick(2, TimeUnit.SECONDS);//Ticks.toTick(15, TimeUnit.SECONDS);
    private static final double ORDER_LATE_DISCOUNT = 0.2;

    private final Customers customers;
    private final Workers workers;

    private final OrderHistory previousOrders;
    private final Map<OrderType, Queue<Order>> nextOrders = new HashMap<>();

    private final Map<Long, Order> currentOrders = new HashMap<>();

    public Delivery(Customers customers, Workers workers, OrderHistory previousOrders) {
        this.customers = customers;
        this.workers = workers;

        this.previousOrders = previousOrders;

        this.nextOrders.put(OrderType.OFFLINE, new ArrayDeque<>());
        this.nextOrders.put(OrderType.ONLINE, new ArrayDeque<>());
    }

    public void receiveOrder(Order order) {
        OrderType type = order.getType();
        nextOrders.get(type)
                  .add(order);
    }

    private int computePrice(Order order) {
        final int price = order.getDish().getPrice();
        return order.isLate() ? (int) Math.ceil(price * (1 - ORDER_LATE_DISCOUNT)) : price;
    }

    private void deliverOrder(Order order) {
        int price = computePrice(order);

        if (order.isLate())
            price = customers.acceptLateOrder() ? customers.receiveOrder(order, price) : 0;
        else
            price = customers.receiveOrder(order, price);

        previousOrders.receiveOrder(order, price);
    }

    private void runTick(WorkerType workerType, OrderType orderType, long workTicks) {
        for (Worker worker : workers.getWorkers(workerType)) {
            final long id = worker.getId();
            Order currentOrder = currentOrders.get(id);

            if (currentOrder != null) {
                long currentTicks = currentOrder.getLiveTicks();
                currentTicks--;

                if (currentTicks <= 0) {
                    currentOrders.remove(id);
                    deliverOrder(currentOrder);

                } else {
                    currentOrder.setLiveTicks(currentTicks);
                }

            } else {
                Queue<Order> orders = nextOrders.get(orderType);
                Order order = orders.peek();

                if (order != null) {
                    order.setLiveTicks(workTicks);
                    currentOrders.put(id, order);

                    orders.poll();
                }
            }
        }
    }
    @Override
    public void tick() {
        runTick(WorkerType.WAITER, OrderType.OFFLINE, OFFLINE_DELIVERY_TICKS);
        runTick(WorkerType.DELIVERY_MAN, OrderType.ONLINE, ONLINE_DELIVERY_TICKS);
    }
}
