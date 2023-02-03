package be.vdab.dance.domain;

import be.vdab.dance.exceptions.OnvoldoendeTicketsBeschikbaarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class FestivalTest {
    private Festival testFestival;

    @BeforeEach
    void beforeEach() {
        testFestival = new Festival(1, "testFestival", 10, BigDecimal.TEN);
    }

    @Test
    public void boekTickets() {
        testFestival.boekTickets(10);
        assertThat(testFestival.getTicketsBeschikbaar()).isZero();
    }
    @Test
    public void boekingMisluktBijOnvoldoendeTickets() {
        assertThatExceptionOfType(OnvoldoendeTicketsBeschikbaarException.class).isThrownBy(
                () -> testFestival.boekTickets(11));
    }
    @Test
    public void nulTicketsBoekenMislukt() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> testFestival.boekTickets(0));
    }
    @Test
    public void eenNegatiefAantalTicketsBoekenMislukt() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> testFestival.boekTickets(-1));
    }
}
