package mapper.services.links;

import mapper.model.Link;

public class RelativeLinksFactory extends LinksFactory {
    private static final String RELATIVE_NAVIGATION_LINK_PATTERN = "^/((\\S)+/){0,15}\\S+/?$";

    @Override
    public Link createLink(String url) {
        url = normalizeUrl(url);

        var mainPart = getMainPage();

        if (url.startsWith(mainPart)) {
            String relative = url.substring(mainPart.length());
            if (relative.equals("/") || relative.isEmpty()) {
                return null;
            }
            return new Link(relative);
        }

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return null;
        }

        if (url.matches(RELATIVE_NAVIGATION_LINK_PATTERN)) {
            return new Link(url);
        }
        return null;
    }

    @Override
    public String getAbsoluteUrl(Link link) {
        return getMainPage() + "/" + link.getValue();
    }
}
