public class Orc extends RpgCharacter {

    public Orc(String name, int age,
               int strength, int dexterity, int constitution,
               int intelligence, int wisdom, int charisma) {
        super(name, age, strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    @Override
    protected void applyRaceModifiers() {
        increaseStat("strength", 3);
        increaseStat("constitution", 1);
    }

    @Override
    public boolean canEquipWeapon(Weapon weapon) {
        if (weapon != null && weapon.isMagical()) {
            return false;
        }
        return super.canEquipWeapon(weapon);
    }

    @Override
    protected int applyAttackModifier(int damage) {
        return (int) Math.round(damage * 1.10);
    }

    @Override
    public String getRace() {
        return "Orc";
    }
}
