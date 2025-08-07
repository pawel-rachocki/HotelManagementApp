package org.example;

import org.example.models.*;
import org.example.models.enums.StatusRezerwacji;
import org.example.utlis.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;


public class TestEncji {

    public static void main(String[] args) {
        testPodstawoweEncje();
        dodajWiecejDanychTestowych();
        dodajUslugiDodatkowe();
        dodajRezerwacjeTestowe();
        HibernateUtil.shutdown();
    }

    public static void testPodstawoweEncje() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Tworzenie podstawowych encji zgodnie z dokumentacją
            Adres adresHotelu = new Adres("Główna", 1, null, "00-001", "Warszawa");
            Hotel hotel = new Hotel("Hotel Test", adresHotelu, "123456789", "test@hotel.com");
            session.persist(hotel);

            Pokoj pokoj = new Pokoj(101, "standard", 2, 150.0, hotel);
            pokoj.dodajUdogodnienie("klimatyzacja");
            pokoj.dodajUdogodnienie("balkon");
            session.persist(pokoj);

            Adres adresGoscia = new Adres("Testowa", 10, 5, "02-123", "Kraków");
            Gosc gosc = new Gosc("Jan", "Kowalski", "987654321",
                    "jan.kowalski@email.com", LocalDate.of(1990, 5, 15), adresGoscia);
            session.persist(gosc);

            Recepcjonista recepcjonista = new Recepcjonista("Anna", "Nowak", "555123456",
                    LocalDate.of(1985, 3, 20), 1);
            recepcjonista.dodajJezyk("polski");
            recepcjonista.dodajJezyk("angielski");
            session.persist(recepcjonista);

            Rezerwacja rezerwacja = new Rezerwacja(
                    LocalDate.now().plusDays(7),
                    LocalDate.now().plusDays(10),
                    2, gosc, pokoj, recepcjonista
            );
            session.persist(rezerwacja);

            // Testowanie usług dodatkowych zgodnie z wymaganiami
            UslugaDodatkowa sniadanie = new UslugaDodatkowa("Śniadanie",
                    "Śniadanie kontynentalne", 50.0, "wyżywienie");
            session.persist(sniadanie);

            ZamowienieUslugi zamowienie = new ZamowienieUslugi(3, 50.0, rezerwacja, sniadanie);
            session.persist(zamowienie);

            Platnosc platnosc = new Platnosc(200.0, "karta", "zaliczka", rezerwacja);
            session.persist(platnosc);

            Rachunek rachunek = new Rachunek(rezerwacja);
            session.persist(rachunek);

            transaction.commit();

            System.out.println("✅ Test encji zakończony pomyślnie!");
            System.out.println("Hotel: " + hotel);
            System.out.println("Rezerwacja: " + rezerwacja);
            System.out.println("Rachunek: " + rachunek);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Błąd podczas testowania: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    public static void dodajWiecejDanychTestowych() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Pobierz istniejący hotel
            Hotel hotel = session.createQuery("FROM Hotel", Hotel.class).getResultList().get(0);

            // Dodaj różnorodne pokoje
            int[] numeryPokoi = {102, 103, 104, 105, 201, 202, 203, 301, 302, 401};
            String[] typy = {"ekonomiczny", "ekonomiczny", "standard", "standard",
                    "standard", "standard", "standard", "apartament", "apartament", "apartament"};
            int[] lozka = {1, 1, 2, 2, 1, 2, 2, 2, 4, 4};
            double[] ceny = {80.0, 80.0, 150.0, 150.0, 120.0, 180.0, 180.0, 350.0, 500.0, 600.0};

            for (int i = 0; i < numeryPokoi.length; i++) {
                // Sprawdź czy pokój już istnieje
                Long count = session.createQuery(
                                "SELECT COUNT(p) FROM Pokoj p WHERE p.numerPokoju = :numer", Long.class)
                        .setParameter("numer", numeryPokoi[i])
                        .uniqueResult();

                if (count == 0) {
                    Pokoj pokoj = new Pokoj(numeryPokoi[i], typy[i], lozka[i], ceny[i], hotel);

                    // Dodaj udogodnienia w zależności od typu
                    switch (typy[i]) {
                        case "ekonomiczny":
                            pokoj.dodajUdogodnienie("Wi-Fi");
                            pokoj.dodajUdogodnienie("telewizor");
                            break;
                        case "standard":
                            pokoj.dodajUdogodnienie("klimatyzacja");
                            pokoj.dodajUdogodnienie("Wi-Fi");
                            pokoj.dodajUdogodnienie("telewizor");
                            pokoj.dodajUdogodnienie("balkon");
                            break;
                        case "apartament":
                            pokoj.dodajUdogodnienie("klimatyzacja");
                            pokoj.dodajUdogodnienie("Wi-Fi");
                            pokoj.dodajUdogodnienie("telewizor");
                            pokoj.dodajUdogodnienie("balkon");
                            pokoj.dodajUdogodnienie("minibar");
                            pokoj.dodajUdogodnienie("jacuzzi");
                            if (numeryPokoi[i] >= 400) {
                                pokoj.dodajUdogodnienie("widok na morze");
                            }
                            break;
                    }

                    session.persist(pokoj);
                    System.out.println("✅ Dodano pokój " + numeryPokoi[i] + " (" + typy[i] + ")");
                }
            }

