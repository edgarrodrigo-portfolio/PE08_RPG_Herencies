public class Human extends RpgCharacter {

    public Human(String name, int age,
                 int strength, int dexterity, int constitution,
                 int intelligence, int wisdom, int charisma) {
        super(name, age, strength, dexterity, constitution, intelligence, wisdom, charisma);
    }

    @Override
    protected void applyRaceModifiers() {
        increaseStat("strength", 1);
        increaseStat("dexterity", 1);
        increaseStat("constitution", 1);
        increaseStat("intelligence", 1);
        increaseStat("wisdom", 1);
        increaseStat("charisma", 1);
    }

    @Override
    public String getRace() {
        return "Huma";
    }
}
