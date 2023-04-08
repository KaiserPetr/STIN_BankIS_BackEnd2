package cz.tul.stin.server.model;
import cz.tul.stin.server.config.Const;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static cz.tul.stin.server.model.Json.getJsonArray;
import static cz.tul.stin.server.model.Json.postJsonArray;

public class Account {
    private int ownerID, accountNumber;
    private float wrbtr;
    private String waers;

    public Account(int ownerID, int accountNumber, float wrbtr, String waers) {
        this.ownerID = ownerID;
        this.accountNumber = accountNumber;
        this.wrbtr = wrbtr;
        this.waers = waers;
    }

    public static Account getAccountFromJson (int accNum) throws Exception {
        JSONArray ja = getJsonArray(Bank.JSON_ACCOUNTS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
            if (jAccNumber == accNum) {
                int ownerID = Integer.parseInt(joi.get(Const.JKEY_OWNER_ID).toString());
                float wrbtr = Float.parseFloat(joi.get(Const.JKEY_WRBTR).toString());
                String waers = joi.get(Const.JKEY_WAERS).toString();
                return new Account(ownerID, jAccNumber, wrbtr, waers);
            }
        }
        throw new RuntimeException("Ucet nenalezen.");
    }

    public static Account getUsersCZKAccount (int ownerID) throws Exception{
        JSONArray ja = getJsonArray(Bank.JSON_ACCOUNTS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jOwnderID = Integer.parseInt(joi.get(Const.JKEY_OWNER_ID).toString());
            String waers = joi.get(Const.JKEY_WAERS).toString();
            if (jOwnderID == ownerID && waers.equals("CZK")) {
                int jAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
                float wrbtr = Float.parseFloat(joi.get(Const.JKEY_WRBTR).toString());
                return new Account(ownerID, jAccNumber, wrbtr, waers);
            }
        }
        throw new RuntimeException("Ucet nenalezen.");
    }

    public static void updateAccountBalance(int accNum, float newBalance) throws Exception{
        JSONArray ja = getJsonArray(Bank.JSON_ACCOUNTS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
            if (jAccNumber == accNum) {
                joi.replace(Const.JKEY_WRBTR,newBalance);
                break;
            }
        }

        postJsonArray(Bank.JSON_ACCOUNTS,ja);
    }

    public static boolean checkIfAccountExists(int accountNumber) throws Exception {
        JSONArray ja = getJsonArray(Bank.JSON_ACCOUNTS);

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jAccNumber = Integer.parseInt(joi.get(Const.JKEY_ACCOUNT_NUMBER).toString());
            if (jAccNumber == accountNumber) {
                return true;
            }
        }
        return false;
    }

    public static int createNewAccount(int ownerID, int accountNumber, String waers) throws Exception{
        if (checkIfAccountExists(accountNumber)){
            return 1;
        } else {
            JSONArray ja = getJsonArray(Bank.JSON_ACCOUNTS);

            JSONObject newAccount = new JSONObject();
            newAccount.put(Const.JKEY_ACCOUNT_NUMBER,accountNumber);
            newAccount.put(Const.JKEY_WRBTR,0);
            newAccount.put(Const.JKEY_WAERS,waers);
            newAccount.put(Const.JKEY_OWNER_ID,ownerID);
            ja.add(newAccount);

            postJsonArray(Bank.JSON_ACCOUNTS,ja);
            return 0;
        }
    }

    public float getWrbtr() {
        return wrbtr;
    }

    public String getWaers() {
        return waers;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getAccountNumber() {
        return accountNumber;
    }
}
