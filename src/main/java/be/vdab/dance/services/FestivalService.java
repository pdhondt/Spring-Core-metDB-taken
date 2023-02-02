package be.vdab.dance.services;

import be.vdab.dance.domain.Festival;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import be.vdab.dance.repositories.FestivalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FestivalService {
    public final FestivalRepository festivalRepository;
    public FestivalService(FestivalRepository festivalRepository) {
        this.festivalRepository = festivalRepository;
    }
    public List<Festival> findAll() {
        return festivalRepository.findAll();
    }
    public List<Festival> findUitverkocht() {
        return festivalRepository.findUitverkocht();
    }
    @Transactional
    public void delete(long id) {
        festivalRepository.delete(id);
    }
    @Transactional
    public long create(Festival festival) {
        return festivalRepository.create(festival);
    }
    @Transactional
    public void annuleerFestivalVerdeelReclameBudgetEnDeleteFestival(long id) {
        var teAnnulerenFestival = festivalRepository.findAndLockById(id);
        if (teAnnulerenFestival == null) {
            throw new FestivalNietGevondenException(id);
        } else {
            var teVerdelenBudget = teAnnulerenFestival.getReclameBudget();
            var aantalResterendeFestivals = festivalRepository.findAantal() - 1;
            var bedrag = teVerdelenBudget.divide(BigDecimal.valueOf(aantalResterendeFestivals),
                    2, RoundingMode.HALF_UP);
            festivalRepository.findAll().forEach(festival ->
                    festivalRepository.verhoogReclameBudgetMetBedrag(festival.getId(), bedrag));
        }
        festivalRepository.delete(id);
    }
}
