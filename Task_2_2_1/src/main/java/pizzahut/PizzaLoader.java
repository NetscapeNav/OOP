package pizzahut;

import com.google.gson.Gson;
import java.io.Reader;

public class PizzaLoader {
    private static class RootConfig {
        Config pizzeria;
    }

    public static Config load(Reader reader) {
        Gson gson = new Gson();
        RootConfig root = gson.fromJson(reader, RootConfig.class);
        return root.pizzeria;
    }
}
