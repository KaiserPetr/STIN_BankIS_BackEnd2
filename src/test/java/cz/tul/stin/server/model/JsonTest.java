package cz.tul.stin.server.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonTest {

    @Test
    void testGetJsonObject() throws Exception {
        // Given
        String url = "https://jsonplaceholder.typicode.com/posts/1";

        // When
        JSONObject jsonObject = Json.getJsonObject(url);

        // Then
        assertEquals(1L, jsonObject.get("id"));
        assertEquals("sunt aut facere repellat provident occaecati excepturi optio reprehenderit", jsonObject.get("title"));
    }

    @Test
    void testGetJsonArray() throws Exception {
        // Given
        String url = "https://jsonplaceholder.typicode.com/posts";

        // When
        JSONArray jsonArray = Json.getJsonArray(url);

        // Then
        assertEquals(100, jsonArray.size());
    }

    @Test
    void testPostJsonObject() throws Exception {
        // Given
        String url = "https://jsonplaceholder.typicode.com/posts";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "foo");
        jsonObject.put("body", "bar");
        jsonObject.put("userId", 1);

        // When
        Json.postJsonObject(url, jsonObject);

        // Then
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void testPostJsonArray() throws Exception {
        // Given
        String url = "https://jsonplaceholder.typicode.com/posts";
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title", "foo");
        jsonObject1.put("body", "bar");
        jsonObject1.put("userId", 1);
        jsonArray.add(jsonObject1);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("title", "baz");
        jsonObject2.put("body", "qux");
        jsonObject2.put("userId", 2);
        jsonArray.add(jsonObject2);

        // When
        Json.postJsonArray(url, jsonArray);

        // Then
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");
        assertEquals(201, connection.getResponseCode());
    }

    @Test
    void testOpenHttpConnection() {
        // Given
        String url = "http://www.example.com";
        String json = "{\"foo\":\"bar\"}";

        // When
        assertThrows(IOException.class, () -> Json.openHttpConnection(url, json));
    }
}

