package mapper.services.links;

import mapper.model.Link;
import mapper.model.SiteProtocol;

import java.util.List;

public interface ILinksFactory {

    Link createLink(String path);

    String getAbsoluteUrl(Link link);

    String getDomain();

    String getInitialPage();

    String getMainPage();

    SiteProtocol getProtocol();

    void setProtocol(SiteProtocol protocol);

    List<String> getInitialLinkParts();

    boolean trySetInitialParsingPage(String url);
}
