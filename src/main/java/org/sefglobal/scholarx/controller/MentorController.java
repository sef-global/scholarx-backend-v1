package org.sefglobal.scholarx.controller;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mentors")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @PostMapping("/{id}/mentee")
    @ResponseStatus(HttpStatus.CREATED)
    public Mentee applyAsMentee(@PathVariable long id,
                                @Valid @RequestBody Mentee mentee)
            throws ResourceNotFoundException, BadRequestException {
        long profileId = 1; // TODO: Get the profileId from headers
        return mentorService.applyAsMentee(id, profileId, mentee);
    }

    @GetMapping("/{id}/mentees")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentee> getMenteesOfMentor(@PathVariable long id,
                                           @RequestParam Optional<EnrolmentState> state)
            throws ResourceNotFoundException, BadRequestException {
        return mentorService.getAllMenteesOfMentor(id, state);
    }
}
