package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MentorService {

    private final static Logger log = LoggerFactory.getLogger(MentorService.class);
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final ProfileRepository profileRepository;

    public MentorService(MentorRepository mentorRepository,
                         MenteeRepository menteeRepository,
                         ProfileRepository profileRepository) {
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Retrieves all the {@link Mentor} objects
     *
     * @return {@link List} of {@link Mentor} objects
     */
    public List<Mentor> getAllMentors() {
        return mentorRepository.findAll();
    }

    /**
     * Retrieves the {@link Mentor} filtered from {@code id}
     *
     * @param id which is the id of the filtering {@link Mentor}
     * @return {@link Mentor}
     *
     * @throws ResourceNotFoundException if the requesting {@link Mentor} doesn't exist
     */
    public Mentor getMentorById(long id) throws ResourceNotFoundException {
        Optional<Mentor> mentor = mentorRepository.findById(id);
        if (!mentor.isPresent()) {
            String msg = "Error, Mentor by id: " + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return mentor.get();
    }

    /**
     * Update a {@link EnrolmentState} of a {@link Mentor}
     *
     * @param id             which is the {@link Mentor} to be updated
     * @param enrolmentState which is the {@link EnrolmentState} of the program to be updated
     * @return the updated {@link Mentor}
     *
     * @throws ResourceNotFoundException is thrown if the requesting {@link Mentor} doesn't exist
     */
    public Mentor updateState(long id, EnrolmentState enrolmentState)
            throws ResourceNotFoundException {
        Optional<Mentor> optionalMentor = mentorRepository.findById(id);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentor with id: " + id + " cannot be updated. " +
                         "Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        optionalMentor.get().setState(enrolmentState);
        return mentorRepository.save(optionalMentor.get());
    }

    /**
     * Create new {@link Mentee}
     *
     * @param profileId which is the Profile id of the applying user's {@link Profile}
     * @param mentorId  which is mentor id for the requesting {@link Mentor}
     * @param mentee    which holds the data to be added
     * @return the created {@link Mentee}
     *
     * @throws ResourceNotFoundException is thrown if the applying {@link Mentor} doesn't exist
     * @throws ResourceNotFoundException is thrown if the applying user's {@link Profile} doesn't exist
     * @throws BadRequestException is thrown if the applying {@link Mentor} is not in applicable state
     * @throws BadRequestException is thrown if the applying user is already a {@link Mentor}
     */
    public Mentee applyAsMentee(long mentorId, long profileId, Mentee mentee)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentor> optionalMentor = mentorRepository.findById(mentorId);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Unable to apply as a mentee. " +
                         "Mentor with id: " + mentorId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!EnrolmentState.APPROVED.equals(optionalMentor.get().getState())) {
            String msg = "Error, Unable to apply as a mentee. " +
                         "Mentor with id: " + mentorId + " is not in the applicable state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (!optionalProfile.isPresent()) {
            String msg = "Error, Unable to apply as a mentee. " +
                         "Profile with id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        Optional<Mentor> alreadyRegisteredMentor = mentorRepository
                .findByProfileIdAndProgramId(profileId, optionalMentor.get().getProgram().getId());
        if (alreadyRegisteredMentor.isPresent() &&
                alreadyRegisteredMentor.get().getState().equals(EnrolmentState.APPROVED)) {
            String msg = "Error, Unable to apply as a mentee. " +
                         "Profile with id: " + profileId + " is already registered as a mentor.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        mentee.setProfile(optionalProfile.get());
        mentee.setProgram(optionalMentor.get().getProgram());
        mentee.setMentor(optionalMentor.get());
        mentee.setState(EnrolmentState.PENDING);
        return menteeRepository.save(mentee);
    }

    /**
     * Retrieves all the {@link Mentee} objects of a {@link Mentor}
     *
     * @param mentorId which is the Mentor id of the {@link Mentor}
     * @param state    which is the state of the {@link Mentee} objects to be filtered
     * @return {@link List} of {@link Mentee} objects
     *
     * @throws ResourceNotFoundException if the requesting {@link Mentor} object doesn't exist
     * @throws BadRequestException       if the requesting {@link Mentor} object is not approved
     */
    public List<Mentee> getAllMenteesOfMentor(long mentorId, Optional<EnrolmentState> state)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentor> optionalMentor = mentorRepository.findById(mentorId);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentor by id: " + mentorId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!EnrolmentState.APPROVED.equals(optionalMentor.get().getState())) {
            String msg = "Error, Mentor by id: " + mentorId + " is not an approved mentor.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        if (!state.isPresent()){
            return optionalMentor.get().getMentees();
        }
        return menteeRepository.findAllByMentorIdAndState(mentorId,state.get());
    }

    /**
     * Update the application of a {@link Mentee}
     *
     * @param profileId which is the Profile id of the {@link Mentee} to be updated
     * @param mentorId  which is the Mentor id of the {@link Mentee} to be updated
     * @param mentee    with the application of the mentee to be updated
     * @return the updated {@link Mentee}
     *
     * @throws ResourceNotFoundException is thrown if the {@link Mentee} doesn't exist
     * @throws BadRequestException       if the {@link Mentee} is not in the valid state
     */
    public Mentee updateMenteeData(long profileId, long mentorId, Mentee mentee)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentee> optionalMentee = menteeRepository.findByProfileIdAndMentorId(profileId, mentorId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee by profile id: " + profileId + " and " +
                         "mentor id: " + mentorId + " cannot be updated. " +
                         "Mentee doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        Mentee existingMentee = optionalMentee.get();
        if (!mentee.getSubmissionUrl().isEmpty()) {
            if (EnrolmentState.PENDING.equals(existingMentee.getState())) {
                existingMentee.setSubmissionUrl(mentee.getSubmissionUrl());
            } else {
                String msg = "Error, Application cannot be updated. " +
                             "Mentee is not in a valid state.";
                log.error(msg);
                throw new BadRequestException(msg);
            }
        }
        return menteeRepository.save(existingMentee);
    }

    /**
     * Retrieves the {@link Mentee} of a user if the user is a mentee
     *
     * @param mentorId  which is the id of the {@link Mentor}
     * @param profileId which is the id of the {@link Profile}
     * @return {@link Mentee}
     *
     * @throws NoContentException if the user hasn't applied for {@link Mentor}
     */
    public Mentee getLoggedInMentee(long mentorId, long profileId)
            throws NoContentException {
        Optional<Mentee> optionalMentee = menteeRepository.findByProfileIdAndMentorId(profileId, mentorId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, User by profile id: " + profileId + " hasn't applied for " +
                         "mentor with id: " + mentorId + ".";
            log.error(msg);
            throw new NoContentException(msg);
        }
        return optionalMentee.get();
    }
}
