package be.vdab.dance.repositories;

import be.vdab.dance.domain.Festival;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import(FestivalRepository.class)
@Sql({"/festivals.sql", "/boekingen.sql"})
public class FestivalRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String FESTIVALS = "festivals";
    private static final String BOEKINGEN = "boekingen";
    private final FestivalRepository festivalRepository;
    public FestivalRepositoryTest(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }
    @Test
    void findAllGeeftAlleFestivalsGesorteerdOpNaam() {
        assertThat(festivalRepository.findAll())
                .hasSize(countRowsInTable(FESTIVALS))
                .extracting(Festival::getNaam)
                .isSortedAccordingTo(String::compareToIgnoreCase);
    }
    @Test
    void findUitverkochtGeeftDeUitverkochteFestivalsGesorteerdOpNaam() {
        assertThat(festivalRepository.findUitverkocht())
                .hasSize(countRowsInTableWhere(FESTIVALS, "ticketsBeschikbaar = 0"))
                //.allSatisfy(festival -> assertThat(festival.getTicketsBeschikbaar() == 0))
                .extracting(Festival::getNaam)
                .isSortedAccordingTo(String::compareToIgnoreCase);
    }
    private long idVanTestFestival1() {
        return jdbcTemplate.queryForObject(
                "select id from festivals where naam = 'Rock Werchter'", Long.class);
    }
    @Test
    void delete() {
        var id = idVanTestFestival1();
        festivalRepository.delete(id);
        assertThat(countRowsInTableWhere(FESTIVALS, "id = " + id)).isZero();
    }
    @Test
    void create() {
        var id = festivalRepository.create(new Festival(0, "testFestival4", 1000, BigDecimal.TEN));
        assertThat(id).isPositive();
        assertThat(countRowsInTableWhere(FESTIVALS, "id = " + id)).isOne();

    }
    @Test
    void findAndLockById() {
        assertThat(festivalRepository.findAndLockById(idVanTestFestival1()))
                .hasValueSatisfying(festival -> assertThat(festival.getNaam()).isEqualTo("Rock Werchter"));
    }
    @Test
    void findAndLockByOnbestaandeIdVindtGeenFestival() {
        assertThat(festivalRepository.findAndLockById(Long.MAX_VALUE)).isEmpty();
    }
    @Test
    void verhoogReclameBudgetMetBedrag() {
        var id = idVanTestFestival1();
        festivalRepository.verhoogReclameBudgetMetBedrag(id, BigDecimal.TEN);
        assertThat(countRowsInTableWhere(FESTIVALS, "reclameBudget = 15010.00 and id = " + id)).isOne();
    }
    @Test
    void verhoogReclameBudgetMetBedragVanOnbestaandeId() {
        assertThatExceptionOfType(FestivalNietGevondenException.class).isThrownBy(
                () -> festivalRepository.verhoogReclameBudgetMetBedrag(Long.MAX_VALUE, BigDecimal.TEN));
    }
    @Test
    void findAantalGeeftHetAantalFestivals() {
        assertThat(festivalRepository.findAantal()).isEqualTo(countRowsInTable(FESTIVALS));
    }
    @Test
    void updateTickets() {
        var id = idVanTestFestival1();
        var testFestival = new Festival(id, "testFestival", 500, BigDecimal.TEN);
        festivalRepository.update(testFestival);
        assertThat(countRowsInTableWhere(FESTIVALS, "naam = 'testFestival' and ticketsBeschikbaar = 500 and id = " + id)).isOne();
    }
    @Test
    void updateTicketsOnbestaandFestivalGeeftEenFout() {
        assertThatExceptionOfType(FestivalNietGevondenException.class).isThrownBy(
                () -> festivalRepository.update(new Festival(Long.MAX_VALUE, "testFestival2", 100, BigDecimal.TEN)));
    }
    @Test
    void findAantalBoekingenPerFestival() {
        var lijst = festivalRepository.findAantalBoekingenPerFestival();
        assertThat(lijst).extracting(aantalBoekingenPerFestival -> aantalBoekingenPerFestival.id()).isSorted();
        var eersteInLijst = lijst.get(0);
        assertThat(eersteInLijst.aantalBoekingen()).isEqualTo(
                countRowsInTableWhere(BOEKINGEN, "festivalId = " + eersteInLijst.id()));
    }
}
