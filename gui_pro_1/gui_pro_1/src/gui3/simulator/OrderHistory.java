package gui3.simulator;

import gui3.pretty.PrettyRecord;
import gui3.pretty.PrettyPrinter;
import gui3.pretty.PrettyTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OrderHistory implements Iterable<Order>, PrettyTable {

    private final List<Order> entries = new ArrayList<>();
    private int revenue = 0;

    public void receiveOrder(Order order, int price) {
        synchronized (entries) {
            entries.add(order);
            revenue += price;
        }
    }

    public int getRevenue() {
        return revenue;
    }

    @Override
    public Iterator<Order> iterator() {
        synchronized (entries) {
            return entries.iterator();
        }
    }
    @Override
    public String toTable() {
        PrettyPrinter table = new PrettyPrinter();

        synchronized (entries) {
            entries.stream()
                   .map(PrettyRecord::toRecord)
                   .forEach(table::addRecord);
        }

        return table.toString();
    }
}
