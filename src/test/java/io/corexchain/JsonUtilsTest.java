package io.corexchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Assert;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class JsonUtilsTest extends TestCase {
    public void testHash0() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(new File("./src/test/java/io/corexchain/1.json"), HashMap.class);
        assertEquals("{\"array\":[{\"blank\":\"\",\"blank_array\":[],\"number\":1,\"string\":\"string test\",\"test\":{\"test\":2,\"test2\":\"test2\"}}],\"array2\":[1,2,\"3\"],\"array3\":[1,2,3],\"array4\":[\"1\",\"2\",\"3\"],\"blank\":\"\",\"blank_array\":[],\"boolean\":true,\"boolean2\":false,\"nested\":{\"nested\":{\"array\":[],\"array2\":[{}],\"blank\":\"\",\"nested\":{},\"number\":1,\"string\":\"saasda\"}},\"null\":null,\"number\":1,\"string\":\"string test\"}",
                JsonUtils.jsonMapToString(json));
    }
}