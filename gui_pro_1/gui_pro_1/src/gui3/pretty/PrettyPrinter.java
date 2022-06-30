package gui3.pretty;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PrettyPrinter {

    private static final String EMPTY_TABLE = "(empty)";

    private final List<List<?>> tableObjects = new ArrayList<>();

    public void addRecord(List<?> recordObjects) {
        tableObjects.add(recordObjects);
    }

    private <T> Stream<Stream<T>> transpose(List<List<T>> table) {
        final OptionalInt columns = table.stream()
                                         .mapToInt(List::size)
                                         .max();

        List<Iterator<T>> iterators = table.stream()
                                           .map(List::iterator)
                                           .toList();

        return IntStream.range(0, columns.getAsInt())
                        .mapToObj(ignore -> iterators.stream()
                                                     .filter(Iterator::hasNext)
                                                     .map(Iterator::next));
    }

    private String addPadding(String item, int width) {
        final int length = width - item.length();
        return item + " ".repeat(length);
    }

    @Override
    public String toString() {
        if (tableObjects.isEmpty())
            return EMPTY_TABLE;

        List<List<String>> tableStrings = tableObjects.stream()
                .map(recordString -> recordString.stream()
                                                 .map(Object::toString)
                                                 .toList())
                .toList();

        List<Integer> tableWidths = transpose(tableStrings)
                .map(column -> column.mapToInt(String::length)
                                     .max()
                                     .getAsInt())
                .toList();

        int delimiterLineWidth = tableWidths.stream()
                                            .mapToInt(Integer::valueOf)
                                            .sum();
        delimiterLineWidth += (tableWidths.size() - 1) * 3 + 2;

        String delimiterLine = "+" + "-".repeat(delimiterLineWidth) + "+";

        StringJoiner lineJoiner = new StringJoiner("\n" + delimiterLine + "\n");

        tableStrings.stream()
                .map(recordStrings -> IntStream.range(0, recordStrings.size())
                                               .mapToObj(i -> addPadding(recordStrings.get(i), tableWidths.get(i))))
                .map(recordStrings -> {
                    StringJoiner itemJoiner = new StringJoiner(" | ");
                    recordStrings.forEach(itemJoiner::add);
                    return "| " + itemJoiner + " |";
                })
                .forEach(lineJoiner::add);

        return delimiterLine + "\n" + lineJoiner + "\n" + delimiterLine;
    }
}
