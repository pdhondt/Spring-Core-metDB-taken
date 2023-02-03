package be.vdab.dance.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class BoekingTest {
    @Test
    public void eenBoekingDieLukt() {
        new Boeking("Peter", 4, 1);
    }
    @Test
    public void eenBoekingZonderNaamMislukt() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Boeking("", 2, 1));
    }
    @Test
    public void eenBoekingMetEnkelBlanksAlsNaamMislukt() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Boeking(" ", 2, 1));
    }
    @Test
    public void aantalTicketsMoetPositiefZijn() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Boeking("Peter", -1, 1));
    }
    @Test
    public void aantalTicketsMagNietNulZijn() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Boeking("Peter", 0, 1));
    }
    @Test
    public void festivalIdMoetPositiefZijn() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Boeking("Peter", 2, 0));
    }
}
