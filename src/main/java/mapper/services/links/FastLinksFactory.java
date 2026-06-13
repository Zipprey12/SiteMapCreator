package mapper.services.links;

import mapper.model.Link;

public class FastLinksFactory extends RelativeLinksFactory {
    private final ILinksFactory base;

    public FastLinksFactory(ILinksFactory base) {
        this.base = base;
    }

    @Override
    public Link createLink(String url) {
        return base.createLink(stripQueryParams(url));
    }

    @Override
    public String getMainPage() { return base.getMainPage(); }

    @Override
    public String getDomain() { return base.getDomain(); }

    private String stripQueryParams(String url) {
        int i = url.indexOf('?');
        return i == -1 ? url : url.substring(0, i);
    }
}