package model;

public enum FoodType {
    NORMAL(1),
    BONUS(3),
    SHRINK(-1);

    private final int growthValue;

    FoodType(int growthValue) {
        this.growthValue = growthValue;
    }

    public int getGrowthValue() {
        return growthValue;
    }
}
