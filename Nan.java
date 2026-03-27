public class Nan extends RpgCharacter {

    public Nan(String name, int age,
               int strength, int dexterity, int constitution,
               int intelligence, int wisdom, int charisma) {
        super(name, age, strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    @Override
    protected void applyRaceModifiers() {
        increaseStat("constitution", 4);
        increaseStat("dexterity", -1);
    }

    @Override
    protected int applyDefenseModifier(int damage) {
        return (int) Math.round(damage * 0.25);
    }

    @Override
    protected int getLifeRegenAmount() {
        return constitution * 4;
    }

    @Override
    public String getRace() {
        return "Nan";
    }
}
