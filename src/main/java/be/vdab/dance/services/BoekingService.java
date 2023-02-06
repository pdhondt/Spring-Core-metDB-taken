package be.vdab.dance.services;

import be.vdab.dance.domain.Boeking;
import be.vdab.dance.dto.BoekingMetFestivalNaam;
import be.vdab.dance.exceptions.BoekingNietGevondenException;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import be.vdab.dance.repositories.BoekingRepository;
import be.vdab.dance.repositories.FestivalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BoekingService {
    private final BoekingRepository boekingRepository;
    private final FestivalRepository festivalRepository;

    public BoekingService(BoekingRepository boekingRepository, FestivalRepository festivalRepository) {
        this.boekingRepository = boekingRepository;
        this.festivalRepository = festivalRepository;
    }
    @Transactional
    public void boekTickets(Boeking boeking) {
        var festival = festivalRepository.findAndLockById(boeking.getFestivalId())
                .orElseThrow(() -> new FestivalNietGevondenException(boeking.getFestivalId()));
        festival.boekTickets(boeking.getAantalTickets());
        boekingRepository.create(boeking);
        festivalRepository.update(festival);
    }
    public List<BoekingMetFestivalNaam> findBoekingenMetFestivalNaam() {
        return boekingRepository.findBoekingenMetFestivalNaam();
    }
    @Transactional
    public void annuleerBoeking(long id) {
        var boeking = boekingRepository.findAndLockById(id)
                .orElseThrow(() -> new BoekingNietGevondenException(id));
        var festival = festivalRepository.findAndLockById(boeking.getFestivalId())
                .orElseThrow(() -> new FestivalNietGevondenException(boeking.getFestivalId()));
        festival.verhoogTickets(boeking.getAantalTickets());
        boekingRepository.delete(boeking.getId());
        festivalRepository.update(festival);
    }
}
