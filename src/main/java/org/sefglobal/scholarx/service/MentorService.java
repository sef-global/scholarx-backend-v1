package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
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

        mentee.setProfile(optionalProfile.get());
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
}
