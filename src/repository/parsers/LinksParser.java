package repository.parsers;

import model.Link;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface LinksParser extends Serializable {
    List<Link> parse(String pageAddress) throws IOException;
}
