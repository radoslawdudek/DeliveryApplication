package gui3.pretty;

public interface PrettyEntity extends PrettyRecord, PrettyTable {

    default String toTable() {
        PrettyPrinter printer = new PrettyPrinter();
        printer.addRecord(toRecord());
        return printer.toString();
    }
}
