package mapper.services.map.filler;

import lombok.extern.slf4j.Slf4j;
import mapper.model.Link;
import mapper.model.MapFillerArgs;
import mapper.repository.parsers.LinksParser;
import mapper.services.links.ILinksFactory;
import mapper.services.map.SiteMap;
import org.jsoup.HttpStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static mapper.services.links.LinksFactoryTest.BASE_HTTPS_PAGE;
import static mapper.services.links.LinksFactoryTest.DOMAIN;
import static mapper.services.map.SiteMapTest.CATALOG_PART;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
class SiteMapFillerTest {

    private static final String CATALOG_URL = BASE_HTTPS_PAGE + "/" + CATALOG_PART;

    private LinksParser parser;
    private ILinksFactory linksFactory;
    private SiteMap map;
    private Set<String> visitedLinks;

    @BeforeEach
    void setUp() {
        parser = mock(LinksParser.class);
        linksFactory = mock(ILinksFactory.class);
        map = new SiteMap();
        map.initialize(BASE_HTTPS_PAGE, List.of(DOMAIN));
        visitedLinks = Collections.synchronizedSet(new HashSet<>());

        when(linksFactory.getAbsoluteUrl(any(Link.class)))
                .thenAnswer(inv -> BASE_HTTPS_PAGE + "/" +
                        ((Link) inv.getArgument(0)).getValue());
    }

    private MapFillerArgs buildArgs(int maxLevel, int maxPages) {
        var args = new MapFillerArgs(map, parser, visitedLinks, linksFactory);
        args.setMaxSearchingLevel(maxLevel);
        args.setMaxArticlesCount(maxPages);
        args.setRequestDelayMs(0);
        return args;
    }

    private void run(MapFillerArgs args) {
        try (var pool = new ForkJoinPool()){
            pool.invoke(new SiteMapFiller(args, BASE_HTTPS_PAGE, 0));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Parsed links are added to the map")
    void parsePageAndAddsLinksToMap() throws IOException {
        var args = buildArgs(1, 10);
        when(parser.parse(BASE_HTTPS_PAGE)).thenReturn(List.of(new Link("/catalog")));
        when(parser.parse(CATALOG_URL)).thenReturn(List.of());

        run(args);

        assertThat(map.getMainNode().getByValue(CATALOG_PART)).isNotNull();
    }

    @Test
    @DisplayName("The same page is not visited twice")
    void doNotVisitSamePageTwice() throws IOException {
        var args = buildArgs(2, 10);
        when(parser.parse(BASE_HTTPS_PAGE))
                .thenReturn(List.of(new Link("/catalog"), new Link("/catalog")));
        when(parser.parse(CATALOG_URL)).thenReturn(List.of());

        run(args);

        verify(parser, times(1)).parse(CATALOG_URL);
    }

    @Test
    @DisplayName("maxSearchingLevel=0 prevents child pages from being parsed")
    void respectsMaxSearchingLevel() throws IOException {
        var args = buildArgs(0, 10);
        when(parser.parse(BASE_HTTPS_PAGE)).thenReturn(List.of(new Link("/catalog")));

        run(args);

        verify(parser, never()).parse(CATALOG_URL);
        assertThat(map.getMainNode().getByValue("catalog")).isNotNull();
    }

    @Test
    @DisplayName("maxArticlesCount=1 stops parsing after first page")
    void respectsMaxArticlesCount() throws IOException {
        var args = buildArgs(3, 1);
        when(parser.parse(BASE_HTTPS_PAGE)).thenReturn(List.of(new Link("/catalog")));

        run(args);

        assertThat(args.getProcessedArticlesCount().get()).isEqualTo(1);
        verify(parser, never()).parse(CATALOG_URL);
    }


    @ParameterizedTest(name = "{0} increments failed counter")
    @MethodSource("getIoExceptions")
    void exceptionIncrementsFailedCounter(IOException exception) throws IOException {
        var args = buildArgs(1, 10);
        when(parser.parse(anyString())).thenThrow(exception);

        run(args);

        assertThat(args.getFailedRequestsCount().get()).isEqualTo(1);
    }

    private static Stream<Arguments> getIoExceptions() {
        return Stream.of(
                Arguments.of("SocketException",        new SocketException("Connection reset")),
                Arguments.of("SocketTimeoutException", new SocketTimeoutException("Timed out")),
                Arguments.of("HttpStatusException 429", new HttpStatusException("Too Many Requests",
                        429, BASE_HTTPS_PAGE)),
                Arguments.of("General IOException",    new IOException("Unknown error"))
        );
    }
}
