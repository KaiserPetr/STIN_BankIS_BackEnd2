package cz.tul.stin.server.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Json {
    public static JSONObject getJsonObject(String urlString) throws Exception {
        URL url = new URL(urlString);
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new InputStreamReader(url.openStream()));
    }

    public static JSONArray getJsonArray(String urlString) throws Exception {
        URL url = new URL(urlString);
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(new InputStreamReader(url.openStream()));
    }
    public static void postJsonArray(String url, JSONArray ja) throws Exception {

        String json = ja.toJSONString();
        openHttpConnection(url, json);
    }

    public static void postJsonObject(String url, JSONObject jo) throws Exception {

        String json = jo.toJSONString();
        openHttpConnection(url, json);
    }

    public static void openHttpConnection(String url, String json) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(json);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Response Code: " + responseCode);
    }
}
