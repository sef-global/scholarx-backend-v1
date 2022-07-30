package org.sefglobal.scholarx.controller;

import java.util.List;
import javax.validation.Valid;
import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.service.IntrospectionService;
import org.sefglobal.scholarx.service.ProfileService;
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
@RequestMapping("/api/me")
public class AuthUserController {

  private final IntrospectionService introspectionService;
  private final ProfileService profileService;

  public AuthUserController(IntrospectionService introspectionService, ProfileService profileService) {
    this.introspectionService = introspectionService;
    this.profileService = profileService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Profile getLoggedUser(Authentication authentication)
    throws ResourceNotFoundException, UnauthorizedException {
    Profile profile = (Profile) authentication.getPrincipal();
    return profileService.getLoggedUser(profile.getId());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public Profile updateUserDetails(
          Authentication authentication,
          @Valid @RequestBody Profile profileDetails)
          throws ResourceNotFoundException {
    Profile profile = (Profile) authentication.getPrincipal();
    return profileService.updateUserDetails(profile.getId(), profileDetails);
  }

  @GetMapping("/programs/mentee")
  @ResponseStatus(HttpStatus.OK)
  public List<Program> getMenteeingPrograms(Authentication authentication)
    throws ResourceNotFoundException, NoContentException {
    Profile profile = (Profile) authentication.getPrincipal();
    return introspectionService.getMenteeingPrograms(profile.getId());
  }

  @GetMapping("/programs/mentor")
  @ResponseStatus(HttpStatus.OK)
  public List<Program> getMentoringPrograms(Authentication authentication,
                                            @RequestParam(required = false) EnrolmentState mentorState)
    throws ResourceNotFoundException, NoContentException {
    Profile profile = (Profile) authentication.getPrincipal();
    return introspectionService.getMentoringPrograms(profile.getId(), mentorState);
  }

  @GetMapping("/programs/{id}/mentees")
  @ResponseStatus(HttpStatus.OK)
  public List<Mentee> getMentees(
    Authentication authentication,
    @PathVariable long id,
    @RequestParam(required = false) List<EnrolmentState> menteeStates
  )
    throws ResourceNotFoundException, NoContentException {
    Profile profile = (Profile) authentication.getPrincipal();
    return introspectionService.getMentees(id, profile.getId(), menteeStates);
  }
}
