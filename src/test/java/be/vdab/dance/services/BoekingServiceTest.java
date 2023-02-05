package be.vdab.dance.services;

import be.vdab.dance.domain.Boeking;
import be.vdab.dance.domain.Festival;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import be.vdab.dance.exceptions.OnvoldoendeTicketsBeschikbaarException;
import be.vdab.dance.repositories.BoekingRepository;
import be.vdab.dance.repositories.FestivalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BoekingServiceTest {
    private BoekingService boekingService;
    @Mock
    private BoekingRepository boekingRepository;
    @Mock
    private FestivalRepository festivalRepository;
    private Festival testFestival;
    @BeforeEach
    void beforeEach() {
        boekingService = new BoekingService(boekingRepository, festivalRepository);
        testFestival = new Festival(1, "testFestival", 10, BigDecimal.TEN);
    }
    @Test
    void boekingMetOnbestaandFestivalLuktNiet() {
        assertThatExceptionOfType(FestivalNietGevondenException.class).isThrownBy(
                () -> boekingService.boekTickets(new Boeking("Peter", 2, Long.MAX_VALUE)));
    }
    @Test
    void nulTicketsBoekenLuktNiet() {
        assertThatIllegalArgumentException().isThrownBy(
                () -> boekingService.boekTickets(new Boeking("Peter", 0, 1)));
    }
    @Test
    void boekingMetOnvoldoendeTicketsBeschikbaarLuktNiet() {
        when(festivalRepository.findAndLockById(1)).thenReturn(Optional.of(testFestival));
        assertThatExceptionOfType(OnvoldoendeTicketsBeschikbaarException.class).isThrownBy(
                () -> boekingService.boekTickets(new Boeking("Peter", 11, 1)));
    }
    @Test
    void boekTickets() {
        when(festivalRepository.findAndLockById(1)).thenReturn(Optional.of(testFestival));
        var boeking = new Boeking("Peter", 4, 1);
        boekingService.boekTickets(boeking);
        assertThat(testFestival.getTicketsBeschikbaar()).isEqualTo(6);
        verify(festivalRepository).findAndLockById(1);
        verify(festivalRepository).update(testFestival);
        verify(boekingRepository).create(boeking);

    }
}
