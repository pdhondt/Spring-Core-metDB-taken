package be.vdab.dance.services;

import be.vdab.dance.domain.Boeking;
import be.vdab.dance.exceptions.BoekingNietGevondenException;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import be.vdab.dance.exceptions.OnvoldoendeTicketsBeschikbaarException;
import be.vdab.dance.repositories.BoekingRepository;
import be.vdab.dance.repositories.FestivalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({BoekingService.class, BoekingRepository.class, FestivalRepository.class})
@Sql({"/festivals.sql", "/boekingen.sql"})
public class BoekingServiceIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String BOEKINGEN = "boekingen";
    private static final String FESTIVALS = "festivals";
    private final BoekingService boekingService;

    public BoekingServiceIntegrationTest(BoekingService boekingService) {
        this.boekingService = boekingService;
    }
    private long idVanTestFestival1() {
        return jdbcTemplate.queryForObject(
                "select id from festivals where naam = 'Rock Werchter'", Long.class);
    }
    private long idVanTestBoeking1() {
        return jdbcTemplate.queryForObject(
                "select id from boekingen where naam = 'test1'", Long.class);
    }
    @Test
    void boekTickets() {
        var testFestivalId = idVanTestFestival1();
        boekingService.boekTickets(new Boeking("Peter", 10, testFestivalId));
        assertThat(countRowsInTableWhere(BOEKINGEN,
                "naam = 'Peter' and aantalTickets = 10 and festivalId = " + testFestivalId)).isOne();
        assertThat(countRowsInTableWhere(FESTIVALS,
                "naam = 'Rock Werchter' and ticketsBeschikbaar = 4990 and id = " + testFestivalId)).isOne();
    }
    @Test
    void boekTicketsVoorEenOnbestaandFestivalMislukt() {
        assertThatExceptionOfType(FestivalNietGevondenException.class).isThrownBy(
                () -> boekingService.boekTickets(new Boeking("Peter", 4, Long.MAX_VALUE)));
    }
    @Test
    void boekingMetOnvoldoendeTicketsBeschikbaarLuktNiet() {
        var testFestivalId = idVanTestFestival1();
        assertThatExceptionOfType(OnvoldoendeTicketsBeschikbaarException.class).isThrownBy(
                () -> boekingService.boekTickets(new Boeking("Peter", 5001, testFestivalId)));
    }
    @Test
    void annuleerBoekingVoorEenOnbestaandeBoekingMislukt() {
        assertThatExceptionOfType(BoekingNietGevondenException.class).isThrownBy(
                () -> boekingService.annuleerBoeking(Long.MAX_VALUE));
    }
    @Test
    void annuleerBoeking() {
        var boekingId = idVanTestBoeking1();
        var festivalId = idVanTestFestival1();
        boekingService.annuleerBoeking(boekingId);
        assertThat(countRowsInTableWhere(BOEKINGEN, "id = " + boekingId)).isZero();
        assertThat(countRowsInTableWhere(FESTIVALS, "ticketsBeschikbaar = 5001 and id = " + festivalId)).isOne();
    }
}
