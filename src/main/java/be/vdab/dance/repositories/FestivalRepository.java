package be.vdab.dance.repositories;

import be.vdab.dance.domain.Festival;
import be.vdab.dance.dto.AantalBoekingenPerFestival;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class FestivalRepository {
    private final JdbcTemplate template;
    private final RowMapper<Festival> festivalMapper = (result, rowNum) ->
            new Festival(result.getLong("id"), result.getString("naam"),
                    result.getInt("ticketsBeschikbaar"), result.getBigDecimal("reclameBudget"));

    public FestivalRepository(JdbcTemplate template) {
        this.template = template;
    }

    public List<Festival> findAll() {
        var sql = """
                select id, naam, ticketsBeschikbaar, reclameBudget
                from festivals
                order by naam
                """;
        return template.query(sql, festivalMapper);
    }

    public List<Festival> findUitverkocht() {
        var sql = """
                select id, naam, ticketsBeschikbaar, reclameBudget
                from festivals
                where ticketsBeschikbaar = 0
                order by naam
                """;
        return template.query(sql, festivalMapper);
    }

    public void delete(long id) {
        var sql = """
                delete from festivals
                where id = ?
                """;
        if (template.update(sql, id) == 0) {
            throw new FestivalNietGevondenException(id);
        }
    }

    public long create(Festival festival) {
        var sql = """
                insert into festivals(naam, ticketsBeschikbaar, reclameBudget)
                values (?, ?, ?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, festival.getNaam());
            statement.setInt(2, festival.getTicketsBeschikbaar());
            statement.setBigDecimal(3, festival.getReclameBudget());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Optional<Festival> findAndLockById(long id) {
        try {
            var sql = """
                select id, naam, ticketsBeschikbaar, reclameBudget
                from festivals
                where id = ?
                for update
                """;
            return Optional.of(template.queryForObject(sql, festivalMapper, id));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    public void verhoogReclameBudgetMetBedrag(long id, BigDecimal bedrag) {
        var sql = """
                update festivals
                set reclameBudget = reclameBudget + ?
                where id = ?
                """;
        if (template.update(sql, bedrag, id) == 0) {
            throw new FestivalNietGevondenException(id);
        }
    }
    public long findAantal() {
        var sql = """
                select count(*)
                from festivals
                """;
        return template.queryForObject(sql, Long.class);
    }
    public void update(Festival festival) {
        var sql = """
                update festivals
                set naam = ?, ticketsBeschikbaar = ?
                where id = ?
                """;
        if (template.update(sql, festival.getNaam(), festival.getTicketsBeschikbaar(), festival.getId()) == 0) {
            throw new FestivalNietGevondenException(festival.getId());
        }
    }
    public List<AantalBoekingenPerFestival> findAantalBoekingenPerFestival() {
        var sql = """
                select festivals.id, festivals.naam, count(*) as aantalBoekingen
                from festivals inner join boekingen
                on festivals.id = boekingen.festivalId
                group by festivals.id
                order by festivals.id;
                """;
        RowMapper<AantalBoekingenPerFestival> mapper = (rs, rowNum) ->
                new AantalBoekingenPerFestival(rs.getLong("id"), rs.getString("naam"),
                        rs.getInt("aantalBoekingen"));
        return template.query(sql, mapper);
    }
}
