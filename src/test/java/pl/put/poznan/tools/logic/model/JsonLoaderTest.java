package pl.put.poznan.tools.logic.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonLoaderTest {

    private final JsonLoader loader = new JsonLoader();

    @Test
    void loadsNestedObject() {
        JsonDocument doc = loader.load("{\"name\":\"Jan\",\"age\":21,\"tags\":[\"a\",\"b\"],\"address\":{\"city\":\"Poznań\"}}");

        assertTrue(doc.isObject());
        assertEquals("Jan", doc.getRoot().get("name").asText());
        assertEquals(21, doc.getRoot().get("age").asInt());
        assertEquals(2, doc.getRoot().get("tags").size());
        assertEquals("Poznań", doc.getRoot().at("/address/city").asText());
    }

    @Test
    void loadsArrayAndScalarRoots() {
        assertTrue(loader.load("[1, 2, 3]").isArray());
        assertTrue(loader.load("\"text\"").getRoot().isTextual());
        assertTrue(loader.load("null").getRoot().isNull());
    }

    @Test
    void keepsDecimalPrecision() {
        JsonDocument doc = loader.load("{\"v\": 0.1000000000000000055511151231257827}");
        assertEquals(new BigDecimal("0.1000000000000000055511151231257827"), doc.getRoot().get("v").decimalValue());
    }

    @Test
    void reportsPositionOfSyntaxError() {
        InvalidJsonException e = assertThrows(InvalidJsonException.class,
                () -> loader.load("{\n  \"a\": 1,\n  \"b\": }"));
        assertEquals(3, e.getLine());
        assertTrue(e.getColumn() > 0);
    }

    @Test
    void rejectsEmptyInput() {
        assertThrows(InvalidJsonException.class, () -> loader.load(""));
        assertThrows(InvalidJsonException.class, () -> loader.load("   "));
        assertThrows(InvalidJsonException.class, () -> loader.load((String) null));
    }

    @Test
    void rejectsTrailingContent() {
        assertThrows(InvalidJsonException.class, () -> loader.load("{} {}"));
        assertThrows(InvalidJsonException.class, () -> loader.load("[1] x"));
    }

    @Test
    void rejectsDuplicateKeys() {
        assertThrows(InvalidJsonException.class, () -> loader.load("{\"a\":1,\"a\":2}"));
    }

    @Test
    void loadsFromStreamAndFile(@TempDir Path dir) throws Exception {
        String json = "{\"x\": [true, false]}";
        JsonDocument fromString = loader.load(json);

        JsonDocument fromStream = loader.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        assertEquals(fromString, fromStream);

        Path file = dir.resolve("in.json");
        Files.write(file, json.getBytes(StandardCharsets.UTF_8));
        assertEquals(fromString, loader.load(file));
    }

    @Test
    void missingFileThrowsUncheckedIo(@TempDir Path dir) {
        assertThrows(UncheckedIOException.class, () -> loader.load(dir.resolve("missing.json")));
    }

    @Test
    void equalityIgnoresKeyOrder() {
        assertEquals(loader.load("{\"a\":1,\"b\":2}"), loader.load("{\"b\":2,\"a\":1}"));
        assertNotEquals(loader.load("[1,2]"), loader.load("[2,1]"));
    }

    @Test
    void copyIsIndependent() {
        JsonDocument doc = loader.load("{\"a\":1}");
        JsonDocument copy = doc.copy();
        ((com.fasterxml.jackson.databind.node.ObjectNode) copy.getRoot()).put("a", 2);

        assertEquals(1, doc.getRoot().get("a").asInt());
        assertEquals(2, copy.getRoot().get("a").asInt());
    }
}
