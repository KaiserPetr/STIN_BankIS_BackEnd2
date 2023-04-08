package cz.tul.stin.server.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTest {

    @BeforeEach
    public void setup() throws Exception {

        Bank.JSON_CNB = "https://api.npoint.io/0162c3ff8723f1380bdb";
        // post test data
        String data = "[{\"waers\":\"CZK\",\"wrbtr\":1},{\"waers\":\"EUR\",\"wrbtr\":\"23,410\"},{\"waers\":\"USD\",\"wrbtr\":\"21,450\"},{\"waers\":\"GBP\",\"wrbtr\":\"26,745\"},{\"waers\":\"AUD\",\"wrbtr\":\"14,351\"},{\"waers\":\"CAD\",\"wrbtr\":\"15,919\"},{\"waers\":\"HUF\",\"wrbtr\":\"0,062\"},{\"waers\":\"PLN\",\"wrbtr\":\"4,997\"},{\"waers\":\"CHF\",\"wrbtr\":\"23,693\"}]";
        JSONParser parser = new JSONParser();
        JSONArray ja = (JSONArray) parser.parse(data);;

        Json.postJsonArray(Bank.JSON_CNB,ja);

        Bank.JSON_CNB_DATE = "https://api.npoint.io/96c946958442a78823f0";
        String dataDate = "{\"cnbDate\":\"06.04.2023\"}";
        JSONObject jo = (JSONObject) parser.parse(dataDate);
        Json.postJsonObject(Bank.JSON_CNB_DATE,jo);
    }

    @Test
    void testDownloadExchangeRates() {
        Assertions.assertDoesNotThrow(Bank::downloadExchangeRates);
    }

    @Test
    public void testGetExchanegRateDate() throws Exception {
        Assertions.assertEquals(Bank.getExchanegRateDate(), "06.04.2023");
    }

    @Test
    public void testUpdateExchanegRateDate() throws Exception {
        String newDate = "30.03.2023";
        Bank.updateExchanegRateDate(newDate);
        Assertions.assertEquals(Bank.getExchanegRateDate(), newDate);
    }


    @Test
    public void testUpdateExchangeRate() throws Exception {
        // Before update
        float initialRate = Bank.getExchangeRate("USD");
        float updatedRate = initialRate * 2;

        // Update exchange rate
        Bank.updateExchangeRate(updatedRate, "USD");

        // Check that the exchange rate has been updated
        float newRate = Bank.getExchangeRate("USD");
        Assertions.assertEquals(updatedRate, newRate, 0.001);

        // Restore the original exchange rate
        Bank.updateExchangeRate(initialRate, "USD");
    }

    @Test
    public void testGetExchangeRate() throws Exception {
        // Test the exchange rate for a known currency
        float rate = Bank.getExchangeRate("CZK");
        Assertions.assertTrue(rate > 0);

        // Test that an exception is thrown for an unknown currency
        Assertions.assertThrows(RuntimeException.class, () -> Bank.getExchangeRate("XYZ"));
    }

    @Test
    void testGetExchangeRateInvalidJson() {
        Bank.JSON_CNB = "https://httpbin.org/status/404";
        Assertions.assertThrows(Exception.class, () -> Bank.getExchangeRate("USD"));
    }


    @Test
    public void testGenerateRandomCode() {
        String code = Bank.generateRandomCode();
        Assertions.assertNotNull(code);
        Assertions.assertEquals(code.length(), 4);
    }

    @AfterEach
    public void cleanup() {
        Bank.JSON_CNB = "https://api.npoint.io/6997254c8bfd5df5736a";
        Bank.JSON_CNB_DATE = "https://api.npoint.io/f12d14488063dc28d90e";
    }

}
