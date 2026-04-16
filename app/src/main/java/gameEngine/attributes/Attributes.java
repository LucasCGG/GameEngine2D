package gameEngine.attributes;

public class Attributes {

    public float maxHealth;
    public float currentHealth;
    public float movementSpeed;
    public float damage;
    public float defense;

    public int level = 1;
    public int experience = 0;
    public int experienceToNextLevel = 100;

    public Attributes(float maxHealth, float movementSpeed, float damage, float defense) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.movementSpeed = movementSpeed;
        this.damage = damage;
        this.defense = defense;
    }

    public void gainExperience(int amount) {
        experience += amount;

        while (experience >= experienceToNextLevel) {
            experience -= experienceToNextLevel;
            levelUp();
        }
    }

    public void levelUp() {
        level++;
        experienceToNextLevel = (int) (experienceToNextLevel + 1.5f);
        maxHealth += 10;
        currentHealth = maxHealth;
        damage += 2;
        defense += 1;
        movementSpeed += 20f;
        // movementSpeed += 0.5f;
    }
}
