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
    public String login(@RequestBody String clientId) throws Exception{
        return clientId.replace("=","");

    }

}



