package model;

import java.util.List;
import java.util.Random;

public class Config {
    private final List<Double> chances;

    public Config(List<Double> chances) {
        this.chances = chances;
    }

    public List<Double> getChances() {
        return chances;
    }

    public FoodType getRandomFoodType(Random random) {
        if (chances == null || chances.isEmpty()) {
            return FoodType.NORMAL;
        }

        double sum = 0;
        for (Double chance : chances) {
            sum += chance;
        }

        double randomValue = random.nextDouble() * sum;
        FoodType selectedType = FoodType.NORMAL;

        double cumulative = 0.0;
        FoodType[] types = FoodType.values();
        for (int i = 0; i < chances.size() && i < types.length; i++) {
            cumulative += chances.get(i);
            if (randomValue < cumulative) {
                selectedType = types[i];
                break;
            }
        }

        return selectedType;
    }
}
