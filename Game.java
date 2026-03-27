import java.util.Random;
import java.util.Scanner;

public class Game {

    private Scanner scanner;
    private Random random;

    private RpgCharacter[] characters;
    private int characterCount;

    public Game() {
        scanner = new Scanner(System.in);
        random = new Random();
        characters = new RpgCharacter[20];
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
                    listCharacters();
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
        System.out.println("===== JOC RPG AMB HERENCIA =====");
        System.out.println("1. Crear personatge");
        System.out.println("2. Llistar personatges");
        System.out.println("3. Combat 1 vs 1");
        System.out.println("4. Sortir");
    }

    private void createCharacter() {
        if (characterCount >= characters.length) {
            System.out.println("No hi caben mes personatges.");
            return;
        }

        System.out.println();
        System.out.println("--- CREAR PERSONATGE ---");
        String name = readNonEmptyText("Nom: ");
        int age = readIntInRange("Edat: ", 1, 120);
        int raceOption = readIntInRange("Raca (1.Huma 2.Elf 3.Orc 4.Nan): ", 1, 4);
        int mode = readIntInRange("Creacio (1.Manual 2.Automatica): ", 1, 2);

        int[] stats;
        if (mode == 1) {
            stats = createStatsManual();
        } else {
            stats = createStatsAutomatic();
        }

        RpgCharacter character = buildCharacter(name, age, raceOption, stats);
        giveDefaultWeapons(character);

        characters[characterCount] = character;
        characterCount++;

        System.out.println("Personatge creat correctament:");
        System.out.println(character.getFullDescription());
    }

    private RpgCharacter buildCharacter(String name, int age, int raceOption, int[] stats) {
        switch (raceOption) {
            case 1:
                return new Human(name, age, stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);
            case 2:
                return new Elf(name, age, stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);
            case 3:
                return new Orc(name, age, stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);
            default:
                return new Nan(name, age, stats[0], stats[1], stats[2], stats[3], stats[4], stats[5]);
        }
    }

    private int[] createStatsAutomatic() {
        int[] stats = {5, 5, 5, 5, 5, 5};
        int points = 30;

        while (points > 0) {
            int position = random.nextInt(stats.length);
            if (stats[position] < 20) {
                stats[position]++;
                points--;
            }
        }

        return stats;
    }

    private int[] createStatsManual() {
        String[] names = {"Forca", "Destresa", "Constitucio", "Intelligencia", "Saviesa", "Carisma"};
        int[] stats = new int[6];
        int remaining = 60;

        for (int i = 0; i < stats.length; i++) {
            int remainingSlots = stats.length - i - 1;
            int minValue = 5;
            int maxValue = Math.min(20, remaining - (remainingSlots * 5));

            stats[i] = readIntInRange(names[i] + " (" + minValue + "-" + maxValue + "): ", minValue, maxValue);
            remaining -= stats[i];
        }

        return stats;
    }

    private void giveDefaultWeapons(RpgCharacter character) {
        character.addWeapon(new Weapon("Punys", "Natural", 0, false));

        if (character instanceof Human) {
            character.addWeapon(new Weapon("Espasa llarga", "Espasa", 30, false));
            character.addWeapon(new Weapon("Basto runic", "Basto", 25, true));
        } else if (character instanceof Elf) {
            character.addWeapon(new Weapon("Arc fi", "Arc", 35, false));
            character.addWeapon(new Weapon("Vareta antiga", "Basto", 40, true));
        } else if (character instanceof Orc) {
            character.addWeapon(new Weapon("Destral pesada", "Destral", 40, false));
            character.addWeapon(new Weapon("Massa", "Massa", 28, false));
        } else if (character instanceof Nan) {
            character.addWeapon(new Weapon("Martell de guerra", "Martell", 38, false));
            character.addWeapon(new Weapon("Destral curta", "Destral", 25, false));
        }
    }

