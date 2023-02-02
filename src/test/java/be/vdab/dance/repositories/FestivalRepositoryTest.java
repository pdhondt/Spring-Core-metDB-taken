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
@Sql("/festivals.sql")
public class FestivalRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String FESTIVALS = "festivals";
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
                .extracting(Festival::getNaam)
                .isEqualTo("Rock Werchter");
    }
    @Test
    void findByOnbestaandeIdVindtGeenFestival() {
        assertThat(festivalRepository.findAndLockById(Long.MAX_VALUE)).isNull();
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

}
