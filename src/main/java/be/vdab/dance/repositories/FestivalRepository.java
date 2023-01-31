package be.vdab.dance.repositories;

import be.vdab.dance.domain.Festival;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

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
}