            transaction.commit();
            System.out.println("✅ Zakończono dodawanie pokoi testowych");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Błąd podczas dodawania pokoi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    public static void dodajUslugiDodatkowe() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Sprawdź czy usługi już istnieją
            Long count = session.createQuery("SELECT COUNT(u) FROM UslugaDodatkowa u", Long.class)
                    .uniqueResult();

            if (count <= 1) { // Jeśli mamy tylko śniadanie lub mniej
                // Usługi wyżywieniowe
                session.persist(new UslugaDodatkowa("Obiad", "Obiad dwudaniowy", 85.0, "wyżywienie"));
                session.persist(new UslugaDodatkowa("Kolacja", "Kolacja trzydaniowa", 120.0, "wyżywienie"));
                session.persist(new UslugaDodatkowa("Room service", "Obsługa pokojowa", 25.0, "wyżywienie"));

                // Usługi wellness
                session.persist(new UslugaDodatkowa("Spa", "Dostęp do strefy wellness", 100.0, "wellness"));
                session.persist(new UslugaDodatkowa("Masaż", "Masaż relaksacyjny 60 min", 200.0, "wellness"));
                session.persist(new UslugaDodatkowa("Sauna", "Dostęp do sauny", 60.0, "wellness"));
                session.persist(new UslugaDodatkowa("Jacuzzi", "Relaks w jacuzzi", 80.0, "wellness"));

                // Usługi transportowe
                session.persist(new UslugaDodatkowa("Parking", "Miejsce parkingowe", 20.0, "transport"));
                session.persist(new UslugaDodatkowa("Transfer z lotniska", "Transport z/na lotnisko", 150.0, "transport"));
                session.persist(new UslugaDodatkowa("Wynajem samochodu", "Wynajem na dobę", 200.0, "transport"));

                // Inne usługi
                session.persist(new UslugaDodatkowa("Pralnia", "Pranie i prasowanie", 30.0, "inne"));
                session.persist(new UslugaDodatkowa("Wi-Fi Premium", "Szybki internet", 15.0, "inne"));
                session.persist(new UslugaDodatkowa("Konsjerż", "Usługi konsjerża", 100.0, "inne"));

                transaction.commit();
                System.out.println("✅ Dodano kompletny zestaw usług dodatkowych");
            } else {
                System.out.println("ℹ️ Usługi dodatkowe już istnieją w bazie");
            }

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Błąd podczas dodawania usług: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Dodaje przykładowe rezerwacje z różnymi statusami dla demonstracji funkcjonalności systemu.
     * Implementuje różne stany zgodnie z diagramem stanu rezerwacji z dokumentacji projektowej.
     * Tworzy rezerwacje ze statusami: zakończona, potwierdzona, anulowana dla gościa "Rezerwacje Testowe".
     */
    public static void dodajRezerwacjeTestowe() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            // Znajdź gościa "Rezerwacje Testowe" - zgodnie z wymaganiami obrony
            List<Gosc> goscie = session.createQuery(
                            "FROM Gosc g WHERE g.nazwisko = :nazwisko", Gosc.class)
                    .setParameter("nazwisko", "Testowe")
                    .getResultList();

            if (goscie.isEmpty()) {
                System.err.println("❌ Nie znaleziono gościa o nazwisku 'Testowe'");
                return;
            }

            // Znajdź gościa o imieniu "Rezerwacje"
            Gosc goscTestowy = null;
            for (Gosc g : goscie) {
                if ("Rezerwacje".equals(g.getImie())) {
                    goscTestowy = g;
                    break;
                }
            }

            if (goscTestowy == null) {
                System.err.println("❌ Nie znaleziono gościa 'Rezerwacje Testowe'");
                return;
            }

