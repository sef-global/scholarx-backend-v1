package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class IntrospectionService {

    private final static Logger log = LoggerFactory.getLogger(IntrospectionService.class);
    private final ProfileRepository profileRepository;
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;

    public IntrospectionService(ProfileRepository profileRepository,
                                MenteeRepository menteeRepository,
                                MentorRepository mentorRepository) {
        this.profileRepository = profileRepository;
        this.menteeRepository = menteeRepository;
        this.mentorRepository = mentorRepository;
    }

    /**
     * Returns a {@link Profile}
     *
     * @param id which is the identifier of the {@link Profile}
     *
     * @throws ResourceNotFoundException if {@link Profile} for {@code id} doesn't exist
     * @throws UnauthorizedException     if the user hasn't logged in
     */
    public Profile getLoggedInUser(long id)
            throws ResourceNotFoundException, UnauthorizedException {
        if (id == -1) {
            String msg = "Error, User hasn't logged in.";
            log.error(msg);
            throw new UnauthorizedException(msg);
        }
        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if (!optionalProfile.isPresent()) {
            String msg = "Error, Profile with id: " + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return optionalProfile.get();
    }

    /**
     * Retrieves all the {@link Program} objects where the user is a {@link Mentee}
     *
     * @param id which is the Profile id of the user
     * @return {@link List} of {@link Program} objects
     *
     * @throws ResourceNotFoundException if the user doesn't exist
     * @throws NoContentException        if the user hasn't enrolled in any program as a mentee
     */
    public List<Program> getMenteeingPrograms(long id)
            throws ResourceNotFoundException, NoContentException {
        if (!profileRepository.existsById(id)) {
            String msg = "Error, Profile with id: " + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        List<Mentee> mentees = menteeRepository.findAllByProfileId(id);
        if (mentees.isEmpty()) {
            String msg = "Error, User has not enrolled in any program as a mentee.";
            log.error(msg);
            throw new NoContentException(msg);
        }
        List<Program> programsList = new ArrayList<>();
        for (Mentee mentee : mentees) {
            programsList.add(mentee.getProgram());
        }
        Set<Program> programSet = new HashSet<>(programsList);
        programsList.clear();
        programsList.addAll(programSet); // TODO: find another way to remove duplicates

        return programsList;
    }

    /**
     * Retrieves all the {@link Program} objects where the user is a {@link Mentor}
     *
     * @param id which is the Profile id of the user
     * @return {@link List} of {@link Program} objects
     *
     * @throws ResourceNotFoundException if the user doesn't exist
     * @throws NoContentException        if the user hasn't enrolled in any program as a mentor
     */
    public List<Program> getMentoringPrograms(long id)
            throws ResourceNotFoundException, NoContentException {
        if (!profileRepository.existsById(id)) {
            String msg = "Error, Profile with id: " + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        List<Mentor> mentors = mentorRepository.findAllByProfileId(id);
        if (mentors.isEmpty()) {
            String msg = "Error, User has not enrolled in any program as a mentor.";
            log.error(msg);
            throw new NoContentException(msg);
        }
        List<Program> programsList = new ArrayList<>();
        for (Mentor mentor : mentors) {
            programsList.add(mentor.getProgram());
        }
        Set<Program> programSet = new HashSet<>(programsList);
        programsList.clear();
        programsList.addAll(programSet); // TODO: find another way to remove duplicates

        return programsList;
    }
}
