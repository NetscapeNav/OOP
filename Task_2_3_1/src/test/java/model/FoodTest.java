package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FoodTest {

    @Test
    void testConstructorAndGetters() {
        Cell pos = new Cell(3, 4);
        Food food = new Food(pos, FoodType.NORMAL);
        assertEquals(pos, food.getPosition());
        assertEquals(FoodType.NORMAL, food.getFoodType());
    }

    @Test
    void testBonusFood() {
        Food food = new Food(new Cell(0, 0), FoodType.BONUS);
        assertEquals(FoodType.BONUS, food.getFoodType());
    }

    @Test
    void testShrinkFood() {
        Food food = new Food(new Cell(1, 1), FoodType.SHRINK);
        assertEquals(FoodType.SHRINK, food.getFoodType());
    }
}
