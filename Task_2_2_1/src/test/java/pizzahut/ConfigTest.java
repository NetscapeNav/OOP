package pizzahut;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {
    @Test
    void configFieldsAreAccessible() {
        Config config = new Config();
        config.storage = 10;

        Config.BakerConfig baker = new Config.BakerConfig();
        baker.speed = 5;

        Config.DeliveryConfig delivery = new Config.DeliveryConfig();
        delivery.storage = 3;
        delivery.time = 200;

        config.bakers = List.of(baker);
        config.delivers = List.of(delivery);

        assertEquals(10, config.storage);
        assertEquals(1, config.bakers.size());
        assertEquals(5, config.bakers.get(0).speed);
        assertEquals(1, config.delivers.size());
        assertEquals(3, config.delivers.get(0).storage);
        assertEquals(200, config.delivers.get(0).time);
    }

    @Test
    void configSupportsMultipleBakersAndDeliveries() {
        Config config = new Config();
        config.storage = 20;

        List<Config.BakerConfig> bakers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Config.BakerConfig b = new Config.BakerConfig();
            b.speed = i * 2;
            bakers.add(b);
        }
        config.bakers = bakers;

        List<Config.DeliveryConfig> deliveries = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Config.DeliveryConfig d = new Config.DeliveryConfig();
            d.storage = i * 3;
            d.time = i * 100;
            deliveries.add(d);
        }
        config.delivers = deliveries;

        assertEquals(3, config.bakers.size());
        assertEquals(2, config.delivers.size());
        assertEquals(2, config.bakers.get(0).speed);
        assertEquals(4, config.bakers.get(1).speed);
        assertEquals(6, config.bakers.get(2).speed);
    }

    @Test
    void defaultFieldsAreNull() {
        Config config = new Config();
        assertEquals(0, config.storage);
        assertNull(config.bakers);
        assertNull(config.delivers);
    }
}
