package model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Link {
    private final String value;
    private List<String> parts;

    public Link(String value) {
        this.value = value;
    }

    public List<String> getParts() {
        if (parts == null) {
            var partsNames = value.split("/");
            parts = new LinkedList<>();
            for (var part : partsNames) {
                if (part.isEmpty()) {
                    continue;
                }
                parts.add(part);
            }
        }
        return Collections.unmodifiableList(parts);
    }

    public String getValue() {
        return value;
    }
}
