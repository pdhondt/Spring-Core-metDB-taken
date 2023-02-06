package be.vdab.dance.repositories;

import be.vdab.dance.domain.Boeking;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(BoekingRepository.class)
@Sql({"/festivals.sql", "/boekingen.sql"})
public class BoekingRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String BOEKINGEN = "boekingen";
    private final BoekingRepository boekingRepository;

    public BoekingRepositoryTest(BoekingRepository boekingRepository) {
        this.boekingRepository = boekingRepository;
    }

    private long idVanTestFestival1() {
        return jdbcTemplate.queryForObject(
                "select id from festivals where naam = 'Rock Werchter'", Long.class);
    }

    @Test
    void create() {
        var id = idVanTestFestival1();
        boekingRepository.create(new Boeking("Peter", 4, id));
        assertThat(countRowsInTableWhere(BOEKINGEN, "naam = 'Peter' and aantalTickets = 4 and festivalId = " + id)).isOne();
    }
    @Test
    void findBoekingenMetFestivalNaam() {
        var lijstBoekingen = boekingRepository.findBoekingenMetFestivalNaam();
        assertThat(lijstBoekingen).hasSize(countRowsInTable(BOEKINGEN))
                .extracting(boekingMetFestivalNaam -> boekingMetFestivalNaam.id()).isSorted();
        var rijMetTest1Boeking = lijstBoekingen
                .stream()
                .filter(boekingMetFestivalNaam -> boekingMetFestivalNaam.naamBoeker().equals("test1"))
                .findFirst()
                .get();
        assertThat(rijMetTest1Boeking.aantalTickets()).isEqualTo(1);
        assertThat(rijMetTest1Boeking.naamFestival()).isEqualTo("Rock Werchter");
    }
}
