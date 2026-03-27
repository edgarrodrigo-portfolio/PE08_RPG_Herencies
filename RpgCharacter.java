import java.util.Random;

public abstract class RpgCharacter {

    protected String name;
    protected int age;

    protected int health;
    protected int mana;
    protected int maxHealth;
    protected int maxMana;

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

    // Atribut extra
    protected int level;

    public RpgCharacter(String name, int age,
                        int strength, int dexterity, int constitution,
                        int intelligence, int wisdom, int charisma) {

        this.name = normalizeName(name);
        this.age = Math.max(age, 0);

        this.strength = clampStat(strength);
        this.dexterity = clampStat(dexterity);
        this.constitution = clampStat(constitution);
        this.intelligence = clampStat(intelligence);
        this.wisdom = clampStat(wisdom);
        this.charisma = clampStat(charisma);

        this.weapons = new Weapon[10];
        this.weaponCount = 0;
        this.equippedWeapon = null;
        this.defending = false;
        this.level = 1;

        applyRaceModifiers();
        refreshDerivedStats();
        this.health = maxHealth;
        this.mana = maxMana;
    }

    protected abstract void applyRaceModifiers();

    public abstract String getRace();

    protected int clampStat(int value) {
        if (value < 5) {
            return 5;
        }
        if (value > 20) {
            return 20;
        }
        return value;
    }

    protected void refreshDerivedStats() {
        maxHealth = constitution * 50;
        maxMana = intelligence * 30;
        health = Math.min(health, maxHealth == 0 ? constitution * 50 : maxHealth);
        mana = Math.min(mana, maxMana == 0 ? intelligence * 30 : maxMana);
    }

    private String normalizeName(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "SenseNom";
        }
        return value.trim();
    }

    protected void increaseStat(String statName, int amount) {
        if (amount == 0) {
            return;
        }

        switch (statName.toLowerCase()) {
            case "strength":
                strength = clampStat(strength + amount);
                break;
            case "dexterity":
                dexterity = clampStat(dexterity + amount);
                break;
            case "constitution":
                constitution = clampStat(constitution + amount);
                break;
            case "intelligence":
                intelligence = clampStat(intelligence + amount);
                break;
            case "wisdom":
                wisdom = clampStat(wisdom + amount);
                break;
            case "charisma":
                charisma = clampStat(charisma + amount);
                break;
            default:
                break;
        }
    }

    public void addWeapon(Weapon weapon) {
        if (weapon != null && weaponCount < weapons.length) {
            weapons[weaponCount] = weapon;
            weaponCount++;
            if (equippedWeapon == null && canEquipWeapon(weapon)) {
                equippedWeapon = weapon;
            }
        }
    }

    public boolean canEquipWeapon(Weapon weapon) {
        if (weapon == null) {
            return false;
        }
        if (!weapon.isMagical()) {
            return true;
        }
        return intelligence >= 10;
    }

    public boolean equipWeapon(int index) {
        if (index < 0 || index >= weaponCount) {
            return false;
        }

        Weapon selected = weapons[index];
        if (!canEquipWeapon(selected)) {
            return false;
        }

        equippedWeapon = selected;
        return true;
    }

    public void showWeapons() {
        if (weaponCount == 0) {
            System.out.println("No te armes.");
            return;
        }

        for (int i = 0; i < weaponCount; i++) {
            String equipped = weapons[i] == equippedWeapon ? " [equipada]" : "";
            System.out.println((i + 1) + ". " + weapons[i] + equipped);
        }
    }

    protected int calculateBaseDamage() {
        if (equippedWeapon == null) {
            return strength;
        }

        if (equippedWeapon.isMagical()) {
            return equippedWeapon.getDamage() * intelligence / 100;
        }

        return (int) Math.round(strength * (1 + equippedWeapon.getDamage() / 100.0));
    }

    protected int applyAttackModifier(int damage) {
        return damage;
    }

    protected int applyDefenseModifier(int damage) {
        return damage / 2;
    }

    protected int getLifeRegenAmount() {
        return constitution * 3;
    }

    protected int getManaRegenAmount() {
        return intelligence * 2;
    }

    public int attack(RpgCharacter enemy, Random random) {
        int damage = calculateBaseDamage();
        damage = applyAttackModifier(damage);
        return enemy.receiveAttack(damage, random);
    }

    public int receiveAttack(int damage, Random random) {
        if (dodge(random)) {
            defending = false;
            return 0;
        }

        if (defending) {
            damage = applyDefenseModifier(damage);
        }

        health -= damage;
        if (health < 0) {
            health = 0;
        }

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
    }

    public void regenerateLife() {
        health += getLifeRegenAmount();
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public void regenerateMana() {
        mana += getManaRegenAmount();
        if (mana > maxMana) {
            mana = maxMana;
        }
    }

    // Metode extra
    public void rest() {
        regenerateLife();
        regenerateMana();
        System.out.println(name + " descansa una estona i recupera forces.");
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getStatsSummary() {
        return "FOR=" + strength + ", DES=" + dexterity + ", CON=" + constitution
                + ", INT=" + intelligence + ", SAV=" + wisdom + ", CAR=" + charisma;
    }

    public String getShortDescription() {
        String weaponText = equippedWeapon == null ? "Sense arma" : equippedWeapon.getName();
        return getRace() + " - " + name + " | Vida: " + health + "/" + maxHealth
                + " | Mana: " + mana + "/" + maxMana + " | Arma: " + weaponText;
    }

    public String getFullDescription() {
        return getShortDescription() + " | " + getStatsSummary() + " | Nivell: " + level;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getHealth() {
        return health;
    }

    public int getMana() {
        return mana;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getConstitution() {
        return constitution;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getWisdom() {
        return wisdom;
    }

    public int getCharisma() {
        return charisma;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getWeaponCount() {
        return weaponCount;
    }
}
