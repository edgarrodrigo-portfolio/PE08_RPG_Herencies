public class Elf extends RpgCharacter {

    public Elf(String name, int age,
               int strength, int dexterity, int constitution,
               int intelligence, int wisdom, int charisma) {
        super(name, age, strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    @Override
    protected void applyRaceModifiers() {
        increaseStat("dexterity", 2);
        increaseStat("intelligence", 2);
    }

    @Override
    protected int getManaRegenAmount() {
        return intelligence * 3;
    }

    @Override
    public String getRace() {
        return "Elf";
    }
}
