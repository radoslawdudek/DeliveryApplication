package gui3.simulator;

import gui3.internal.Ticks;

import java.util.concurrent.TimeUnit;

public class Customers {

    private static final long ORDER_ON_TIME_MINUTES = 15;
    private static final long ORDER_ON_TIME_TICKS = Ticks.toTick(ORDER_ON_TIME_MINUTES, TimeUnit.MINUTES);

    private static final long ORDER_MIN_TIME_MINUTES = 10;
    private static final double MAX_TIP_SCALE = 0.1;
    private static final double CHANCE_TO_ACCEPT_LATE = 0.5;

    // 50% chance to accept

    public boolean acceptLateOrder() {
        return Math.random() < CHANCE_TO_ACCEPT_LATE;
    }

    // 1 min before = 1%
    // 5 min before = 5%
    // 10 min before = 10%
    // 11-15 min before = 10%
    private int computeTip(int price, long ticksBefore) {
        final long minutesBefore = Ticks.toUnit(ticksBefore, TimeUnit.MINUTES);
        //System.out.println("T " + minutesBefore + " " + ticksBefore);

        double tipFactor;

        tipFactor = minutesBefore / (double) (ORDER_ON_TIME_MINUTES - ORDER_MIN_TIME_MINUTES);
        tipFactor = Math.min(tipFactor, 1d);

        final double tipScale = MAX_TIP_SCALE * tipFactor;
        return (int) Math.floor(price * tipScale);
    }

    public int receiveOrder(Order order, int price) {
        final long ticksElapsed = Ticks.ticksElapsed(order.getCreateTick());
        final long ticksBefore = ORDER_ON_TIME_TICKS - ticksElapsed;

        if (ticksBefore <= 0)
            return price;

        return price + computeTip(price, ticksBefore);
    }
}
