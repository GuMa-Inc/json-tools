package pl.put.poznan.tools.logic;

import java.io.UncheckedIOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pl.put.poznan.tools.logic.model.JsonDocument;

public class JsonMinifier {
    private final ObjectMapper mapper = new ObjectMapper();

    public String minify(JsonDocument document) {
        Objects.requireNonNull(document, "document");
        try {
            return mapper.writeValueAsString(document.getRoot());
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException("Failed to serialize JSON document", e);
        }
    }
}
