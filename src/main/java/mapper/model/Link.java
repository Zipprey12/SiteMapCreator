package mapper.model;

import lombok.Getter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Link {

    @Getter
    private final String value;
    private List<String> parts;

    public Link(String value) {
        if (value.startsWith("/")) {
            this.value = value.substring(1);
        } else {
            this.value = value;
        }
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
}
