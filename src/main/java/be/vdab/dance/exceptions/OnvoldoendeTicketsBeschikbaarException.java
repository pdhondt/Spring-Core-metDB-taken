package be.vdab.dance.exceptions;

import java.io.Serial;

public class OnvoldoendeTicketsBeschikbaarException extends RuntimeException {
    private final int ticketsBeschikbaar;
    @Serial
    private static final long serialVersionUID = 1L;
    public OnvoldoendeTicketsBeschikbaarException(int ticketsBeschikbaar) {
        this.ticketsBeschikbaar = ticketsBeschikbaar;
    }

    public int getTicketsBeschikbaar() {
        return ticketsBeschikbaar;
    }
}
