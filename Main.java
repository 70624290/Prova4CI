package com.panettondoro.phonebook;

import com.panettondoro.phonebook.phonecode.Country;
import com.panettondoro.phonebook.phonecode.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        Scanner keyboardInput = new Scanner(System.in);
        Phonebook phonebook = new Phonebook();
        String settings = "name";
        Pair[] menu = new Pair[6];

        char choice;

        menu[0] = new Pair('A', "Per aggiungere un nuovo contatto");
        menu[1] = new Pair('E', "Per modificare un contatto");
        menu[2] = new Pair('D', "Per eliminare un contatto");
        menu[3] = new Pair('C', "Per mostrare un contatto");
        menu[4] = new Pair('S', "Per mostrare tutti i contatti");
        menu[5] = new Pair('Q', "Per uscire");

        do {
            displayTitle("Rubrica");
            showMenu(menu);
            choice = keyboardInput.next().toUpperCase().charAt(0);
            switch(choice) {
                case 'A' -> phonebook.addContact(defineContact());
                case 'E' -> phonebook = editContact(phonebook);
                case 'D' -> phonebook = deleteContact(phonebook);
                case 'C' -> showContact(phonebook);
                case 'S' -> displayContacts(phonebook);
                default -> {}
            }
        } while (choice != 'Q');

    }

    // Searching

    public static ArrayList<Integer> search(Phonebook phonebook) {
        Scanner keyboardInput = new Scanner(System.in);
        ArrayList<Integer> IDs = new ArrayList<>();
        String searchTerm;

        searchTerm = keyboardInput.nextLine().toLowerCase();

        for (Contact contact: phonebook.getContacts()) {
            if (contact.getNome().getNome().toLowerCase().contains(searchTerm)) {
                IDs.add(phonebook.getContacts().indexOf(contact));
            } else if (contact.getNome().getCognome().toLowerCase().contains(searchTerm)) {
                IDs.add(phonebook.getContacts().indexOf(contact));
            } else if (contact.getTelephone().getNumber().contains(searchTerm)) {
                IDs.add(phonebook.getContacts().indexOf(contact));
            } else if (contact.getPlace().isPopulated()) {
                if (contact.getPlace().getPlace().toLowerCase().contains(searchTerm)) {
                    IDs.add(phonebook.getContacts().indexOf(contact));
                }
            } else if (contact.getMail().isPopulated()) {
                if (contact.getMail().getMail().toLowerCase().contains(searchTerm)) {
                    IDs.add(phonebook.getContacts().indexOf(contact));
                }
            }
        }
        return IDs;
    }

    public static int choice(ArrayList<Integer> IDs) {
        Scanner keyboardInput = new Scanner(System.in);
        String input;
        int choice;
        boolean message = false;
        System.out.println("> Scelta");
        do {
            if (message) {
                System.out.println("> ID invalido");
            }
            input = keyboardInput.nextLine();
            if (input.matches("^\\d+$") ) {
                choice = Integer.parseInt(input);
                if (IDs.contains(choice)) {
                    return choice;
                }
            }
            message = true;
        } while (true);
    }

    // Display

    public static char displayContactCompact(Contact contact, char previousIndex) {
        char quickIndex = contact.getNome().getNome().charAt(0);
        if (quickIndex != previousIndex) {
            System.out.println("> [" + quickIndex +"] - - - - - - - - ");
        }
        System.out.println(" + " + contact.getNome().getNomeRedacted('N') + ", " + contact.getTelephone().getNumber());
        return quickIndex;
    }

    public static char displayContactCompact(Contact contact, char previousIndex, Integer ID) {
        char quickIndex = contact.getNome().getNome().charAt(0);
        if (quickIndex != previousIndex) {
            System.out.println("> [" + quickIndex +"] - - - - - - - - ");
        }
        System.out.println(ID + ": " + contact.getNome().getNomeRedacted('N') + ", " + contact.getTelephone().getNumber());

        return quickIndex;
    }

    public static void displayContacts(Phonebook phonebook) {
        char quickIndex = 0;
        for (Contact contact: phonebook.getContacts()) {
            quickIndex = displayContactCompact(contact, quickIndex);
        }
    }

    public static void displayResults(Phonebook phonebook, ArrayList<Integer> IDs) {
        char quickIndex = 0;

        for (Integer ID: IDs) {
            quickIndex = displayContactCompact(phonebook.getContacts().get(ID), quickIndex, ID);
        }
    }

    public static void displayTitle (String title) {
        System.out.println("\n- - - - " + title + " - - - -\n");
    }

    // Main functions

    public static Contact defineContact() {
        Nome nome;
        Mail mail;
        Place place;
        Telephone telephone;

        displayTitle("Aggiungi un contatto");

        nome = defineName();
        mail = defineMail();
        telephone = defineNumber();
        place = definePlace();

        if (!place.isPopulated()) {
            place = guessPlace(telephone.getPhoneCode());
        }

        return new Contact(nome, mail, place, telephone);
    }

    public static Phonebook deleteContact(Phonebook phonebook) {
        Scanner keyboardInput = new Scanner(System.in);
        ArrayList<Integer> results;
        int ID;
        displayTitle("Elimina un contatto");
        System.out.println("> Inserisci il termine di ricerca");
        results = search(phonebook);
        if (!results.isEmpty()) {
            displayResults(phonebook, results);
            ID = choice(results);
            System.out.println("Eliminare davvero il contatto \"" + phonebook.getContacts().get(ID).getNome().getNomeRedacted('N')+ "\"? (y/n)");
            if (keyboardInput.nextLine().toLowerCase().charAt(0) == 'y') {
                phonebook.getContacts().remove(ID);
            }
        } else {
            System.out.println("> Nessun risultato");
        }
        return phonebook;
    }

    public static Phonebook editContact (Phonebook phonebook) {
        Scanner keyboardInput = new Scanner(System.in);
        ArrayList<Integer> results;
        Contact contact;
        int ID;
        char choice;
        Pair[] menu = new Pair[5];

        menu[0] = new Pair('N', "Per modificare il nome del contatto");
        menu[1] = new Pair('T', "Per modificare il numero del contatto");
        menu[2] = new Pair('E', "Per modificare l'e-mail del contatto");
        menu[3] = new Pair('R', "Per modificare la residenza contatto");
        menu[4] = new Pair('Q', "Per uscire");

        displayTitle("Modifica un contatto");
        System.out.println("> Inserisci il termine di ricerca");
        results = search(phonebook);
        if (!results.isEmpty()) {

            displayResults(phonebook, results);
            ID = choice(results);
            contact = phonebook.getContacts().get(ID);

            do {
                showMenu(menu);
                choice = keyboardInput.next().toUpperCase().charAt(0);
                switch(choice) {
                    case 'N' -> contact.setNome(defineName());
                    case 'T' -> contact.setTelephone(defineNumber());
                    case 'E' -> contact.setMail(defineMail());
                    case 'R' -> contact.setPlace(definePlace());
                    default -> {}
                }
            } while (choice != 'Q');

            phonebook.getContacts().set(ID, contact);
            phonebook.sortContacts();
        } else {
            System.out.println("> Nessun risultato");
        }

        return phonebook;
    }

    public static void showContact(Phonebook phonebook) {
        Scanner keyboardInput = new Scanner(System.in);
        ArrayList<Integer> results;
        Contact contact;
        int ID;

        displayTitle("Mostra un contatto");
        System.out.println("> Inserisci il termine di ricerca");
        results = search(phonebook);

        if (!results.isEmpty()) {

            displayResults(phonebook, results);
            ID = choice(results);
            contact = phonebook.getContacts().get(ID);

            System.out.println("- - - - - - - - -");
            System.out.println(" Nome completo: " + contact.getNome().getNome() + " " + contact.getNome().getCognome());
            System.out.println(" Numero di telefono: " + contact.getTelephone().getNumber());
            System.out.println("- Dati ausiliari -");
            if (contact.getMail().isPopulated()) {
                System.out.println(" Indirizzo e-mail: " + contact.getMail().getMail());
            }
            if (contact.getPlace().isPopulated()) {
                if (contact.getPlace().isGuessed()) {
                    System.out.println(" Residenza (stima): " + contact.getPlace().getPlace());
                } else {
                    System.out.println(" Residenza: " + contact.getPlace().getPlace());
                }
            }

        } else {
            System.out.println("> Nessun risultato");
        }
    }

    // "Internal" functions

    public static Nome defineName() {
        Scanner keyboardInput = new Scanner(System.in);
        String nome, cognome;
        displayTitle("Dichiara il nome");
        System.out.println("> Inserire il nome della persona: ");
        nome = keyboardInput.nextLine();
        System.out.println("> Inserire il cognome della persona: ");
        cognome = keyboardInput.nextLine();
        return new Nome(nome, cognome);
    }

    public static Mail defineMail() {
        Scanner keyboardInput = new Scanner(System.in);
        Mail mail = new Mail();
        String address;
        boolean matches = true;

        displayTitle("Aggiungi un indirizzo e-mail");

        System.out.println("> Per saltare questo passaggio, lascia il campo vuoto");
        System.out.println("> Inserisci un indirizzo e-mail valido: ");

        address = keyboardInput.nextLine();

        if (!address.equals("")) {
            do {
                if(!matches) {
                    System.out.println("> E-mail invalida");
                    address = keyboardInput.nextLine();
                }
                matches = mail.setMail(address);
            } while(!matches);
        }

        return mail;
    }

    public static Telephone defineNumber() {
        Scanner keyboardInput = new Scanner(System.in);
        Telephone telephone = new Telephone();
        boolean matches = true;

        displayTitle("Aggiungi il numero di telefono");

        System.out.println("> Inserisci un numero di telefono valido: ");

        do {
            if (!matches) {
                System.out.println("> Numero invalido");
            }
            matches = telephone.setNumber(keyboardInput.nextLine());
        } while (!matches);

        return telephone;
    }

    public static Place definePlace() {
        Scanner keyboardInput = new Scanner(System.in);
        Place place = new Place();
        String address;

        displayTitle("Aggiungi un luogo di residenza");

        System.out.println("> Per saltare questo passaggio, lascia il campo vuoto");
        System.out.println("> Inserisci la residenza: ");

        address = keyboardInput.nextLine();

        place.setPlace(address);

        if (address.equals("")) {
            place.setPopulated(false);
        }

        return place;
    }

    // Test

    public static Place guessPlace(String phoneCode) {
        Place place = new Place();
        try {
            File countryCodes = new File("/home/panettondoro/IdeaProjects/Phonebook/src/main/countries.json");
            Country[] countries = JSON.fromJson((JSON.parse(countryCodes)).get("countries"), Country[].class);

            for (Country country: countries) {
                if (country.getPhone_code().equals(phoneCode)) {
                    place.setPlace(country.getCountry_name());
                    place.setGuessed(true);
                    return place;
                }
            }

        } catch (IOException e) {
            e.getStackTrace();
        }
        return place;
    }

    public static void showMenu(Pair[] menu) {
        for (Pair entry: menu) {
            System.out.println(">[" + entry.key() + "] " + entry.value());
        }
    }
}
