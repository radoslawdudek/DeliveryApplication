package gui3.internal;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Ticks {

    public static LocalTime toTime(long tick) {
        final long seconds = toUnit(tick, TimeUnit.SECONDS) % TimeUnit.DAYS.toSeconds(1);
        return LocalTime.ofSecondOfDay(seconds);
    }

    public static long toTick(long duration, TimeUnit unit) {
        return unit.toSeconds(duration) * Engine.TPS;
    }

    public static long toUnit(long ticks, TimeUnit unit) {
        return unit.convert(ticks / Engine.TPS, TimeUnit.SECONDS);
    }

    public static long ticksElapsed(long tick) {
        return Engine.getInstance().getTick() - tick;
    }
}
