package cz.tul.stin.server.controller;
import cz.tul.stin.server.model.*;
import cz.tul.stin.server.config.Const;
import cz.tul.stin.server.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("https://stellular-maamoul-21d188.netlify.app/")
public class LoginController {

    @PostMapping("/login")
    public String login(@RequestBody String clientId) throws Exception {
        User client = User.getUserData(Integer.parseInt(clientId.replace("=", "")));

        if (client != null) {
            String code = Bank.generateRandomCode();
            return code;
        } else {
            return "-1";
        }
    }
}



