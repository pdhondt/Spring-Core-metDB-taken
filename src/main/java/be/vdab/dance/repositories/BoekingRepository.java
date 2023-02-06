package be.vdab.dance.repositories;

import be.vdab.dance.domain.Boeking;
import be.vdab.dance.dto.BoekingMetFestivalNaam;
import be.vdab.dance.exceptions.BoekingNietGevondenException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BoekingRepository {
    private final JdbcTemplate template;
    private final RowMapper<Boeking> boekingMapper = (rs, rowNum) ->
            new Boeking(rs.getLong("id"), rs.getString("naam"),
                    rs.getInt("aantalTickets"), rs.getLong("festivalId"));

    public BoekingRepository(JdbcTemplate template) {
        this.template = template;
    }
    public void create(Boeking boeking) {
        var sql = """
                insert into boekingen(naam, aantalTickets, festivalId)
                values (?, ?, ?)
                """;
        template.update(sql, boeking.getNaam(), boeking.getAantalTickets(), boeking.getFestivalId());
    }
    public List<BoekingMetFestivalNaam> findBoekingenMetFestivalNaam() {
        var sql = """
                select boekingen.id, boekingen.naam as naamBoeker, festivals.naam as naamFestival, aantalTickets
                from boekingen inner join festivals
                on boekingen.festivalId = festivals.id
                order by boekingen.id
                """;
        RowMapper<BoekingMetFestivalNaam> mapper = (rs, rowNum) ->
                new BoekingMetFestivalNaam(rs.getLong("id"), rs.getString("naamBoeker"),
                        rs.getString("naamFestival"), rs.getInt("aantalTickets"));
        return template.query(sql, mapper);
    }
    public void delete(long id) {
        var sql = """
                delete from boekingen
                where id = ?
                """;
        if (template.update(sql, id) == 0) {
            throw new BoekingNietGevondenException(id);
        }
    }
    public Optional<Boeking> findAndLockById(long id) {
        try {
            var sql = """
                select id, naam, aantalTickets, festivalId
                from boekingen
                where id = ?
                for update
                """;
            return Optional.of(template.queryForObject(sql, boekingMapper, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }
}
