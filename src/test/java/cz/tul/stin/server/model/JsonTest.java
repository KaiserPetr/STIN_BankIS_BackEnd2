package cz.tul.stin.server.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class JsonTest {

    private final String GET_URL = "https://jsonplaceholder.typicode.com/posts";
    private final String GET_URL_OBJ = "https://api.npoint.io/a85768b55f75d24e90b7";
    private final String POST_URL = "https://jsonplaceholder.typicode.com/posts";

    @Test
    void getJsonObjectTest() throws Exception {
        JSONObject jsonObject = Json.getJsonObject(GET_URL_OBJ);
        Assertions.assertNotNull(jsonObject);
        Assertions.assertTrue(jsonObject.containsKey("what"));
    }

    @Test
    void getJsonArrayTest() throws Exception {
        JSONArray jsonArray = Json.getJsonArray(GET_URL);
        Assertions.assertNotNull(jsonArray);
        Assertions.assertEquals(100, jsonArray.size());
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        Assertions.assertTrue(jsonObject.containsKey("userId"));
        Assertions.assertTrue(jsonObject.containsKey("id"));
        Assertions.assertTrue(jsonObject.containsKey("title"));
        Assertions.assertTrue(jsonObject.containsKey("body"));
    }

    @Test
    void postJsonObjectTest() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", 1);
        jsonObject.put("id", 1);
        jsonObject.put("title", "Test Title");
        jsonObject.put("body", "Test Body");

        Json.postJsonObject(POST_URL, jsonObject);
    }

    @Test
    void postJsonArrayTest() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", 1);
        jsonObject.put("id", 1);
        jsonObject.put("title", "Test Title");
        jsonObject.put("body", "Test Body");

        List<JSONObject> jsonObjects = new ArrayList<>();
        jsonObjects.add(jsonObject);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(jsonObjects);

        Json.postJsonArray(POST_URL, jsonArray);
    }
}
