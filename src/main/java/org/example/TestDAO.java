package org.example;


import org.example.dao.GoscDAOImpl;
import org.example.dao.RezerwacjaDAOImpl;
import org.example.models.Adres;
import org.example.models.Gosc;
import org.example.models.Rezerwacja;
import org.example.utlis.HibernateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TestDAO {

    public static void main(String[] args) {
        testGoscDAO();
        testRezerwacjaDAO();
        HibernateUtil.shutdown();
    }
    public static void testRezerwacjaDAO() {
        RezerwacjaDAOImpl rezerwacjaDAO = new RezerwacjaDAOImpl();

        try {
            // Test wyszukiwania wszystkich rezerwacji
            List<Rezerwacja> wszystkieRezerwacje = rezerwacjaDAO.findAll();
            System.out.println("✅ Znaleziono " + wszystkieRezerwacje.size() + " rezerwacji w bazie");

            if (!wszystkieRezerwacje.isEmpty()) {
                Rezerwacja pierwszaRezerwacja = wszystkieRezerwacje.get(0);

                // Test wyszukiwania po numerze rezerwacji
                Optional<Rezerwacja> rezerwacja = rezerwacjaDAO.findByNumerRezerwacji(
                        pierwszaRezerwacja.getNumerRezerwacji());
                if (rezerwacja.isPresent()) {
                    System.out.println("✅ Znaleziono rezerwację po numerze: " +
                            rezerwacja.get().getNumerRezerwacji());
                }

                // Test wyszukiwania rezerwacji gościa
                List<Rezerwacja> rezerwacjeGoscia = rezerwacjaDAO.findByGosc(
                        pierwszaRezerwacja.getGosc().getId());
                System.out.println("✅ Gość ma " + rezerwacjeGoscia.size() + " rezerwacji");

                // Test sprawdzania kolizji terminów
                boolean maKolizje = rezerwacjaDAO.sprawdzKolizjeTerminow(
                        pierwszaRezerwacja.getPokoj().getId(),
                        pierwszaRezerwacja.getDataPrzyjazdu(),
                        pierwszaRezerwacja.getDataWyjazdu(),
                        pierwszaRezerwacja.getId()
                );
                System.out.println("✅ Sprawdzenie kolizji terminów: " + !maKolizje);
            }

        } catch (Exception e) {
            System.err.println("❌ Błąd podczas testowania RezerwacjaDAO: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void testGoscDAO() {
        GoscDAOImpl goscDAO = new GoscDAOImpl();

        try {
            // Test wyszukiwania po email
            Optional<Gosc> gosc = goscDAO.findByEmail("jan.kowalski@email.com");
            if (gosc.isPresent()) {
                System.out.println("✅ Znaleziono gościa: " + gosc.get().getPelneImieNazwisko());
            } else {
                System.out.println("❌ Nie znaleziono gościa o podanym email");
            }

            // Test wyszukiwania po nazwisku
            List<Gosc> goscie = goscDAO.findByNazwisko("Kowalski");
            System.out.println("✅ Znaleziono " + goscie.size() + " gości o nazwisku Kowalski");

            // Test sprawdzania istnienia
            boolean exists = goscDAO.existsByEmail("jan.kowalski@email.com");
            System.out.println("✅ Gość istnieje w bazie: " + exists);

            // Test dodawania nowego gościa z UNIKALNYM emailem
            String uniqueEmail = "maria.nowak." + System.currentTimeMillis() + "@email.com";

            // Sprawdź czy email już istnieje
            if (!goscDAO.existsByEmail(uniqueEmail)) {
                Adres nowyAdres = new Adres("Nowa", 5, 10, "03-456", "Gdańsk");
                Gosc nowyGosc = new Gosc("Maria", "Nowak", "111222333",
                        uniqueEmail, LocalDate.of(1985, 8, 20), nowyAdres);
                goscDAO.save(nowyGosc);
                System.out.println("✅ Dodano nowego gościa: " + nowyGosc.getPelneImieNazwisko());
            } else {
                System.out.println("⚠️ Gość o tym emailu już istnieje - pomijam dodawanie");
            }

        } catch (Exception e) {
            System.err.println("❌ Błąd podczas testowania DAO: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

