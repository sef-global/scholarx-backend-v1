package org.sefglobal.scholarx.controller;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.IntrospectionService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Profile getLoggedInUser(@AuthenticationPrincipal Profile profile)
            throws ResourceNotFoundException, UnauthorizedException {
        return introspectionService.getLoggedInUser(profile.getId());
    }

    @GetMapping("/programs/mentee")
    @ResponseStatus(HttpStatus.OK)
    public List<Program> getMenteeingPrograms(@AuthenticationPrincipal Profile profile)
            throws ResourceNotFoundException, NoContentException {
        return introspectionService.getMenteeingPrograms(profile.getId());
    }

    @GetMapping("/programs/mentor")
    @ResponseStatus(HttpStatus.OK)
    public List<Program> getMentoringPrograms(@AuthenticationPrincipal Profile profile)
            throws ResourceNotFoundException, NoContentException {
        return introspectionService.getMentoringPrograms(profile.getId());
    }

    @GetMapping("/programs/{id}/mentees")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentee> getMentees(@AuthenticationPrincipal Profile profile,
                                   @PathVariable long id,
                                   @RequestParam(required = false)
                                           List<EnrolmentState> menteeStates)
            throws ResourceNotFoundException, NoContentException {
        return introspectionService.getMentees(id, profile.getId(), menteeStates);
    }

    @PutMapping("/mentor/{id}/confirmation")
    @ResponseStatus(HttpStatus.OK)
    public Mentee confirmMentor(@PathVariable long id,
                                @AuthenticationPrincipal Profile profile)
            throws ResourceNotFoundException, BadRequestException {
        return introspectionService.confirmMentor(id, profile.getId());
    }
}
