package pizzahut;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class PizzaLoaderTest {

    @Test
    void loadsConfigFromJson() {
        String json = """
            {
              "pizzeria": {
                "bakers": [
                  {"speed": 5},
                  {"speed": 6}
                ],
                "delivers": [
                  {"storage": 3, "time": 200},
                  {"storage": 5, "time": 500}
                ],
                "storage": 10
              }
            }
            """;
        Config config = PizzaLoader.load(new StringReader(json));

        assertNotNull(config);
        assertEquals(10, config.storage);
        assertEquals(2, config.bakers.size());
        assertEquals(5, config.bakers.get(0).speed);
        assertEquals(6, config.bakers.get(1).speed);
        assertEquals(2, config.delivers.size());
        assertEquals(3, config.delivers.get(0).storage);
        assertEquals(200, config.delivers.get(0).time);
        assertEquals(5, config.delivers.get(1).storage);
        assertEquals(500, config.delivers.get(1).time);
    }

    @Test
    void loadsSingleBakerAndDelivery() {
        String json = """
            {
              "pizzeria": {
                "bakers": [{"speed": 1}],
                "delivers": [{"storage": 1, "time": 100}],
                "storage": 5
              }
            }
            """;
        Config config = PizzaLoader.load(new StringReader(json));

        assertEquals(5, config.storage);
        assertEquals(1, config.bakers.size());
        assertEquals(1, config.bakers.get(0).speed);
        assertEquals(1, config.delivers.size());
        assertEquals(1, config.delivers.get(0).storage);
        assertEquals(100, config.delivers.get(0).time);
    }

    @Test
    void loadsEmptyBakersAndDeliveries() {
        String json = """
            {
              "pizzeria": {
                "bakers": [],
                "delivers": [],
                "storage": 0
              }
            }
            """;
        Config config = PizzaLoader.load(new StringReader(json));

        assertEquals(0, config.storage);
        assertTrue(config.bakers.isEmpty());
        assertTrue(config.delivers.isEmpty());
    }

    @Test
    void loadsFromResourceFile() {
        var stream = getClass().getClassLoader().getResourceAsStream("setting.json");
        assertNotNull(stream, "setting.json should exist in test resources");

        Config config = PizzaLoader.load(new java.io.InputStreamReader(stream));
        assertNotNull(config);
        assertTrue(config.storage > 0);
        assertFalse(config.bakers.isEmpty());
        assertFalse(config.delivers.isEmpty());
    }
}
