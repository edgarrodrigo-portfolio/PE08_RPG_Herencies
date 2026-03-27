import java.util.Random;
import java.util.Scanner;

public class Game {

    private static final int MAX_CHARACTERS = 10;
    private static final int TOTAL_POINTS = 60;
    private static final int BASE_STAT = 5;
    private static final int STAT_COUNT = 6;

    private Scanner scanner;
    private Random random;

    private RpgCharacter[] characters;
    private int characterCount;

    public Game() {
        scanner = new Scanner(System.in);
        random = new Random();
        characters = new RpgCharacter[MAX_CHARACTERS];
        characterCount = 0;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void start() {
        int option;

        do {
            showMenu();
            option = readIntInRange("Selecciona una opcio: ", 1, 4);

            switch (option) {
                case 1:
                    createCharacter();
                    break;
                case 2:
                    listCharacters(true);
                    break;
                case 3:
                    playSimpleCombat();
                    break;
                case 4:
                    System.out.println("Fins aviat.");
                    break;
                default:
                    break;
            }
        } while (option != 4);
    }

    private void showMenu() {
        System.out.println();
        System.out.println("===== JOC RPG =====");
        System.out.println("1. Crear personatge");
        System.out.println("2. Llistar personatges");
        System.out.println("3. Combat 1 vs 1");
        System.out.println("4. Sortir");
    }

    private void createCharacter() {
        if (characterCount >= characters.length) {
            System.out.println("No es poden crear mes personatges.");
            return;
        }

        System.out.println();
        System.out.println("--- Crear personatge ---");

        String name = readNonEmptyText("Nom: ");
        int age = readIntInRange("Edat: ", 1, 300);
        String race = readRace();

        System.out.println("1. Repartiment manual de caracteristiques");
        System.out.println("2. Repartiment automatic de caracteristiques");
        int mode = readIntInRange("Escull una opcio: ", 1, 2);

        int[] stats;
        if (mode == 1) {
            stats = createStatsManual();
        } else {
            stats = createStatsAutomatic();
        }

        RpgCharacter character = new RpgCharacter(name, race, age,
                stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);

        addWeaponsToCharacter(character);

        characters[characterCount] = character;
        characterCount++;

        System.out.println("Personatge creat correctament.");
        System.out.println(character.getShortDescription());
    }

    private int[] createStatsManual() {
        int[] stats = {BASE_STAT, BASE_STAT, BASE_STAT, BASE_STAT, BASE_STAT, BASE_STAT};
        int remainingPoints = TOTAL_POINTS - (BASE_STAT * STAT_COUNT);

        String[] names = {"Forca", "Destresa", "Constitucio", "Intelligencia", "Saviesa", "Carisma"};

        System.out.println();
        System.out.println("Totes les caracteristiques comencen amb 5 punts.");
        System.out.println("Et queden " + remainingPoints + " punts per repartir fins a un maxim de 20.");

        for (int i = 0; i < stats.length; i++) {
            int maxExtra = 20 - BASE_STAT;
            int assign = readIntInRange("Punts extra per a " + names[i] + " (0-" + maxExtra + "): ", 0, maxExtra);

            while (assign > remainingPoints) {
                System.out.println("No tens prou punts. Et queden " + remainingPoints + ".");
                assign = readIntInRange("Torna-ho a provar per a " + names[i] + ": ", 0, maxExtra);
            }

            stats[i] += assign;
            remainingPoints -= assign;
            System.out.println("Punts restants: " + remainingPoints);
        }

        while (remainingPoints > 0) {
            System.out.println();
            System.out.println("Encara queden " + remainingPoints + " punts per repartir.");
            showStatMenu(stats);
            int option = readIntInRange("A quina caracteristica vols afegir 1 punt? ", 1, 6);

            if (stats[option - 1] < 20) {
                stats[option - 1]++;
                remainingPoints--;
            } else {
                System.out.println("Aquesta caracteristica ja esta al maxim.");
            }
        }

        return stats;
    }

    private void showStatMenu(int[] stats) {
        System.out.println("1. Forca: " + stats[0]);
        System.out.println("2. Destresa: " + stats[1]);
        System.out.println("3. Constitucio: " + stats[2]);
        System.out.println("4. Intelligencia: " + stats[3]);
        System.out.println("5. Saviesa: " + stats[4]);
        System.out.println("6. Carisma: " + stats[5]);
    }

    private int[] createStatsAutomatic() {
        int[] stats = {BASE_STAT, BASE_STAT, BASE_STAT, BASE_STAT, BASE_STAT, BASE_STAT};
        int points = TOTAL_POINTS - (BASE_STAT * STAT_COUNT);

        while (points > 0) {
            int pos = random.nextInt(STAT_COUNT);
            if (stats[pos] < 20) {
                stats[pos]++;
                points--;
            }
        }

        return stats;
    }

    private void addWeaponsToCharacter(RpgCharacter character) {
        System.out.println();
        System.out.println("--- Crear armes del personatge ---");
        int weaponAmount = readIntInRange("Quantes armes vols afegir? (1-5): ", 1, 5);

        for (int i = 0; i < weaponAmount; i++) {
            System.out.println();
            System.out.println("Arma " + (i + 1));

            String name = readNonEmptyText("Nom de l'arma: ");
            String type = readWeaponType();
            int damage = readIntInRange("Dany (1-100): ", 1, 100);
            boolean magical = readYesNo("Es magica? (s/n): ");

            if (magical && character.getIntelligence() < 10) {
                System.out.println("Aquest personatge no te prou intelligencia per equipar armes magiques.");
                magical = false;
                System.out.println("L'arma es guardara com a arma fisica.");
            }

            character.addWeapon(new Weapon(name, type, damage, magical));
        }

        chooseWeapon(character);
    }

    private void chooseWeapon(RpgCharacter character) {
        if (character.getWeaponCount() == 0) {
            return;
        }

        System.out.println();
        System.out.println("Armes disponibles de " + character.getName() + ":");
        showWeapons(character);
        int option = readIntInRange("Quina arma vols equipar? ", 1, character.getWeaponCount());

        if (character.equipWeapon(option - 1)) {
            System.out.println("Arma equipada correctament.");
        } else {
            System.out.println("No s'ha pogut equipar l'arma.");
        }
    }

    private void showWeapons(RpgCharacter character) {
        for (int i = 0; i < character.getWeaponCount(); i++) {
            Weapon weapon = character.getWeapon(i);
            String equippedText = "";
            if (weapon == character.getEquippedWeapon()) {
                equippedText = " [EQUIPADA]";
            }
            System.out.println((i + 1) + ". " + weapon + equippedText);
        }
    }

    private void listCharacters(boolean showDetails) {
        if (characterCount == 0) {
            System.out.println("No hi ha personatges creats.");
            return;
        }

        System.out.println();
        System.out.println("--- Personatges ---");
        for (int i = 0; i < characterCount; i++) {
            System.out.println((i + 1) + ". " + characters[i].getShortDescription());
            if (showDetails) {
                System.out.println(characters[i].getFullDescription());
                System.out.println();
            }
        }
    }

    private void playSimpleCombat() {
        if (characterCount < 2) {
            System.out.println("Necessites almenys 2 personatges per poder jugar.");
            return;
        }

        System.out.println();
        System.out.println("--- Combat 1 vs 1 ---");
        listCharacters(false);

        int p1 = chooseCharacter("Jugador 1, tria personatge: ", -1);
        int p2 = chooseCharacter("Jugador 2, tria personatge: ", p1);

        RpgCharacter player1 = characters[p1];
        RpgCharacter player2 = characters[p2];

        RpgCharacter current = player1;
        RpgCharacter enemy = player2;
        int turn = 1;

        while (player1.isAlive() && player2.isAlive()) {
            System.out.println();
            System.out.println("===== TORN " + turn + " =====");
            showBattleStatus(player1, player2);

            if (current.getWeaponCount() > 0) {
                boolean changeWeapon = readYesNo(current.getName() + ", vols canviar d'arma? (s/n): ");
                if (changeWeapon) {
                    showWeapons(current);
                    int weaponOption = readIntInRange("Escull arma: ", 1, current.getWeaponCount());
                    if (current.equipWeapon(weaponOption - 1)) {
                        System.out.println("Ara porta equipada: " + current.getEquippedWeapon().getName());
                    } else {
                        System.out.println("No pot equipar aquesta arma.");
                    }
                }
            }

            System.out.println("1. Atacar");
            System.out.println("2. Defensar-se");
            int action = readIntInRange("Opcio: ", 1, 2);

            if (action == 1) {
                int damage = current.attack(enemy, random);

                if (damage == 0) {
                    System.out.println(enemy.getName() + " ha esquivat l'atac.");
                } else {
                    String weaponName = "sense arma";
                    if (current.getEquippedWeapon() != null) {
                        weaponName = current.getEquippedWeapon().getName();
                    }
                    System.out.println(current.getName() + " ataca amb " + weaponName +
                            " i fa " + damage + " de dany.");
                }
            } else {
                current.defend();
                System.out.println(current.getName() + " es posa en posicio defensiva.");
            }

            if (!enemy.isAlive()) {
                System.out.println();
                System.out.println(current.getName() + " guanya el combat.");
                showBattleStatus(player1, player2);
                return;
            }

            RpgCharacter temp = current;
            current = enemy;
            enemy = temp;
            turn++;
        }
    }

    private void showBattleStatus(RpgCharacter player1, RpgCharacter player2) {
        System.out.println(player1.getShortDescription());
        System.out.println(player2.getShortDescription());
    }

    private int chooseCharacter(String message, int forbiddenIndex) {
        int option = readIntInRange(message, 1, characterCount) - 1;

        while (option == forbiddenIndex) {
            System.out.println("Aquest personatge ja ha estat escollit. Tria'n un altre.");
            option = readIntInRange(message, 1, characterCount) - 1;
        }

        return option;
    }

    private String readRace() {
        String race = readNonEmptyText("Raca (Huma, Elf, Orc, Nan): ");

        while (!race.equalsIgnoreCase("Huma")
                && !race.equalsIgnoreCase("Elf")
                && !race.equalsIgnoreCase("Orc")
                && !race.equalsIgnoreCase("Nan")) {
            System.out.println("Raca no valida.");
            race = readNonEmptyText("Torna-la a escriure (Huma, Elf, Orc, Nan): ");
        }

        return race;
    }

    private String readWeaponType() {
        String type = readNonEmptyText("Tipus (Espasa, Destral, Basto, Arc): ");

        while (!type.equalsIgnoreCase("Espasa")
                && !type.equalsIgnoreCase("Destral")
                && !type.equalsIgnoreCase("Basto")
                && !type.equalsIgnoreCase("Arc")) {
            System.out.println("Tipus d'arma no valid.");
            type = readNonEmptyText("Torna-la a escriure (Espasa, Destral, Basto, Arc): ");
        }

        return type;
    }

    private boolean readYesNo(String message) {
        String answer = readNonEmptyText(message);
        while (!answer.equalsIgnoreCase("s") && !answer.equalsIgnoreCase("n")) {
            System.out.println("Resposta no valida.");
            answer = readNonEmptyText(message);
        }
        return answer.equalsIgnoreCase("s");
    }

    private String readNonEmptyText(String message) {
        System.out.print(message);
        String text = scanner.nextLine().trim();

        while (text.isEmpty()) {
            System.out.println("El text no pot estar buit.");
            System.out.print(message);
            text = scanner.nextLine().trim();
        }

        return text;
    }

    private int readIntInRange(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            String line = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Introdueix un valor entre " + min + " i " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Has d'introduir un numero valid.");
            }
        }
    }
}
