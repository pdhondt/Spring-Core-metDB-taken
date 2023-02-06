package be.vdab.dance.domain;

import be.vdab.dance.exceptions.OnvoldoendeTicketsBeschikbaarException;

import java.math.BigDecimal;

public class Festival {
    private final long id;
    private final String naam;
    private int ticketsBeschikbaar;
    private final BigDecimal reclameBudget;

    public Festival(long id, String naam, int ticketsBeschikbaar, BigDecimal reclameBudget) {
        this.id = id;
        this.naam = naam;
        this.ticketsBeschikbaar = ticketsBeschikbaar;
        this.reclameBudget = reclameBudget;
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public int getTicketsBeschikbaar() {
        return ticketsBeschikbaar;
    }

    public BigDecimal getReclameBudget() {
        return reclameBudget;
    }
    public void boekTickets(int tickets) {
        if (tickets <= 0) {
            throw new IllegalArgumentException("Aantal tickets moet positief zijn");
        }
        if (ticketsBeschikbaar < tickets) {
            throw new OnvoldoendeTicketsBeschikbaarException(ticketsBeschikbaar);
        }
        ticketsBeschikbaar -= tickets;
    }
    public void verhoogTickets(int tickets) {
        ticketsBeschikbaar += tickets;
    }
}
