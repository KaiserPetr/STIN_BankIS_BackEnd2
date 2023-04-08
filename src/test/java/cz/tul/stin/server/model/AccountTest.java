package cz.tul.stin.server.model;

import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountTest {

    @BeforeEach
    public void setup() throws Exception {
        // create a test file with one account
        Bank.JSON_ACCOUNTS = "https://api.npoint.io/e8135a468b3f9070545d";
        // post test data
        String data = "[{\"waers\":\"CZK\",\"wrbtr\":1000,\"ownerID\":456,\"accountNumber\":123}]";
        JSONParser parser = new JSONParser();
        JSONArray ja = (JSONArray) parser.parse(data);;

        Json.postJsonArray(Bank.JSON_ACCOUNTS,ja);
    }

    @Test
    public void testCreateNewAccount() throws Exception {
        int result = Account.createNewAccount(789, 456, "CZK");
        Assertions.assertEquals(0, result);
        // check that the account was added to the file
        JSONArray ja = Json.getJsonArray(Bank.JSON_ACCOUNTS);
        JSONObject newAccount = (JSONObject) ja.get(1);
        Assertions.assertEquals(456, Integer.parseInt(newAccount.get(Const.JKEY_ACCOUNT_NUMBER).toString()));
        Assertions.assertEquals(0, Integer.parseInt(newAccount.get(Const.JKEY_WRBTR).toString()));
        Assertions.assertEquals("CZK", newAccount.get(Const.JKEY_WAERS));
        Assertions.assertEquals(789, Integer.parseInt(newAccount.get(Const.JKEY_OWNER_ID).toString()));
    }

    @Test
    public void testCreateExistingAccount() throws Exception {
        int result = Account.createNewAccount(789, 123, "CZK");
        Assertions.assertEquals(1, result);
    }

    @Test
    public void testGetUsersCZKAccount() throws Exception {
        Account account = Account.getUsersCZKAccount(456);
        Assertions.assertEquals(123, account.getAccountNumber());
        Assertions.assertEquals(1000.0, account.getWrbtr());
        Assertions.assertEquals("CZK", account.getWaers());
        Assertions.assertEquals(456, account.getOwnerID());
    }

    @Test
    public void testUpdateAccountBalance() throws Exception {
        Account.updateAccountBalance(123, 2000);
        // check that the balance was updated in the file
        JSONArray ja = Json.getJsonArray(Bank.JSON_ACCOUNTS);
        JSONObject updatedAccount = (JSONObject) ja.get(0);
        Assertions.assertEquals(2000.0, updatedAccount.get(Const.JKEY_WRBTR));
    }

    @Test
    public void testCheckIfAccountExists() throws Exception {
        Assertions.assertTrue(Account.checkIfAccountExists(123));
        Assertions.assertFalse(Account.checkIfAccountExists(789));
    }

    @Test
    public void testGetAccountFromJson() throws Exception {
        Account account = Account.getAccountFromJson(123);
        Assertions.assertEquals(456, account.getOwnerID());
        Assertions.assertEquals(123, account.getAccountNumber());
        Assertions.assertEquals(1000, account.getWrbtr());
        Assertions.assertEquals("CZK", account.getWaers());
    }

    @Test
    public void testGetAccountFromJsonNotFound() {
        Assertions.assertThrows(RuntimeException.class, () -> Account.getAccountFromJson(999999));
    }

    @AfterEach
    public void cleanup() {
        // delete the test file
        Bank.JSON_ACCOUNTS = "https://api.npoint.io/4039555b9d988523ca33";
    }


}

