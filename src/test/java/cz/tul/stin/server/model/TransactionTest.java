package cz.tul.stin.server.model;

import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cz.tul.stin.server.model.Account.getAccountFromJson;
import static cz.tul.stin.server.model.Account.getUsersCZKAccount;
import static cz.tul.stin.server.model.Transaction.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @BeforeEach
    public void setup() throws Exception {
        Bank.JSON_TRANSACTIONS = "https://api.npoint.io/88ac1f5a8b720f3ead54";
        String data = "[{\"msg\":\"\",\"waers\":\"CZK\",\"wrbtr\":100,\"operation\":\"+\",\"accountNumber\":123456789},{\"msg\":\"\",\"waers\":\"CZK\",\"wrbtr\":171,\"operation\":\"-\",\"accountNumber\":123456789},{\"msg\":\"AUD:CZK~1:14,44\",\"waers\":\"CZK\",\"wrbtr\":1443.6,\"operation\":\"-\",\"accountNumber\":123456789}]";
        JSONParser parser = new JSONParser();
        JSONArray ja = (JSONArray) parser.parse(data);;
        Json.postJsonArray(Bank.JSON_TRANSACTIONS,ja);

        Bank.JSON_ACCOUNTS = "https://api.npoint.io/43d392eb5efbea83d344";
        String dataAccounts = "[{\"waers\":\"CZK\",\"wrbtr\":419490.523,\"ownerID\":1234,\"accountNumber\":123456789},{\"waers\":\"EUR\",\"wrbtr\":1294,\"ownerID\":1234,\"accountNumber\":123789456},{\"waers\":\"CZK\",\"wrbtr\":200,\"ownerID\":4321,\"accountNumber\":753195423}]";
        JSONArray ja2 = (JSONArray) parser.parse(dataAccounts);;
        Json.postJsonArray(Bank.JSON_ACCOUNTS,ja2);
    }

    @Test
    public void testConstructorAndGetters() {
        Transaction t = new Transaction(1234, '+', 100.0f, "USD", "test message");

        assertEquals(1234, t.getAccNum());
        assertEquals('+', t.getOperation());
        assertEquals(100.0f, t.getWrbtr(), 0.001);
        assertEquals("USD", t.getWaers());
        assertEquals("test message", t.getMessage());
    }

    @Test
    public void testConstructorWithoutMessage() {
        Transaction t = new Transaction(1234, '-', 50.0f, "EUR");

        assertEquals(1234, t.getAccNum());
        assertEquals('-', t.getOperation());
        assertEquals(50.0f, t.getWrbtr(), 0.001);
        assertEquals("EUR", t.getWaers());
        assertEquals("", t.getMessage());
    }

    //T bad operator
    @Test
    public void testProvideTransactionBadOperator() throws Exception {
        int result = provideTransaction('x',100,"CZK",1234);
        assertEquals (1, result);
    }

    // T plus CZK
    @Test
    public void testProvideTransactionPlusOnCZK() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        float balanceBefore = czkAccount.getWrbtr();
        int result = provideTransaction('+',100,"CZK",1234);

        assertEquals (czkAccount.getAccountNumber(), result);
        assertEquals(balanceBefore+100.0f, getUsersCZKAccount(1234).getWrbtr(), 0.001);

    }

    // T plus EUR
    @Test
    public void testProvideTransactionPlusOnEUR() throws Exception {

        Account eurAccount = getAccountFromJson(123789456);
        float balanceBefore = eurAccount.getWrbtr();
        int result = provideTransaction('+',100,"EUR",1234);

        assertEquals(eurAccount.getAccountNumber(), result);
        assertEquals(balanceBefore+100.0f, getAccountFromJson(123789456).getWrbtr(), 0.001);
    }

    //T plus diff curr
    @Test
    public void testProvideTransactionPlusOnDiffCurr() throws Exception {
        Account czkAccount = getUsersCZKAccount(1234);
        float balanceBefore = czkAccount.getWrbtr();
        int result = provideTransaction('+',100,"AUD",1234);

        float newBalance = balanceBefore + Bank.getExchangeRate("AUD") * 100;

        assertEquals(czkAccount.getAccountNumber(), result);
        assertEquals(newBalance, getUsersCZKAccount(1234).getWrbtr(), 0.001);
    }

    //T minus CZK ok balance
    @Test
    public void testProvideTransactionMinusOnCZKOK() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        float balanceBefore = czkAccount.getWrbtr();
        int result = provideTransaction('-',100,"CZK",1234);

        assertEquals(czkAccount.getAccountNumber(), result);
        assertEquals(balanceBefore-100.0f, getUsersCZKAccount(1234).getWrbtr(), 0.001);

    }

    //T minus CZK bad
    @Test
    public void testProvideTransactionMinusOnCZKBad() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        int result = provideTransaction('-',100000000,"CZK",1234);

        assertEquals(0, result);
    }

    // T minus EUR ok
    @Test
    public void testProvideTransactionMinusOnEUROk() throws Exception {

        Account eurAccount = getAccountFromJson(123789456);
        float balanceBefore = eurAccount.getWrbtr();
        int result = provideTransaction('-',100,"EUR",1234);

        assertEquals(eurAccount.getAccountNumber(), result);
        assertEquals(balanceBefore-100.0f, getAccountFromJson(123789456).getWrbtr(), 0.001);
    }

    // T minus EUR bad CZK ok
    @Test
    public void testProvideTransactionMinusOnEURBadCZKOk() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        float balanceBefore = czkAccount.getWrbtr();
        int result = provideTransaction('-',2000,"EUR",1234);

        float newBalance = balanceBefore - Bank.getExchangeRate("EUR") * 2000;

        assertEquals(czkAccount.getAccountNumber(), result);
        assertEquals(newBalance, getUsersCZKAccount(1234).getWrbtr(), 0.001);
    }

    // T minus EUR bad CZK bad
    @Test
    public void testProvideTransactionMinusOnEURBadCZKBad() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        int result = provideTransaction('-',200000,"EUR",1234);

        assertEquals(0, result);
    }

    // T minus dif curr CZK ok
    @Test
    public void testProvideTransactionMinusOnDiffCurrCZKOk() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        float balanceBefore = czkAccount.getWrbtr();
        int result = provideTransaction('-',100,"AUD",1234);

        float newBalance = balanceBefore - Bank.getExchangeRate("AUD") * 100;

        assertEquals(czkAccount.getAccountNumber(), result);
        assertEquals(newBalance, getUsersCZKAccount(1234).getWrbtr(), 0.001);
    }

    // T minus diff curr CZK bad
    @Test
    public void testProvideTransactionMinusOnDiffCurrCZKBad() throws Exception {

        Account czkAccount = getUsersCZKAccount(1234);
        int result = provideTransaction('-',2000000,"AUD",1234);

        assertEquals(0, result);
    }
    @Test
    public void testWriteTransactionToJson_addNewTransaction() throws Exception {
        // Create a new transaction object
        Transaction t = new Transaction(123456, '+', 1000.f , "USD", "Test deposit");

        // Write the transaction to the JSON file
        writeTransactionToJson(t);

        // Check that the new transaction was added to the JSON file
        JSONArray ja = Json.getJsonArray(Bank.JSON_TRANSACTIONS);
        JSONObject lastTransaction = (JSONObject) ja.get(ja.size() - 1);
        assertEquals(t.getAccNum(), Integer.parseInt(lastTransaction.get(Const.JKEY_ACCOUNT_NUMBER).toString()));
        assertEquals(String.valueOf(t.getOperation()), lastTransaction.get(Const.JKEY_OPERATION));
        assertEquals(t.getWrbtr(), Float.valueOf(lastTransaction.get(Const.JKEY_WRBTR).toString()));
        assertEquals(t.getWaers(), lastTransaction.get(Const.JKEY_WAERS));
        assertEquals(t.getMessage(), lastTransaction.get(Const.JKEY_MESSAGE));
    }

    @Test
    public void testGenerateRandomTransaction_correctAccountNumber() {
        int accNum = 123456;
        // Generate a random transaction with the given account number
        Transaction t = generateRandomTransaction(accNum);
        // Check that the generated transaction has the correct account number
        assertEquals(accNum, t.getAccNum());
    }

    @Test
    public void testGenerateRandomTransaction_validOperator() {
        int accNum = 123456;
        // Generate a random transaction with the given account number
        Transaction t = generateRandomTransaction(accNum);
        // Check that the generated transaction has a valid operator (+ or -)
        assertTrue(t.getOperation() == '+' || t.getOperation() == '-');
    }
    @Test
    public void testGenerateRandomTransaction_validCurrency() {
        int accNum = 123456;
        // Generate a random transaction with the given account number
        Transaction t = generateRandomTransaction(accNum);
        // Check that the generated transaction has a valid currency
        assertTrue(Arrays.asList(Bank.CURRENCIES).contains(t.getWaers()));
    }
    @Test
    public void testGenerateRandomTransaction_validAmount() {
        int accNum = 123456;
        // Generate a random transaction with the given account number
        Transaction t = generateRandomTransaction(accNum);
        // Check that the generated transaction has a valid amount (between 0 and 1000)
        assertTrue(t.getWrbtr() >= 0 && t.getWrbtr() <= 1000);
    }

    @Test
    public void testGetTransactionsNoTransactions() throws Exception {
        int accNumber = 123456;
        List<Transaction> transactions = getTransactions(accNumber);
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void testGetTransactionsAllTransactions() throws Exception {
        int accNumber = 123456789;
        List<Transaction> expectedTransactions = new ArrayList<>();
        expectedTransactions.add(new Transaction(accNumber, '+', 100.0f, "CZK", ""));
        expectedTransactions.add(new Transaction(accNumber, '-', 171.0f, "CZK", ""));
        expectedTransactions.add(new Transaction(accNumber, '-', 1443.6f, "CZK", "AUD:CZK~1:14,44"));
        List<Transaction> actualTransactions = getTransactions(accNumber);
        for(int i = 0; i < actualTransactions.size();i++){
            assertEquals(expectedTransactions.get(i).getAccNum(), actualTransactions.get(i).getAccNum());
            assertEquals(expectedTransactions.get(i).getOperation(), actualTransactions.get(i).getOperation());
            assertEquals(expectedTransactions.get(i).getWrbtr(), actualTransactions.get(i).getWrbtr());
            assertEquals(expectedTransactions.get(i).getWaers(), actualTransactions.get(i).getWaers());
            assertEquals(expectedTransactions.get(i).getMessage(), actualTransactions.get(i).getMessage());
        }
    }

    @AfterEach
    public void cleanup() {
        Bank.JSON_TRANSACTIONS = "https://api.npoint.io/00dd6d5cbcab26314a7b";
        Bank.JSON_ACCOUNTS = "https://api.npoint.io/4039555b9d988523ca33";
    }
}

