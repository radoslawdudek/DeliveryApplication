package gui3.internal;

public class Table {

    private long id = 0;

    protected long nextId() {
        return id++;
    }
}
