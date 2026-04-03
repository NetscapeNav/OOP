package model;

public class Food {
    private final Cell position;
    private final FoodType foodType;

    public Food(Cell position, FoodType foodType) {
        this.position = position;
        this.foodType = foodType;
    }

    public Cell getPosition() {
        return position;
    }

    public FoodType getFoodType() {
        return foodType;
    }
}
