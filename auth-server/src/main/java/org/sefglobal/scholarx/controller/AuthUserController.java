package org.sefglobal.scholarx.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthUserController {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Object getLoggedUser(Authentication authentication) {
        return authentication.getPrincipal();
    }
}
