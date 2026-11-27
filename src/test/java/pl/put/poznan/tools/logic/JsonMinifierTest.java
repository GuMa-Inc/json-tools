package pl.put.poznan.tools.logic;

import org.junit.jupiter.api.Test;
import pl.put.poznan.tools.logic.model.JsonDocument;
import pl.put.poznan.tools.logic.model.JsonLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonMinifierTest {

    private final JsonLoader loader = new JsonLoader();
    private final JsonMinifier minifier = new JsonMinifier();

    @Test
    void removesWhitespaceAndNewlines() {
        JsonDocument doc = loader.load("{\n  \"a\" : 1 ,\n  \"b\" : [ true , null , 3.14 ]\n}");
        assertEquals("{\"a\":1,\"b\":[true,null,3.14]}", minifier.minify(doc));
    }

    @Test
    void preservesSpacesInsideStrings() {
        JsonDocument doc = loader.load("{ \"name\" : \"Jan   Kowalski\" , \"city\" : \"Poznań\" }");
        assertEquals("{\"name\":\"Jan   Kowalski\",\"city\":\"Poznań\"}", minifier.minify(doc));
    }

    @Test
    void rejectsNullDocument() {
        assertThrows(NullPointerException.class, () -> minifier.minify(null));
    }
}