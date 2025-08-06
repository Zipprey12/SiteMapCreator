package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Link {
    private final List<String> parts;

    public Link(String string){
        var partsNames = string.split("/");
        parts = new ArrayList<>(partsNames.length);
        for(var part : partsNames){
            if(part.isEmpty()){
                continue;
            }
            parts.add(part);
        }
    }

    public List<String> getParts(){
        return Collections.unmodifiableList(parts);
    }
}
