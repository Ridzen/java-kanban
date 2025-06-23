package http;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonFactory {
    public static Gson build() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Duration.class, new JsonSerializer<Duration>() {
            public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toString());
            }
        });
        builder.registerTypeAdapter(Duration.class, new JsonDeserializer<Duration>() {
            public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return Duration.parse(json.getAsString());
            }
        });
        builder.registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        });
        builder.registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
                return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });
        builder.setPrettyPrinting();
        return builder.create();
    }
}
