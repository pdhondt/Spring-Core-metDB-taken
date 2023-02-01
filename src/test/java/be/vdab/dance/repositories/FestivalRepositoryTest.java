package be.vdab.dance.repositories;

import be.vdab.dance.domain.Festival;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

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
}
