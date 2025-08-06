package repository.parsers;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface SiteParser extends Serializable {
    List<String> parse(String pageAddress) throws IOException;
}
