package cz.tul.stin.server.controller;
import cz.tul.stin.server.model.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("https://stellular-maamoul-21d188.netlify.app/")
public class LoginController {
    @Autowired
    private EmailSenderService service;

    @PostMapping("/login")
    public String login(@RequestBody String clientId){

        User client;
        try {
            client = User.getUserData(Integer.parseInt(clientId.replace("=", "")));
        } catch (Exception e){
            client = null;
        }
        if (client != null) {
            String code = Bank.generateRandomCode();
            String msg = String.format("Váš kód pro přilášení je: %s", code);
            service.sendSimpleEmail(client.getEmail(), Const.EMAIL_SUBJECT, msg);
            return code;
        } else {
            return "-1";
        }
    }

}



