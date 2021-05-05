package org.sefglobal.scholarx.controller;

import java.util.List;
import javax.validation.Valid;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.*;
import org.sefglobal.scholarx.service.ProgramService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.sefglobal.scholarx.util.ProgramState;
import org.sefglobal.scholarx.util.QuestionCategory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

  private final ProgramService programService;

  public ProgramController(ProgramService programService) {
    this.programService = programService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Program> getAllPrograms(@RequestParam(required = false) List<ProgramState> states) {
    return programService.getAllPrograms(states);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Program getProgramById(@PathVariable long id)
    throws ResourceNotFoundException {
    return programService.getProgramById(id);
  }

  @GetMapping("/{id}/mentors")
  @ResponseStatus(HttpStatus.OK)
  public List<Mentor> getAllMentorsByProgramId(
    @PathVariable long id,
    @RequestParam(required = false) List<EnrolmentState> states
  )
    throws ResourceNotFoundException {
    return programService.getAllMentorsByProgramId(id, states);
  }

  @PostMapping("/{id}/mentor")
  @ResponseStatus(HttpStatus.CREATED)
  public Mentor applyAsMentor(
    @PathVariable long id,
    Authentication authentication,
    @Valid @RequestBody List<MentorResponse> responses
  )
    throws ResourceNotFoundException, BadRequestException {
    Profile profile = (Profile) authentication.getPrincipal();
    return programService.applyAsMentor(id, profile.getId(), responses);
  }

  @GetMapping("/{id}/mentor")
  @ResponseStatus(HttpStatus.OK)
  public Mentor getLoggedInMentor(
    @PathVariable long id,
    Authentication authentication
  )
    throws ResourceNotFoundException {
    Profile profile = (Profile) authentication.getPrincipal();
    return programService.getLoggedInMentor(id, profile.getId());
  }

  @GetMapping("/{id}/mentee/mentors")
  @ResponseStatus(HttpStatus.OK)
  public List<Mentor> getAppliedMentors(
    @PathVariable long id,
    @RequestParam(required = false) List<EnrolmentState> menteeStates,
    Authentication authentication
  )
    throws NoContentException {
    Profile profile = (Profile) authentication.getPrincipal();
    return programService.getAppliedMentorsOfMentee(
      id,
      menteeStates,
      profile.getId()
    );
  }

  @GetMapping("/{id}/mentee/mentor")
  @ResponseStatus(HttpStatus.OK)
  public Mentor getSelectedMentor(
    @PathVariable long id,
    Authentication authentication
  )
    throws ResourceNotFoundException, NoContentException {
    Profile profile = (Profile) authentication.getPrincipal();
    return programService.getSelectedMentor(id, profile.getId());
  }

  @GetMapping("/{id}/questions/{category}")
  @ResponseStatus(HttpStatus.OK)
  public List<Question> getQuestions(@PathVariable long id,
                                     @PathVariable QuestionCategory category) throws ResourceNotFoundException {
    return programService.getQuestions(id, category);
  }

  @GetMapping("/{id}/responses/mentor")
  @ResponseStatus(HttpStatus.OK)
  public List<MentorResponse> getMentorResponses(
          @PathVariable long id,
          Authentication authentication,
          @RequestParam(required = false) Long mentorId
  )
  throws ResourceNotFoundException {
    if (mentorId != null) {
      return programService.getMentorResponses(mentorId);
    }
    Profile profile = (Profile) authentication.getPrincipal();
    return programService.getMentorResponses(id, profile.getId());
  }

  @PutMapping("/{id}/responses/mentor")
  public List<MentorResponse> editMentorResponses(
          @PathVariable long id,
          Authentication authentication,
          @RequestBody List<MentorResponse> responses
  )
  throws ResourceNotFoundException{
    Profile profile = (Profile) authentication.getPrincipal();
    return programService.editMentorResponses(id, profile.getId(), responses);
  }
}
