package be.vdab.dance.services;

import be.vdab.dance.domain.Festival;
import be.vdab.dance.repositories.FestivalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
