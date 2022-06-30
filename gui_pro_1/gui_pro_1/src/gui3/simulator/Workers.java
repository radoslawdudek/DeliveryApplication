package gui3.simulator;

import gui3.internal.Table;
import gui3.pretty.PrettyRecord;
import gui3.pretty.PrettyPrinter;

import java.util.*;
public class Workers extends Table {

    private final Map<WorkerType, Map<Long, Worker>> entries = new HashMap<>();

    public Workers() {
        this.entries.put(WorkerType.WAITER, new HashMap<>());
        this.entries.put(WorkerType.CHEF, new HashMap<>());
        this.entries.put(WorkerType.DELIVERY_MAN, new HashMap<>());
    }

    public Worker createWorker(WorkerType type, String name, String surname, String phone) {
        final long id = nextId();

        Worker worker = new Worker(id, type, name, surname, phone);

        synchronized(entries) {
            entries.get(type)
                   .put(id, worker);
        }

        return worker;
    }

    public Worker deleteWorker(long id) {
        Worker worker = null;

        synchronized (entries) {
            for (Map<Long, Worker> workers : entries.values()) {;
                Worker currentWorker = workers.remove(id);

                if (worker == null)
                    worker = currentWorker;
            }
        }

        return worker;
    }

    public Worker getWorker(long id) {
        synchronized (entries) {
            for (Map<Long, Worker> workers : entries.values()) {
                Worker worker = workers.get(id);

                if (worker != null)
                    return worker;
            }
        }

        return null;
    }

    public Collection<Worker> getWorkers(WorkerType type) {
        synchronized (entries) {
            return entries.get(type)
                          .values();
        }
    }

    @Override
    public String toString() {
        PrettyPrinter table = new PrettyPrinter();

        synchronized (entries) {
            entries.values()
                   .stream()
                   .map(Map::values)
                   .flatMap(Collection::stream)
                   .map(PrettyRecord::toRecord)
                   .forEach(table::addRecord);
        }

        return table.toString();
    }
}
