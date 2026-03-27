public class Weapon {

    private String name;
    private String type;
    private int damage;
    private boolean magical;

    public Weapon(String name, String type, int damage, boolean magical) {
        this.name = name;
        this.type = type;
        setDamage(damage);
        this.magical = magical;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null && !type.trim().isEmpty()) {
            this.type = type.trim();
        }
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        if (damage < 1) {
            this.damage = 1;
        } else if (damage > 100) {
            this.damage = 100;
        } else {
            this.damage = damage;
        }
    }

    public boolean isMagical() {
        return magical;
    }

    public void setMagical(boolean magical) {
        this.magical = magical;
    }

    @Override
    public String toString() {
        String mode = magical ? "magica" : "fisica";
        return name + " | Tipus: " + type + " | Dany: " + damage + " | " + mode;
    }
}