            // Pobierz dostępne pokoje dla rezerwacji testowych
            List<Pokoj> pokoje = session.createQuery("FROM Pokoj", Pokoj.class).getResultList();
            if (pokoje.size() < 3) {
                System.err.println("❌ Potrzeba co najmniej 3 pokoi w bazie");
                return;
            }

            // Pobierz recepcjonistę do utworzenia rezerwacji
            List<Pracownik> pracownicy = session.createQuery("FROM Pracownik", Pracownik.class).getResultList();
            Recepcjonista recepcjonista = null;
            for (Pracownik p : pracownicy) {
                if (p instanceof Recepcjonista) {
                    recepcjonista = (Recepcjonista) p;
                    break;
                }
            }

            if (recepcjonista == null) {
                System.err.println("❌ Nie znaleziono recepcjonisty w bazie");
                return;
            }

            System.out.println("✅ Znaleziono gościa: " + goscTestowy.getPelneImieNazwisko());
            System.out.println("✅ Recepcjonista: " + recepcjonista.getPelneImieNazwisko());

            // 1. Dodaj rezerwację ZAKOŃCZONĄ (przeszły pobyt)
            // Zgodnie z diagramem stanu: utworzona → potwierdzona → w trakcie → zakończona
            LocalDate dataPrzyjazdZakonczona = LocalDate.now().minusDays(16);
            LocalDate dataWyjazdZakonczona = LocalDate.now().minusDays(14);

            Rezerwacja rezerwacjaZakonczona = new Rezerwacja(
                    dataPrzyjazdZakonczona, dataWyjazdZakonczona, 2,
                    goscTestowy, pokoje.get(0), recepcjonista);
            rezerwacjaZakonczona.setStatusRezerwacji(StatusRezerwacji.ZAKONCZONA);
            rezerwacjaZakonczona.setDataUtworzenia(LocalDate.now().minusDays(30)); // Utworzona miesiąc temu

            session.persist(rezerwacjaZakonczona);
            System.out.println("  ✓ Dodano rezerwację ZAKOŃCZONĄ: " +
                    rezerwacjaZakonczona.getNumerRezerwacji() +
                    " (pokój " + pokoje.get(0).getNumerPokoju() + ")");

            // 2. Dodaj rezerwację POTWIERDZONĄ (przyszły pobyt)
            // Zgodnie z diagramem stanu: utworzona → potwierdzona
            LocalDate dataPrzyjazdPotwierdzona = LocalDate.now().plusDays(7);
            LocalDate dataWyjazdPotwierdzona = LocalDate.now().plusDays(10);

            Rezerwacja rezerwacjaPotwierdzona = new Rezerwacja(
                    dataPrzyjazdPotwierdzona, dataWyjazdPotwierdzona, 1,
                    goscTestowy, pokoje.get(1), recepcjonista);
            rezerwacjaPotwierdzona.setStatusRezerwacji(StatusRezerwacji.POTWIERDZONA);
            rezerwacjaPotwierdzona.setDataUtworzenia(LocalDate.now().minusDays(3)); // Utworzona 3 dni temu

            session.persist(rezerwacjaPotwierdzona);
            System.out.println("  ✓ Dodano rezerwację POTWIERDZONĄ: " +
                    rezerwacjaPotwierdzona.getNumerRezerwacji() +
                    " (pokój " + pokoje.get(1).getNumerPokoju() + ")");

            // 3. Dodaj rezerwację ANULOWANĄ (dla demonstracji diagramu stanu)
            // Zgodnie z diagramem stanu: utworzona → potwierdzona → anulowana
            LocalDate dataPrzyjazdAnulowana = LocalDate.now().plusDays(14);
            LocalDate dataWyjazdAnulowana = LocalDate.now().plusDays(17);

            Rezerwacja rezerwacjaAnulowana = new Rezerwacja(
                    dataPrzyjazdAnulowana, dataWyjazdAnulowana, 3,
                    goscTestowy, pokoje.get(2), recepcjonista);
            rezerwacjaAnulowana.setStatusRezerwacji(StatusRezerwacji.ANULOWANA);
            rezerwacjaAnulowana.setDataUtworzenia(LocalDate.now().minusDays(5)); // Utworzona 5 dni temu

            session.persist(rezerwacjaAnulowana);
            System.out.println("  ✓ Dodano rezerwację ANULOWANĄ: " +
                    rezerwacjaAnulowana.getNumerRezerwacji() +
                    " (pokój " + pokoje.get(2).getNumerPokoju() + ")");

            transaction.commit();
            System.out.println("✅ Dodano przykładowe rezerwacje z różnymi statusami dla demonstracji obrony");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Błąd podczas dodawania rezerwacji testowych: " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
    }



}



