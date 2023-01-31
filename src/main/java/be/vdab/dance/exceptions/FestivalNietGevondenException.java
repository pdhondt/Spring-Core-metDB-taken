package be.vdab.dance.exceptions;

import java.io.Serial;

public class FestivalNietGevondenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long id;
    public FestivalNietGevondenException(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
}
