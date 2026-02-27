package pizzahut;

import java.util.List;

public class Config {
    public int storage;
    public List<BakerConfig> bakers;
    public List<DeliveryConfig> delivers;

    public static class BakerConfig {
        public int speed;
    }

    public static class DeliveryConfig {
        public int storage;
        public int time;
    }
}