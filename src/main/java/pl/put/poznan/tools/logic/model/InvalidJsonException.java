package pl.put.poznan.tools.logic.model;

/**
 * Thrown when the input cannot be loaded as a JSON document.
 * Carries the position of the error (1-based), if it is known.
 */
public class InvalidJsonException extends RuntimeException {

    private final int line;
    private final int column;

    public InvalidJsonException(String message, int line, int column, Throwable cause) {
        super(message, cause);
        this.line = line;
        this.column = column;
    }

    public InvalidJsonException(String message, Throwable cause) {
        this(message, -1, -1, cause);
    }

    /** @return line of the error (1-based), or -1 if unknown */
    public int getLine() {
        return line;
    }

    /** @return column of the error (1-based), or -1 if unknown */
    public int getColumn() {
        return column;
    }
}
