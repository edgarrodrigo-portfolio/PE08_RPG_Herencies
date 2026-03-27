import java.util.Random;

public class RpgCharacter {

    protected String name;
    protected String race;
    protected int age;

    protected int health;
    protected int mana;
    protected int stamina;

    protected int strength;
    protected int dexterity;
    protected int constitution;
    protected int intelligence;
    protected int wisdom;
    protected int charisma;

    protected Weapon[] weapons;
    protected int weaponCount;
    protected Weapon equippedWeapon;

    protected boolean defending;

    // Atribut extra demanat a l'enunciat
    protected int level;

    public RpgCharacter(String name, String race, int age,
                        int strength, int dexterity, int constitution,
                        int intelligence, int wisdom, int charisma) {

        this.name = name;
        this.race = race;
        setAge(age);

        setStrength(strength);
        setDexterity(dexterity);
        setConstitution(constitution);
        setIntelligence(intelligence);
        setWisdom(wisdom);
        setCharisma(charisma);

        this.health = getMaxHealth();
        this.mana = getMaxMana();
        this.stamina = 100;

        this.weapons = new Weapon[5];
        this.weaponCount = 0;
        this.equippedWeapon = null;

        this.defending = false;
        this.level = 1;
    }

    public String getName() {
        return name;
    }

    public String getRace() {
        return race;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age < 0) {
            this.age = 0;
        } else {
            this.age = age;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health < 0) {
            this.health = 0;
        } else if (health > getMaxHealth()) {
            this.health = getMaxHealth();
        } else {
            this.health = health;
        }
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        if (mana < 0) {
            this.mana = 0;
        } else if (mana > getMaxMana()) {
            this.mana = getMaxMana();
        } else {
            this.mana = mana;
        }
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        if (stamina < 0) {
            this.stamina = 0;
        } else if (stamina > 100) {
            this.stamina = 100;
        } else {
            this.stamina = stamina;
        }
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = normalizeStat(strength);
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = normalizeStat(dexterity);
    }

    public int getConstitution() {
        return constitution;
    }

    public void setConstitution(int constitution) {
        this.constitution = normalizeStat(constitution);
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = normalizeStat(intelligence);
    }

    public int getWisdom() {
        return wisdom;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = normalizeStat(wisdom);
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = normalizeStat(charisma);
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getWeaponCount() {
        return weaponCount;
    }

    public Weapon getWeapon(int index) {
        if (index < 0 || index >= weaponCount) {
            return null;
        }
        return weapons[index];
    }

    private int normalizeStat(int value) {
        if (value < 5) {
            return 5;
        }
        if (value > 20) {
            return 20;
        }
        return value;
    }

    public int getMaxHealth() {
        return constitution * 50;
    }

    public int getMaxMana() {
        return intelligence * 30;
    }

    public void addWeapon(Weapon weapon) {
        if (weapon == null || weaponCount >= weapons.length) {
            return;
        }

        weapons[weaponCount] = weapon;
        weaponCount++;

        if (equippedWeapon == null) {
            equipWeapon(weaponCount - 1);
        }
    }

    public boolean equipWeapon(int index) {
        if (index < 0 || index >= weaponCount) {
            return false;
        }

        Weapon selectedWeapon = weapons[index];

        if (selectedWeapon.isMagical() && intelligence < 10) {
            return false;
        }

        equippedWeapon = selectedWeapon;
        return true;
    }

    public int calculateDamage() {
        if (equippedWeapon == null) {
            return strength;
        }

        if (equippedWeapon.isMagical()) {
            return equippedWeapon.getDamage() * intelligence / 100;
        }

        return (int) Math.round(strength * (1 + equippedWeapon.getDamage() / 100.0));
    }

    public int attack(RpgCharacter enemy, Random random) {
        int damage = calculateDamage();
        int realDamage = enemy.receiveAttack(damage, random);

        regenerateLife();
        regenerateMana();
        setStamina(stamina - 15);

        return realDamage;
    }

    public int receiveAttack(int damage, Random random) {
        if (dodge(random)) {
            defending = false;
            return 0;
        }

        if (defending) {
            damage = damage / 2;
        }

        setHealth(health - damage);
        defending = false;

        return damage;
    }

    public boolean dodge(Random random) {
        double chance = (dexterity - 5) * 3.33;
        int number = random.nextInt(100) + 1;
        return number <= chance;
    }

    public void defend() {
        defending = true;
        regenerateLife();
        regenerateMana();
        setStamina(stamina + 10);
    }

    public void regenerateLife() {
        setHealth(health + constitution * 3);
    }

    public void regenerateMana() {
        setMana(mana + intelligence * 2);
    }

    // Metode propi afegit per donar una mica mes de personalitat al personatge
    public void rest() {
        setStamina(stamina + 25);
        regenerateLife();
        regenerateMana();
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getShortDescription() {
        String weaponName = "Sense arma";
        if (equippedWeapon != null) {
            weaponName = equippedWeapon.getName();
        }

        return name + " | Raca: " + race + " | Vida: " + health + "/" + getMaxHealth() +
                " | Mana: " + mana + "/" + getMaxMana() +
                " | Stamina: " + stamina + " | Arma equipada: " + weaponName;
    }

    public String getFullDescription() {
        return "Nom: " + name +
                "\nRaca: " + race +
                "\nEdat: " + age +
                "\nNivell: " + level +
                "\nForca: " + strength +
                "\nDestresa: " + dexterity +
                "\nConstitucio: " + constitution +
                "\nIntelligencia: " + intelligence +
                "\nSaviesa: " + wisdom +
                "\nCarisma: " + charisma +
                "\nVida: " + health + "/" + getMaxHealth() +
                "\nMana: " + mana + "/" + getMaxMana() +
                "\nStamina: " + stamina;
    }
}
