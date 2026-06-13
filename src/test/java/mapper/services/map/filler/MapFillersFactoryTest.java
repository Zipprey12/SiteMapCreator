package mapper.services.map.filler;

import mapper.model.ParsingType;
import mapper.repository.parsers.LinksParser;
import mapper.services.links.RelativeLinksFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MapFillersFactoryTest {

    private static final ForkJoinPool POOL = new ForkJoinPool();

    private LinksParser parser;
    private MapFillersFactory factory;

    private static Stream<Arguments> getTypes() {
        return Stream.of(
                Arguments.of(ParsingType.FAST),
                Arguments.of(ParsingType.EXTENDED)
        );
    }

    @BeforeEach
    void setUp() {
        parser = mock(LinksParser.class);
        var linksFactory = new RelativeLinksFactory();
        factory = new MapFillersFactory(parser, linksFactory, POOL);
    }

    @Test
    @DisplayName("Default settings are applied on creation")
    void defaultSettings() {
        assertThat(factory.getMaxSearchingLevel()).isEqualTo(3);
        assertThat(factory.getMaxArticlesCount()).isEqualTo(3_000);
        assertThat(factory.getRequestDelayMs()).isEqualTo(150);
        assertThat(factory.getParsingType()).isEqualTo(ParsingType.EXTENDED);
    }

    @ParameterizedTest(name = "setParsingType {0} delegates to parser and updates field")
    @MethodSource("getTypes")
    void setParsingTypeFastDelegatesToParser(ParsingType type) {
        factory.setParsingType(type);

        verify(parser).setParsingType(type);
        assertThat(factory.getParsingType()).isEqualTo(type);
    }
}
