package be.vdab.dance.console;

import be.vdab.dance.exceptions.FestivalNietGevondenException;
import be.vdab.dance.services.FestivalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    private final FestivalService festivalService;
    public MyRunner(FestivalService festivalService) {
        this.festivalService = festivalService;
    }
    @Override
    public void run(String... args) {
        /*festivalService.findUitverkocht().forEach(festival ->
                System.out.println(festival.getId() + ": " + festival.getNaam() + ": aantal tickets: " +
                        festival.getTicketsBeschikbaar() + ", reclame budget: " + festival.getReclameBudget()));*/
        var scanner = new Scanner(System.in);
        System.out.print("Geef de id in van het te annuleren festival: ");
        var teAnnulerenId = scanner.nextLong();
        try {
            festivalService.annuleerFestivalVerdeelReclameBudgetEnDeleteFestival(teAnnulerenId);
        } catch (FestivalNietGevondenException ex) {
            System.err.println("Festival met id " + ex.getId() + " niet gevonden");
        }
    }
}
