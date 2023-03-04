package org.sefglobal.scholarx.controller;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonView;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.util.Views;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mentors")
public class MentorController {

  private final MentorService mentorService;

  public MentorController(MentorService mentorService) {
    this.mentorService = mentorService;
  }

  @JsonView(Views.Public.class)
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Mentor> getAllMentors() {
    return mentorService.getAllMentors();
  }

  @JsonView(Views.Public.class)
  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mentor getMentorById(@PathVariable long id)
    throws ResourceNotFoundException {
    return mentorService.getMentorById(id);
  }

  @PostMapping("/{id}/mentee")
  @ResponseStatus(HttpStatus.CREATED)
  public Mentee applyAsMentee(
    @PathVariable long id,
    Authentication authentication,
    @Valid @RequestBody Mentee mentee
  )
    throws ResourceNotFoundException, BadRequestException {
    Profile profile = (Profile) authentication.getPrincipal();
    return mentorService.applyAsMentee(id, profile.getId(), mentee);
  }

  @GetMapping("/{id}/mentees")
  @ResponseStatus(HttpStatus.OK)
  public List<Mentee> getMenteesOfMentor(
    @PathVariable long id,
    @RequestParam Optional<EnrolmentState> state
  )
    throws ResourceNotFoundException, BadRequestException {
    return mentorService.getAllMenteesOfMentor(id, state);
  }
}
