package utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class TestDataLoader {

    private static final JsonObject data;

    static {
        try (Reader reader = new InputStreamReader(
                TestDataLoader.class.getResourceAsStream("/data/test-user.json"), StandardCharsets.UTF_8)) {
            data = new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test-user.json", e);
        }
    }

    public static String get(String key) {
        return data.get(key).getAsString();
    }
}
