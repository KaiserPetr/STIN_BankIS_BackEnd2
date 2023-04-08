package cz.tul.stin.server.model;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserTest {

    @BeforeEach
    public void setup() throws Exception {
        // create a test file with one account
       Bank.JSON_USERS = "https://api.npoint.io/196aa5f15e01f2b0d718";

        String data = "[{\"id\":1234,\"email\":\"petr.kaiser1@seznam.cz\",\"surname\":\"Kaiser\",\"firstname\":\"Petr\"},{\"id\":4321,\"email\":\"petr.kaiser1@seznam.cz\",\"surname\":\"Novák\",\"firstname\":\"Jan\"}]";
        JSONParser parser = new JSONParser();
        JSONArray ja = (JSONArray) parser.parse(data);;

        Json.postJsonArray(Bank.JSON_USERS,ja);
    }

    @Test
    public void testGetUserData() throws Exception {
        // Test valid user ID
        User user = User.getUserData(1234);
        assert user != null;
        Assertions.assertEquals(1234, user.getId());
        Assertions.assertEquals("Petr", user.getFirstname());
        Assertions.assertEquals("Kaiser", user.getSurname());
        Assertions.assertEquals("petr.kaiser1@seznam.cz", user.getEmail());
        // Test invalid user ID
        User user2 = User.getUserData(999);
        Assertions.assertNull(user2);
    }

    @Test
    public void testGetUserAccounts() throws Exception {
        // Test user with no accounts
        List<Account> accounts = User.getUserAccounts(3);
        Assertions.assertEquals(0, accounts.size());

        // Test user with one account
        accounts = User.getUserAccounts(4321);
        Assertions.assertEquals(1, accounts.size());
        Account account = accounts.get(0);
        Assertions.assertEquals(4321, account.getOwnerID());
        Assertions.assertEquals(753195423, account.getAccountNumber());
        Assertions.assertEquals(200, account.getWrbtr());
        Assertions.assertEquals("CZK", account.getWaers());

        // Test user with multiple accounts
        accounts = User.getUserAccounts(1234);
        Assertions.assertEquals(2, accounts.size());
    }

    @AfterEach
    public void cleanup() {
        Bank.JSON_USERS = "https://api.npoint.io/9623327c5439cec96d2b";
    }

}

