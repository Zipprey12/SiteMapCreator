package services.links;

import model.Link;

public class RelativeLinksFactory extends LinksFactory{
    private static final String RELATIVE_NAVIGATION_LINK_PATTERN = "^/((\\S)+/){0,15}\\S+/?$";

    @Override
    public Link createLink(String url) {
        if(getInitialPage() == null){
            throw new IllegalStateException("To create link initialPage value must be initialized");
        }
        url = normalizeUrl(url);

        var initialPage = getInitialPage();
        var index = url.indexOf(initialPage);
        if(index == 0){
            return new Link(url.substring(initialPage.length()));
        }
        if(url.matches(RELATIVE_NAVIGATION_LINK_PATTERN)){
            return new Link(url);
        }
        return null;
    }

    @Override
    public String getAbsoluteUrl(Link link){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getInitialPage());
        stringBuilder.append("/");

        for (var part: link.getParts()){
            stringBuilder.append(part);
            stringBuilder.append('/');
        }
        return stringBuilder.toString();
    }
}