    private void listCharacters() {
        if (characterCount == 0) {
            System.out.println("No hi ha personatges creats.");
            return;
        }

        System.out.println();
        System.out.println("--- LLISTA DE PERSONATGES ---");
        for (int i = 0; i < characterCount; i++) {
            System.out.println((i + 1) + ". " + characters[i].getFullDescription());
        }
    }

    private void playSimpleCombat() {
        if (characterCount < 2) {
            System.out.println("Necessites com a minim 2 personatges.");
            return;
        }

        System.out.println();
        System.out.println("--- COMBAT SIMPLE ---");
        listCharacters();

        int p1 = selectCharacter("Jugador 1 tria personatge: ");
        int p2 = selectCharacter("Jugador 2 tria personatge: ");

        while (p2 == p1) {
            System.out.println("No podeu triar el mateix personatge.");
            p2 = selectCharacter("Jugador 2 tria un altre personatge: ");
        }

        RpgCharacter player1 = characters[p1];
        RpgCharacter player2 = characters[p2];
        RpgCharacter current = player1;
        RpgCharacter enemy = player2;

        System.out.println();
        System.out.println("Comenca el combat entre " + player1.getName() + " i " + player2.getName() + ".");

        while (player1.isAlive() && player2.isAlive()) {
            System.out.println();
            System.out.println("Torn de " + current.getName() + " (" + current.getRace() + ")");

            current.regenerateLife();
            current.regenerateMana();
            showTurnStatus(current, enemy);

            int changeWeapon = readIntInRange("Vols canviar d'arma? (1.Si 2.No): ", 1, 2);
            if (changeWeapon == 1) {
                current.showWeapons();
                int weaponOption = readIntInRange("Tria arma: ", 1, current.getWeaponCount());
                if (current.equipWeapon(weaponOption - 1)) {
                    System.out.println("Ara porta: " + current.getEquippedWeapon().getName());
                } else {
                    System.out.println("No pot equipar aquesta arma.");
                }
            }

            System.out.println("1. Atacar");
            System.out.println("2. Defensar-se");
            System.out.println("3. Descansar");
            int action = readIntInRange("Opcio: ", 1, 3);

            if (action == 1) {
                int damage = current.attack(enemy, random);
                if (damage == 0) {
                    System.out.println(enemy.getName() + " ha esquivat l'atac.");
                } else {
                    System.out.println(current.getName() + " ha fet " + damage + " de dany a " + enemy.getName() + ".");
                }
            } else if (action == 2) {
                current.defend();
                System.out.println(current.getName() + " es prepara per resistir el proxim cop.");
            } else {
                current.rest();
            }

            if (!enemy.isAlive()) {
                System.out.println();
                System.out.println(enemy.getName() + " ha caigut en combat.");
                System.out.println(current.getName() + " guanya la partida.");
                return;
            }

            RpgCharacter temp = current;
            current = enemy;
            enemy = temp;
        }
    }

    private void showTurnStatus(RpgCharacter current, RpgCharacter enemy) {
        System.out.println(current.getName() + " -> Vida: " + current.getHealth() + "/" + (current.getConstitution() * 50)
                + " | Mana: " + current.getMana() + "/" + (current.getIntelligence() * 30));
        System.out.println(enemy.getName() + " -> Vida: " + enemy.getHealth() + "/" + (enemy.getConstitution() * 50)
                + " | Mana: " + enemy.getMana() + "/" + (enemy.getIntelligence() * 30));
        System.out.println("Arma equipada: " + (current.getEquippedWeapon() == null ? "Cap" : current.getEquippedWeapon().getName()));
    }

    private int selectCharacter(String message) {
        return readIntInRange(message, 1, characterCount) - 1;
    }

    private String readNonEmptyText(String message) {
        String value;
        do {
            System.out.print(message);
            value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("El text no pot estar buit.");
            }
        } while (value.isEmpty());
        return value;
    }

    private int readIntInRange(String message, int min, int max) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(scanner.nextLine());
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
