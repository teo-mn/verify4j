package io.corexchain;

import java.util.Map;
import java.util.TreeMap;

public class JsonUtils {
    public static String jsonMapToString(Map<String, Object> json) {
        StringBuilder result = new StringBuilder();
        TreeMap<String, Object> sorted = new TreeMap<>(json);
        boolean first = true;
        result.append("{");
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            if(!first) result.append(",");
            first = false;
            result.append("\"").append(entry.getKey()).append("\"").append(":");
            if (entry.getValue() instanceof String) {
                result.append("\"").append(entry.getValue()).append("\"");
            } else if (entry.getValue() instanceof Integer) {
                result.append(entry.getValue());
            } else if (entry.getValue() instanceof Map) {
                result.append(JsonUtils.jsonMapToString((Map<String, Object>) entry.getValue()));
            } else {
                result.append(entry.getValue().toString());
            }
        }
        result.append("}");
        return result.toString();
    }
}
