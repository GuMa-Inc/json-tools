package pl.put.poznan.tools.logic.model;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads JSON text into the internal data model ({@link JsonDocument}) using Jackson.
 * <p>
 * Parsing is strict: the input must hold exactly one JSON value, and duplicate
 * keys in an object are rejected rather than silently overwritten.
 * Instances are thread-safe.
 */
public class JsonLoader {

    private final ObjectMapper mapper = JsonMapper.builder()
            .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build();

    /**
     * @param json JSON text
     * @return the loaded document
     * @throws InvalidJsonException if the text is empty or is not valid JSON
     */
    public JsonDocument load(String json) {
        if (json == null) {
            throw new InvalidJsonException("Input is null", null);
        }
        try {
            return toDocument(mapper.readTree(json));
        } catch (JsonProcessingException e) {
            throw invalid(e);
        }
    }

    /**
     * Reads the stream to the end; it is not closed.
     *
     * @throws InvalidJsonException if the content is empty or is not valid JSON
     * @throws UncheckedIOException if reading fails
     */
    public JsonDocument load(InputStream in) {
        try {
            return toDocument(mapper.readTree(in));
        } catch (JsonProcessingException e) {
            throw invalid(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * @throws InvalidJsonException if the file is empty or is not valid JSON
     * @throws UncheckedIOException if the file cannot be read
     */
    public JsonDocument load(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            return load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static JsonDocument toDocument(JsonNode node) {
        // Jackson returns null or MissingNode for empty input
        if (node == null || node.isMissingNode()) {
            throw new InvalidJsonException("Input contains no JSON value", null);
        }
        return new JsonDocument(node);
    }

    private static InvalidJsonException invalid(JsonProcessingException e) {
        JsonLocation loc = e.getLocation();
        String message = "Invalid JSON: " + e.getOriginalMessage();
        if (loc == null) {
            return new InvalidJsonException(message, e);
        }
        return new InvalidJsonException(
                message + " (line " + loc.getLineNr() + ", column " + loc.getColumnNr() + ")",
                loc.getLineNr(), loc.getColumnNr(), e);
    }
}
