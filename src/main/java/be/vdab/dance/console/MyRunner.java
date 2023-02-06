package be.vdab.dance.console;

import be.vdab.dance.domain.Boeking;
import be.vdab.dance.exceptions.BoekingNietGevondenException;
import be.vdab.dance.exceptions.FestivalNietGevondenException;
import be.vdab.dance.exceptions.OnvoldoendeTicketsBeschikbaarException;
import be.vdab.dance.services.BoekingService;
import be.vdab.dance.services.FestivalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    /*private final FestivalService festivalService;
    public MyRunner(FestivalService festivalService) {
        this.festivalService = festivalService;
    }*/
    private final BoekingService boekingService;
    public MyRunner(BoekingService boekingService) {
        this.boekingService = boekingService;
    }
    @Override
    public void run(String... args) {
        /*festivalService.findUitverkocht().forEach(festival ->
                System.out.println(festival.getId() + ": " + festival.getNaam() + ": aantal tickets: " +
                        festival.getTicketsBeschikbaar() + ", reclame budget: " + festival.getReclameBudget()));*/
        /*var scanner = new Scanner(System.in);
        System.out.print("Geef de id in van het te annuleren festival: ");
        var teAnnulerenId = scanner.nextLong();
        try {
            festivalService.annuleerFestivalVerdeelReclameBudgetEnDeleteFestival(teAnnulerenId);
            System.out.println("Festival geannuleerd");
        } catch (FestivalNietGevondenException ex) {
            System.err.println("Festival met id " + ex.getId() + " niet gevonden");
        }*/
        /*var scanner = new Scanner(System.in);
        System.out.print("Naam: ");
        var naam = scanner.nextLine();
        System.out.print("Aantal gewenste tickets: ");
        var aantalTickets = scanner.nextInt();
        System.out.print("Id van festival: ");
        var festivalId = scanner.nextLong();
        try {
            var boeking = new Boeking(naam, aantalTickets, festivalId);
            boekingService.boekTickets(boeking);
            System.out.println("Tickets geboekt");
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        } catch (FestivalNietGevondenException ex) {
            System.err.println("Festival met id " + ex.getId() + " niet gevonden");
        } catch (OnvoldoendeTicketsBeschikbaarException ex) {
            System.err.println("Slechts " + ex.getTicketsBeschikbaar() + " tickets beschikbaar. Uw gevraagde aantal" +
                    " tickets (" + aantalTickets + ") kan niet geboekt worden");
        }*/
        /*boekingService.findBoekingenMetFestivalNaam().forEach(boekingMetFestivalNaam ->
                System.out.println(boekingMetFestivalNaam.id() + ":" + boekingMetFestivalNaam.naamBoeker() + ":" +
                        boekingMetFestivalNaam.aantalTickets() + ":" + boekingMetFestivalNaam.naamFestival()));*/
        var scanner = new Scanner(System.in);
        System.out.print("Id van de te annuleren boeking: ");
        var id = scanner.nextLong();
        try {
            boekingService.annuleerBoeking(id);
            System.out.println("Boeking " + id + " succesvol geannuleerd");
        } catch (BoekingNietGevondenException ex) {
            System.err.println("Boeking met id " + id + " niet gevonden");
        } catch (FestivalNietGevondenException ex) {
            System.err.println("Festival met id " + ex.getId() + " niet gevonden");
        }
    }
}
