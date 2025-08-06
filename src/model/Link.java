package model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Link {
    private final List<String> parts;

    public Link(String string) {
        var partsNames = string.split("/");
        parts = new LinkedList<>();
        for (var part : partsNames) {
            if (part.isEmpty()) {
                continue;
            }
            parts.add(part);
        }
    }

    public List<String> getParts() {
        return Collections.unmodifiableList(parts);
    }
}
