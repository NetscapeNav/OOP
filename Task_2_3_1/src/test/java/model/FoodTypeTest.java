package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FoodTypeTest {

    @Test
    void testNormalGrowthValue() {
        assertEquals(1, FoodType.NORMAL.getGrowthValue());
    }

    @Test
    void testBonusGrowthValue() {
        assertEquals(3, FoodType.BONUS.getGrowthValue());
    }

    @Test
    void testShrinkGrowthValue() {
        assertEquals(-1, FoodType.SHRINK.getGrowthValue());
    }

    @Test
    void testValuesCount() {
        assertEquals(3, FoodType.values().length);
    }
}
