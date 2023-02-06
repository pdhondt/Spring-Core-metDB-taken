package be.vdab.dance.exceptions;

import java.io.Serial;

public class BoekingNietGevondenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    private final long id;
    public BoekingNietGevondenException(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
}
