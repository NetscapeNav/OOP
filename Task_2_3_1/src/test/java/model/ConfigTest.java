package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTest {
    @Test
    void testConfig() {
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        assertTrue(config.getChances().contains(70.0));
        assertTrue(config.getChances().get(2).equals(15.0));
    }
}
