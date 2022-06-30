package gui3.simulator;

import gui3.internal.Table;
import gui3.pretty.PrettyRecord;
import gui3.pretty.PrettyPrinter;
import gui3.pretty.PrettyTable;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Menu extends Table implements PrettyTable, Serializable {

    private final Map<Long, Dish> entries = new LinkedHashMap<>();

    public Dish getRandomDish() {
        Dish[] dishes;

        synchronized (entries) {
            dishes = entries.values().toArray(Dish[]::new);
        }

        return dishes[(int) (Math.random() * dishes.length)];
    }

    public Dish getDish(long id) {
        return entries.get(id);
    }

    public Dish createDish(String name, String description, int price) {
        final long id = nextId();

        Dish dish = new Dish(id, name, description, price);

        synchronized (entries) {
            entries.put(id, dish);
        }

        return dish;
    }

    public Dish deleteDish(long id) {
        synchronized (entries) {
            return entries.remove(id);
        }
    }

    @Override
    public String toTable() {
        PrettyPrinter table = new PrettyPrinter();

        synchronized (entries) {
            entries.values()
                   .stream()
                   .map(PrettyRecord::toRecord)
                   .forEach(table::addRecord);
        }

        return table.toString();
    }
}
