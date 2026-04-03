package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level {
    private final int height;
    private final int width;
    private final int tickInterval;
    private final int foodCount;
    private final int winLength;
    private final Obstacle obstacles;
    private final int levelNumber;
    private final Config config;

    public Level(int height, int width, int tickInterval, int foodCount, int winLength, Obstacle obstacles, int levelNumber, Config config) {
        this.height = height;
        this.width = width;
        this.tickInterval = tickInterval;
        this.foodCount = foodCount;
        this.winLength = winLength;
        this.obstacles = obstacles;
        this.levelNumber = levelNumber;
        this.config = config;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public int getWinLength() {
        return winLength;
    }

    public Obstacle getObstacles() {
        return obstacles;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public Config getConfig() {
        return config;
    }

    public static List<Level> defaultLevels() {
        List<Level> levels = new ArrayList<>();
        Config defaultConfig = new Config(Arrays.asList(70.0, 15.0, 15.0));

        levels.add(new Level(15, 20, 200, 3, 10, new Obstacle(), 1, defaultConfig));

        Obstacle obstaclesSecond = new Obstacle();
        obstaclesSecond.addCell(new Cell(10,8));
        obstaclesSecond.addCell(new Cell(11,8));
        obstaclesSecond.addCell(new Cell(12,8));
        levels.add(new Level(15, 20, 150, 4, 15, obstaclesSecond, 2, defaultConfig));

        Obstacle obstaclesThird = new Obstacle();
        for (int i = 5; i <= 14; i++) {
            obstaclesThird.addCell(new Cell(10, i));
        }
        levels.add(new Level(15, 20, 100, 5, 20, obstaclesThird, 3, defaultConfig));

        return levels;
    }
}
