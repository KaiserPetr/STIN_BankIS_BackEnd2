package cz.tul.stin.server.controller;
import cz.tul.stin.server.model.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.service.EmailSenderService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("https://stellular-maamoul-21d188.netlify.app/")
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestBody String clientId) {
        String data = "[{\"id\":1234,\"email\":\"petr.kaiser1@seznam.cz\",\"surname\":\"Kaiser\",\"firstname\":\"Petr\"},{\"id\":4321,\"email\":\"petr.kaiser1@seznam.cz\",\"surname\":\"Novák\",\"firstname\":\"Jan\"}]";
        JSONParser parser = new JSONParser();
        JSONArray ja = null;
        try {
            ja = (JSONArray) parser.parse(data);
        } catch (ParseException e) {
            return null;
        }

        for (Object o : ja) {
            JSONObject joi = (JSONObject) o;
            int jId = Integer.parseInt(joi.get(Const.JKEY_ID).toString());
            if (jId == Integer.parseInt(clientId.replace("=", ""))) {

                String firstName = joi.get(Const.JKEY_FIRSTNAME).toString();
                String surName = joi.get(Const.JKEY_SURNAME).toString();
                String email = joi.get(Const.JKEY_EMAIL).toString();
                return clientId.replace("=", "");
            }
        }
        return "-1";
    }
}



