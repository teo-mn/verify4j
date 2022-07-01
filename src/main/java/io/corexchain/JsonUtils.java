package io.corexchain;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JsonUtils {
    public static String jsonMapToString(Object data) {
        StringBuilder result = new StringBuilder();
        if (data == null) {
            result.append("null");
        }
        else if (data instanceof String) {
            result.append("\"").append(data).append("\"");
        } else if(data instanceof Integer) {
            result.append(data);
        } else if(data instanceof Double) {
            result.append(data);
        } else if(data instanceof Float) {
            result.append(data);
        } else if(data instanceof List) {
            result.append("[");
            boolean first2 = true;
            for (Object item : (List)data){
                if (!first2) result.append(",");
                result.append(jsonMapToString(item));
                first2 = false;
            }
            result.append("]");
        } else if(data instanceof Map) {
            TreeMap<String, Object> sorted = new TreeMap<>((Map<String, Object>)data);
            boolean first = true;
            result.append("{");

            for (Map.Entry<String, Object> entry : sorted.entrySet()) {
                if (!first) result.append(",");
                first = false;
                result.append("\"").append(entry.getKey()).append("\"").append(":");
                result.append(jsonMapToString(entry.getValue()));
            }
            result.append("}");
        } else {
            result.append(data);
        }
        return result.toString().toLowerCase();
    }
}
