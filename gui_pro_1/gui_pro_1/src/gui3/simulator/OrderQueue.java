package gui3.simulator;

import gui3.internal.Table;
import gui3.pretty.PrettyRecord;
import gui3.pretty.PrettyPrinter;
import gui3.pretty.PrettyTable;
import gui3.internal.Ticks;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class OrderQueue extends Table implements PrettyTable {

    private static final String[] RANDOM_ADDRESS_POOL = {"Obrzeżna 3", "Ciemna 12", "Grodzka 1"};
    private static final Random RANDOM = new Random();

    private static final long ORDER_LATE_TICKS = Ticks.toTick(45, TimeUnit.SECONDS); //Ticks.toTick(15, TimeUnit.MINUTES);

    private final Map<OrderType, Queue<Order>> entries = new LinkedHashMap<>();

    public OrderQueue() {
        this.entries.put(OrderType.OFFLINE, new LinkedList<>());
        this.entries.put(OrderType.ONLINE, new LinkedList<>());
    }

    public Order createRandomOrder(Menu menu) {
        if (RANDOM.nextBoolean())
            return createOfflineOrder(menu.getRandomDish(), RANDOM.nextInt(0, 64));
        else
            return createOnlineOrder(menu.getRandomDish(), RANDOM_ADDRESS_POOL[(int) (Math.random() * RANDOM_ADDRESS_POOL.length)]);
    }
    public Order createOfflineOrder(Dish dish, int table) {
        final long id = nextId();

        Order order = new OfflineOrder(id, dish, table);

        synchronized (entries) {
            entries.get(OrderType.OFFLINE)
                   .add(order);
        }

        return order;
    }

    public Order createOnlineOrder(Dish dish, String address) {
        final long id = nextId();

        Order order = new OnlineOrder(id, dish, address);

        synchronized (entries) {
            entries.get(OrderType.ONLINE)
                   .add(order);
        }

        return order;
    }

    public Order pollLateOrder() {
        synchronized (entries) {
            for (Queue<Order> orders : entries.values()) {
                Iterator<Order> iterator = orders.iterator();

                while (iterator.hasNext()) {
                    Order order = iterator.next();

                    if (Ticks.ticksElapsed(order.getCreateTick()) > ORDER_LATE_TICKS) {
                        iterator.remove();

                        order.setLate(true);
                        return order;
                    }
                }
            }

            return null;
        }
    }
    public Order pollOrder() {
        Order order = pollLateOrder();

        if (order != null)
            return order;

        synchronized (entries) {
            for (Queue<Order> orders : entries.values()) {
                order = orders.poll();

                if (order != null)
                    return order;
            }
        }

        return null;
    }
    @Override
    public String toTable() {
        PrettyPrinter table = new PrettyPrinter();

        synchronized (entries) {
            entries.values()
                   .stream()
                   .flatMap(Queue::stream)
                   .map(PrettyRecord::toRecord)
                   .forEach(table::addRecord);
        }

        return table.toString();
    }
}
