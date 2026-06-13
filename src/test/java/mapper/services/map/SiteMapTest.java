package mapper.services.map;

import mapper.model.Link;
import mapper.model.LinkPartNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mapper.services.links.LinksFactoryTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SiteMapTest {

    public static final String CATALOG_PART = "catalog";
    private SiteMap map;

    @BeforeEach
    void setUp() {
        map = new SiteMap();
        map.initialize(BASE_HTTP_PAGE, List.of(DOMAIN));
    }

    @Test
    @DisplayName("Initialize sets site name from first link part")
    void initializeSetsSiteName() {
        assertThat(map.getSiteName()).isEqualTo(DOMAIN);
    }

    @Test
    @DisplayName("Initialize sets main node value to mainPage")
    void initializeSetsMainNode() {
        assertThat(map.getMainNode()).isNotNull();
        assertThat(map.getMainNode().getValue()).isEqualTo(BASE_HTTP_PAGE);
    }

    @Test
    @DisplayName("Initialize with path creates child nodes")
    void initializeWithPathCreatesChildNodes() {
        var deepMap = new SiteMap();
        deepMap.initialize(BASE_HTTPS_PAGE, List.of(DOMAIN, CATALOG_PART, PAGE_PART));

        var domainNode = deepMap.getMainNode();
        assertThat(domainNode).isNotNull();

        var pageNode = domainNode
                .getByValue(CATALOG_PART)
                .getByValue(PAGE_PART);
        assertThat(pageNode).isNotNull();

        var other = pageNode.getByValue("other");
        assertThat(other).isNull();
    }

    @Test
    @DisplayName("Initialize with empty parts throws IllegalArgumentException")
    void initializeWithEmptyPartsThrows() {
        var siteMap = new SiteMap();
        List<String> list = List.of();

        assertThatThrownBy(() -> siteMap.initialize(BASE_HTTP_PAGE, list))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("AddLink creates node in tree")
    void addLinkCreatesNodeInTree() {
        map.addLink(new Link("/" + CATALOG_PART));

        assertThat(map.getMainNode().getChildren()).hasSize(1);
        assertThat(map.getMainNode().getByValue(CATALOG_PART)).isNotNull();
    }

    @Test
    @DisplayName("AddLink with deep path creates nested nodes")
    void addLinkDeepPathCreatesNestedNodes() {
        map.addLink(new Link("/catalog/phones/page1"));

        var phonesNode = map.getMainNode()
                .getByValue("catalog")
                .getByValue("phones");
        assertThat(phonesNode).isNotNull();
        assertThat(phonesNode.getByValue("page1")).isNotNull();
    }

    @Test
    @DisplayName("AddLink marks leaf node as page")
    void addLinkMarksLeafNodeAsPage() {
        map.addLink(new Link("/catalog/page1"));

        var phonesNode = (LinkPartNode) map.getMainNode()
                .getByValue("catalog")
                .getByValue("page1");
        assertThat(phonesNode.isPage()).isTrue();
    }

    @Test
    void addLinkDoesNotDuplicateExistingNode() {
        map.addLink(new Link("/catalog"));
        map.addLink(new Link("/catalog"));

        assertThat(map.getMainNode().getChildren()).hasSize(1);
    }

    @Test
    @DisplayName("Two links with common prefix share the same parent node")
    void addTwoLinksShareCommonPrefix() {
        map.addLink(new Link("/catalog/page1"));
        map.addLink(new Link("/catalog/page2"));

        var catalogNode = map.getMainNode().getByValue("catalog");
        assertThat(catalogNode).isNotNull();
        assertThat(catalogNode.getChildren()).hasSize(2);
        assertThat(catalogNode.getByValue("page1")).isNotNull();
        assertThat(catalogNode.getByValue("page2")).isNotNull();
    }

    @Test
    void addLinkWithEmptyValueDoNothing() {
        map.addLink(new Link(""));
        assertThat(map.getMainNode().getChildren()).isEmpty();
    }
}
