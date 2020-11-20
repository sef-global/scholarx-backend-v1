package org.sefglobal.scholarx.controller;

import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.IntrospectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
public class IntrospectionController {

    private final IntrospectionService introspectionService;

    public IntrospectionController(IntrospectionService introspectionService) {
        this.introspectionService = introspectionService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Profile getLoggedInUser(@CookieValue(value = "profileId", defaultValue = "-1") long profileId)
            throws ResourceNotFoundException, UnauthorizedException {
        return introspectionService.getLoggedInUser(profileId);
    }

    @GetMapping("/programs/mentee")
    @ResponseStatus(HttpStatus.OK)
    public List<Program> getMenteeingPrograms(@CookieValue(value = "profileId") long profileId)
            throws ResourceNotFoundException, NoContentException {
        return introspectionService.getMenteeingPrograms(profileId);
    }

    @GetMapping("/programs/mentor")
    @ResponseStatus(HttpStatus.OK)
    public List<Program> getMentoringPrograms(@CookieValue(value = "profileId") long profileId)
            throws ResourceNotFoundException, NoContentException {
        return introspectionService.getMentoringPrograms(profileId);
    }
}
