package org.sefglobal.scholarx.controller;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.ProgramService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/programs")
public class ProgramController {
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Program> getAllPrograms() {
        return programService.getAllPrograms();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Program getProgramById(@PathVariable long id) throws ResourceNotFoundException {
        return programService.getProgramById(id);
    }

    @GetMapping("/{id}/mentors")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentor> getAllMentorsByProgramId(@PathVariable long id,
                                                 @RequestParam(required = false) List<EnrolmentState> states)
            throws ResourceNotFoundException {
        return programService.getAllMentorsByProgramId(id, states);
    }

    @PostMapping("/{id}/mentor")
    @ResponseStatus(HttpStatus.CREATED)
    public Mentor applyAsMentor(@PathVariable long id,
                                @CookieValue(value = "profileId") long profileId,
                                @Valid @RequestBody Mentor mentor)
            throws ResourceNotFoundException, BadRequestException {
        return programService.applyAsMentor(id, profileId, mentor);
    }

    @GetMapping("/{id}/mentor")
    @ResponseStatus(HttpStatus.OK)
    public Mentor getLoggedInMentor(@PathVariable long id,
                                    @CookieValue(value = "profileId") long profileId)
            throws ResourceNotFoundException {
        return programService.getLoggedInMentor(id, profileId);
    }

    @PutMapping("/{id}/application")
    @ResponseStatus(HttpStatus.OK)
    public Mentor updateMentorData(@PathVariable long id,
                                   @CookieValue(value = "profileId") long profileId,
                                   @Valid @RequestBody Mentor mentor)
            throws ResourceNotFoundException, BadRequestException {
        return programService.updateMentorData(profileId, id, mentor);
    }

    @GetMapping("/{id}/mentee/mentors")
    @ResponseStatus(HttpStatus.OK)
    public List<Mentor> getAppliedMentors(@PathVariable long id,
                                          @RequestParam(required = false) List<EnrolmentState> menteeStates,
                                          @CookieValue(value = "profileId") long profileId)
            throws NoContentException {
        return programService.getAppliedMentorsOfMentee(id, menteeStates, profileId);
    }

    @GetMapping("/{id}/mentee/mentor")
    @ResponseStatus(HttpStatus.OK)
    public Mentor getSelectedMentor(@PathVariable long id,
                                    @CookieValue(value = "profileId") long profileId)
            throws ResourceNotFoundException, NoContentException {
        return programService.getSelectedMentor(id, profileId);
    }
}
