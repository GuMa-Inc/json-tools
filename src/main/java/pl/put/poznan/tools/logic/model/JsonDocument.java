package pl.put.poznan.tools.logic.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * Internal data model of a loaded JSON document.
 * <p>
 * Wraps the Jackson tree ({@link JsonNode}), which the further operations
 * (minify, prettify, filtering, comparing) work on. Use {@link JsonLoader} to create one.
 * The tree is shared, not copied: operations that change it should work on {@link #copy()}.
 */
public final class JsonDocument {

    private final JsonNode root;

    JsonDocument(JsonNode root) {
        this.root = Objects.requireNonNull(root, "root");
    }

    /** @return the root node of the document */
    public JsonNode getRoot() {
        return root;
    }

    /** @return true if the root is a JSON object */
    public boolean isObject() {
        return root.isObject();
    }

    /** @return true if the root is a JSON array */
    public boolean isArray() {
        return root.isArray();
    }

    /** @return an independent deep copy of this document */
    public JsonDocument copy() {
        return new JsonDocument(root.deepCopy());
    }

    /** Two documents are equal when their JSON values are equal (object key order is ignored). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonDocument)) return false;
        return root.equals(((JsonDocument) o).root);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    /** @return the document as compact JSON text */
    @Override
    public String toString() {
        return root.toString();
    }
}
