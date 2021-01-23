package org.sefglobal.scholarx.controller;

import org.sefglobal.scholarx.model.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthUserController {

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Profile getLoggedUser(@AuthenticationPrincipal Profile profile) {
        return profile;
    }
}
