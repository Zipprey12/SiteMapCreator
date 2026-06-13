package mapper.services.links;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static mapper.services.links.LinksFactoryTest.*;
import static org.assertj.core.api.Assertions.assertThat;

class RelativeLinksFactoryTest {

    private static final String ANOTHER_SITE_LINK = "https://other.com";

    private RelativeLinksFactory factory;

    @BeforeEach
    void setUp() {
        factory = new RelativeLinksFactory();
        factory.trySetInitialParsingPage(DEFAULT_HTTPS_URL);
    }

    @Test
    @DisplayName("Absolute link of same domain returns relative part")
    void absoluteLinkSameDomainReturnsRelativePart() {
        var link = factory.createLink(DEFAULT_HTTPS_URL + "/catalog");
        assertThat(link).isNotNull();
        assertThat(link.getValue()).isEqualTo("page1/catalog");
        assertThat(link.getParts()).hasSize(2);
    }

    @Test
    @DisplayName("Absolute link with deep path returns full relative part")
    void absoluteLinkWithDeepPath() {
        var link = factory.createLink(DEFAULT_HTTPS_URL + "/catalog/phones/iphone");
        assertThat(link).isNotNull();
        assertThat(link.getValue()).isEqualTo("page1/catalog/phones/iphone");
        assertThat(link.getParts()).hasSize(4);
    }

    @Test
    @DisplayName("Absolute link with base page returns null")
    void absoluteLinkRootReturnsNull() {
        assertThat(factory.createLink(BASE_HTTPS_PAGE + "/")).isNull();
        assertThat(factory.createLink(BASE_HTTPS_PAGE)).isNull();
    }

    @Test
    @DisplayName("Absolute link of another domain returns null")
    void absoluteLinkAnotherDomainReturnsNull() {
        assertThat(factory.createLink(ANOTHER_SITE_LINK + "/catalog")).isNull();
    }

    @Test
    @DisplayName("Relative link returns link with value without leading slash")
    void relativeLinkReturnsLink() {
        var link = factory.createLink("/catalog/phones");
        assertThat(link).isNotNull();
        assertThat(link.getValue()).isEqualTo("catalog/phones");
    }

    @Test
    void relativeLinkWithTrailingSlash() {
        assertThat(factory.createLink("/catalog/")).isNotNull();
    }

    @Test
    void relativeLinkWithoutLeadingSlashReturnsNull() {
        assertThat(factory.createLink("catalog")).isNull();
    }

    @Test
    void uppercaseUrlIsNormalized() {
        var link = factory.createLink(DEFAULT_HTTPS_URL.toUpperCase());
        assertThat(link).isNotNull();
        assertThat(link.getValue()).isEqualTo(PAGE_PART);
    }
}
