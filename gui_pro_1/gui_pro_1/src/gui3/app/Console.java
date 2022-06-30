package gui3.app;

import gui3.internal.Engine;
import gui3.simulator.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Console extends Thread {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    private static final Engine ENGINE = Engine.getInstance();
    private final Workers workers;

    private final OrderQueue queue;
    private final OrderHistory history;

    private Menu menu;

    public Console(Workers workers, OrderQueue queue, OrderHistory history, Menu menu) {
        this.workers = workers;
        this.queue = queue;
        this.history = history;
        this.menu = menu;
    }

    private void checkEngine() {
        if (ENGINE.isRunning())
            throw new IllegalStateException("engine must be stopped");
    }

    private void loadMenu(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream stream = new ObjectInputStream(Files.newInputStream(Paths.get(path)))) {
            menu = (Menu) stream.readObject();
        }
    }

    private void saveMenu(String path) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(Files.newOutputStream(Paths.get(path), StandardOpenOption.CREATE, StandardOpenOption.WRITE))) {
            stream.writeObject(menu);
        }
    }

    public String[] split(String contents) {
        return TOKEN_PATTERN.matcher(contents)
                            .results()
                            .map(result -> result.group(1))
                            .map(result -> {
                                if (result.startsWith("\""))
                                    result = result.substring(1);

                                if (result.endsWith("\""))
                                    result = result.substring(0, result.length() - 1);

                                return result;
                            })
                            .toArray(String[]::new);
    }

    private void show(String[] tokens) {
        switch (tokens[1]) {
            case "menu": {
                System.out.println(menu.toTable());
                break;
            }
            case "worker": {
                Worker worker = workers.getWorker(Long.parseLong(tokens[2]));

                if (worker == null)
                    throw new IllegalArgumentException();

                System.out.println(worker.toTable());
                break;
            }
            case "queue": {
                System.out.println(queue.toTable());
                break;
            }
            case "history": {
                System.out.println(history.toTable());
                break;
            }
            case "revenue": {
                System.out.println(history.getRevenue());
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    private void random(String[] tokens) {
        switch (tokens[1]) {
            case "order":
                System.out.println(queue.createRandomOrder(menu).toTable());
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    private void createOrder(String[] tokens) {
        Dish dish = menu.getDish(Long.parseLong(tokens[3]));

        if (dish == null)
            throw new IllegalArgumentException();

        Order order;

        switch (tokens[2]) {
            case "offline": {
                order = queue.createOfflineOrder(dish, Integer.parseInt(tokens[4]));
                break;
            }
            case "online": {
                order = queue.createOnlineOrder(dish, tokens[4]);
                break;
            }

            default:
                throw new IllegalArgumentException();
        }

        System.out.println(order.toTable());
    }

    private void createWorker(String[] tokens) {
        checkEngine();

        WorkerType type;

        switch (tokens[2]) {
            case "waiter": {
                type = WorkerType.WAITER;
                break;
            }
            case "chef": {
                type = WorkerType.CHEF;
                break;
            }
            case "delivery_man": {
                type = WorkerType.DELIVERY_MAN;
                break;
            }
            default:
                throw new IllegalArgumentException();
        }

        System.out.println(workers.createWorker(type, tokens[3], tokens[4], tokens[5]).toTable());
    }

    private void createDish(String[] tokens) {
        System.out.println(menu.createDish(tokens[2], tokens[3], Integer.parseInt(tokens[4])).toTable());
    }

    private void create(String[] tokens) {
        switch (tokens[1]) {
            case "dish": {
                createDish(tokens);
                break;
            }
            case "order": {
                createOrder(tokens);
                break;
            }
            case "worker": {
                createWorker(tokens);
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    private void deleteDish(String[] tokens) {
        Dish dish = menu.deleteDish(Long.parseLong(tokens[2]));

        if (dish == null)
            throw new IllegalArgumentException();

        System.out.println(dish.toTable());
    }

    private void deleteWorker(String[] tokens) {
        checkEngine();

        Worker worker = workers.deleteWorker(Long.parseLong(tokens[2]));

        if (worker == null)
            throw new IllegalArgumentException();

        if (workers.getWorkers(worker.getType()).size() < 1)
            throw new IllegalStateException("there are no more workers of this type");

        System.out.println(worker.toTable());
    }

    private void delete(String[] tokens) {
        switch (tokens[1]) {
            case "dish": {
                deleteDish(tokens);
                break;
            }
            case "worker": {
                deleteWorker(tokens);
                break;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    private void setDish(String[] tokens) {
        Dish dish = menu.getDish(Long.parseLong(tokens[2]));

        if (dish == null)
            throw new IllegalArgumentException();

        boolean available;

        switch (tokens[3]) {
            case "available": {
                available = true;
                break;
            }
            case "unavailable": {
                available = false;
                break;
            }
            default:
                throw new IllegalArgumentException();
        }

        dish.setAvailable(available);
        System.out.println(dish.toTable());
    }

    private void set(String[] tokens) {
        switch (tokens[1]) {
            case "dish": {
                setDish(tokens);
                break;
            }
            default:
                throw new IllegalStateException();
        }
    }

    private void interpret(String[] tokens) {
        try {
            switch (tokens[0]) {
                case "show":
                    show(tokens);
                    break;

                case "random":
                    random(tokens);
                    break;

                case "create":
                    create(tokens);
                    break;

                case "delete":
                    delete(tokens);
                    break;

                case "load": {
                    loadMenu(tokens[1]);
                    break;
                }
                case "save": {
                    saveMenu(tokens[1]);
                    break;
                }
                case "start": {
                    ENGINE.start();
                    System.out.println("started at tick: " + ENGINE.getTick());
                    break;
                }
                case "stop": {
                    ENGINE.stop();
                    System.out.println("stopped at tick: " + ENGINE.getTick());
                    break;
                }
                case "set": {
                    set(tokens);
                    break;
                }
                default:
                    throw new IllegalArgumentException();
            }
        }
        catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignore) {
            System.out.println("syntax, or argument error...");
        }
        catch (IOException | ClassNotFoundException | IllegalStateException exception) {
            System.out.println("something went wrong... (" + exception + ")");
        }
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            String[] tokens = split(line);
            interpret(tokens);
        }
    }
}
