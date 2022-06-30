package gui3.app;

import gui3.internal.Engine;
import gui3.simulator.*;

public class Main {

    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.createDish("Spaghetti bolognese", "Tasty italian pasta", 25);
        menu.createDish("Sandwich", "Best sandwich", 12);
        menu.createDish("Hot dog", "Not made from a dog", 8);
        menu.createDish("Protein bar", "To grow muscles", 5);
        menu.createDish("Cheeseburger", "200g ground beef", 35);

        Customers customers = new Customers();

        Workers workers = new Workers();
        workers.createWorker(WorkerType.WAITER, "Barrack", "Obama", "+48546731234");
        workers.createWorker(WorkerType.DELIVERY_MAN, "Tadeusz", "Hulajnoga", "+48662503877");
        workers.createWorker(WorkerType.CHEF, "Mokebe", "Ubuntu", "+48882453123");
        workers.createWorker(WorkerType.CHEF, "Johnny", "Bravo", "+48501702863");

        OrderQueue queue = new OrderQueue();

        for (int i = 0; i < 10; i++) {
            queue.createRandomOrder(menu);

            // optinal delay
            try {
                Thread.sleep(250);
            }
            catch (InterruptedException ignore) {}
        }

        OrderHistory history = new OrderHistory();

        Delivery delivery = new Delivery(customers, workers, history);
        Kitchen kitchen = new Kitchen(workers, delivery, queue);

        Engine engine = Engine.getInstance();

        engine.register(kitchen);
        engine.register(delivery);

        engine.start();

        Console console = new Console(workers, queue, history, menu);
        console.start();
    }
}
