package mapper.services.links;

import mapper.model.SiteProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

public class LinksFactoryTest {

    public static final String DOMAIN = "some.domain.com";
    public static final String PAGE_PART = "page1";
    public static final String BASE_HTTPS_PAGE = "https://" + DOMAIN;
    public static final String BASE_HTTP_PAGE = "http://" + DOMAIN;

    public static final String DEFAULT_HTTPS_URL = BASE_HTTPS_PAGE + "/" + PAGE_PART;
    public static final String DEFAULT_HTTP_URL = BASE_HTTP_PAGE + "/" + PAGE_PART;

    private RelativeLinksFactory factory;

    @BeforeEach
    void setUp() {
        factory = new RelativeLinksFactory();
    }

    @Test
    @DisplayName("Test set https initial page")
    void parseHttpsUrl() {
        assertThat(factory.trySetInitialParsingPage(DEFAULT_HTTPS_URL)).isTrue();
        assertThat(factory.getProtocol()).isEqualTo(SiteProtocol.HTTPS);
        assertThat(factory.getDomain()).isEqualTo(DOMAIN);
        assertThat(factory.getMainPage()).isEqualTo("https://some.domain.com");
        assertThat(factory.getInitialPage()).isEqualTo(DEFAULT_HTTPS_URL);
    }

    @Test
    @DisplayName("Test set http initial page")
    void parseHttpUrl() {
        assertThat(factory.trySetInitialParsingPage(DEFAULT_HTTP_URL)).isTrue();
        assertThat(factory.getProtocol()).isEqualTo(SiteProtocol.HTTP);
        assertThat(factory.getDomain()).isEqualTo(DOMAIN);
        assertThat(factory.getMainPage()).isEqualTo("http://some.domain.com");
        assertThat(factory.getInitialPage()).isEqualTo(DEFAULT_HTTP_URL);
    }

    @Test
    @DisplayName("Test set initial page without protocol")
    void parsesUrlWithoutProtocol() {
        var url = DOMAIN + "/" + PAGE_PART;
        assertThat(factory.trySetInitialParsingPage(url)).isTrue();
        assertThat(factory.getProtocol()).isNull();
        assertThat(factory.getInitialPage()).isNull();
        assertThat(factory.getMainPage()).isNull();
        assertThat(factory.getDomain()).isEqualTo(DOMAIN);
    }

    @Test
    void normalizesUrlToLowercase() {
        assertThat(factory.trySetInitialParsingPage("HTTPS://EXAMPLE.COM")).isTrue();
        assertThat(factory.getDomain()).isEqualTo("example.com");
    }

    @Test
    void returnsFalseForEmptyUrl() {
        assertThat(factory.trySetInitialParsingPage("")).isFalse();
    }

    @Test
    @DisplayName("Test set protocol after initialPage")
    void setProtocolBuildsMainAndInitialPage() {
        var url = DOMAIN + "/" + PAGE_PART;
        factory.trySetInitialParsingPage(url);
        factory.setProtocol(SiteProtocol.HTTPS);

        assertThat(factory.getMainPage()).isEqualTo("https://" + DOMAIN);
        assertThat(factory.getInitialPage()).isEqualTo("https://" + url);
    }

    @Test
    void setProtocolNullDoesNotThrow() {
        factory.trySetInitialParsingPage(DOMAIN);
        assertThatCode(() -> factory.setProtocol(null)).doesNotThrowAnyException();
        assertThat(factory.getProtocol()).isNull();
    }

    @Test
    void initialLinkPartsForSimpleDomain() {
        factory.trySetInitialParsingPage(DEFAULT_HTTPS_URL);
        var parts = factory.getInitialLinkParts();
        assertThat(parts).hasSize(2);
        assertThat(parts.getFirst()).isEqualTo(DOMAIN);
        assertThat(parts.get(1)).isEqualTo(PAGE_PART);
    }

    @Test
    void initialLinkPartsEmptyBeforeParsing() {
        assertThat(factory.getInitialLinkParts()).isEmpty();
    }
}
