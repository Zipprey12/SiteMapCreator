package services.links;

import model.Link;

public class RelativeLinksFactory extends LinksFactory {
    private static final String RELATIVE_NAVIGATION_LINK_PATTERN = "^/((\\S)+/){0,15}\\S+/?$";

    @Override
    public Link createLink(String url) {
        if (getInitialPage() == null) {
            throw new IllegalStateException("To create link initialPage value must be initialized");
        }
        url = normalizeUrl(url);

        var initialPage = getInitialPage();
        if (url.equals(initialPage)) {
            return null;
        }
        if (url.startsWith(initialPage)) {
            return new Link(url.substring(initialPage.length() + 1));
        }
        if (url.matches(RELATIVE_NAVIGATION_LINK_PATTERN)) {
            return new Link(url.substring(1));
        }
        return null;
    }

    @Override
    public String getAbsoluteUrl(Link link) {
        return getInitialPage() + "/" + link.getValue();
    }
}
