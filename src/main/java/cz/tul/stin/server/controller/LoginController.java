package cz.tul.stin.server.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("https://stellular-maamoul-21d188.netlify.app/")
public class LoginController {


    @PostMapping("/login")
    public String login(@RequestBody String clientId){
        return clientId.replace("=","");
        
    }

}



