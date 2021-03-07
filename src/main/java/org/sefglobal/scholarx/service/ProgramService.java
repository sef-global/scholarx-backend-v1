package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.repository.ProgramRepository;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.sefglobal.scholarx.util.ProgramState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProgramService {
    private final static Logger log = LoggerFactory.getLogger(ProgramService.class);
    private final ProgramRepository programRepository;
    private final ProfileRepository profileRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    public ProgramService(ProgramRepository programRepository,
                          ProfileRepository profileRepository,
                          MentorRepository mentorRepository,
                          MenteeRepository menteeRepository) {
        this.programRepository = programRepository;
        this.profileRepository = profileRepository;
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
    }

    /**
     * Retrieves all the {@link Program} objects
     *
     * @return {@link List} of {@link Program} objects
     */
    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    /**
     * Retrieves the {@link Program} filtered from {@code id}
     *
     * @param id which is the id of the filtering {@link Program}
     * @return {@link Program}
     *
     * @throws ResourceNotFoundException if the requesting {@link Program} doesn't exist
     */
    public Program getProgramById(long id) throws ResourceNotFoundException {
        Optional<Program> program = programRepository.findById(id);
        if (!program.isPresent()) {
            String msg = "Error, Program by id: " + id + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return program.get();
    }

    /**
     * Create a new {@link Program}
     *
     * @param program which holds the data to be added
     * @return the created {@link Program}
     */
    public Program addProgram(Program program) {
        ProgramState createdState = ProgramState.CREATED;
        program.setState(createdState);
        return programRepository.save(program);
    }

    /**
     * Update a {@link Program} Only the {@link Program} specific data is updated(title, headline,
     * etc.). Except {@link ProgramState} Children of the {@link Program} will not be updated by
     * this method
     *
     * @param id      which is the {@link Program} to be updated
     * @param program which is the up-to-date object
     * @return the updated {@link Program}
     *
     * @throws ResourceNotFoundException is thrown if the requesting {@link Program} doesn't exist
     */
    public Program updateProgram(long id, Program program) throws ResourceNotFoundException {
        Optional<Program> existingProgram = programRepository.findById(id);
        if (!existingProgram.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be updated. " +
                         "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        Program updatedProgram = existingProgram.get();
        updatedProgram.setTitle(program.getTitle());
        updatedProgram.setHeadline(program.getHeadline());
        updatedProgram.setImageUrl(program.getImageUrl());
        updatedProgram.setLandingPageUrl(program.getLandingPageUrl());
        return programRepository.save(updatedProgram);
    }

    /**
     * Update a {@link Program} by selecting the next {@link ProgramState}
     *
     * @param id which is the {@link Program} to be updated
     * @return the updated {@link Program}
     *
     * @throws ResourceNotFoundException is thrown if the requesting {@link Program} doesn't exist
     */
    public Program updateState(long id) throws ResourceNotFoundException {
        Optional<Program> program = programRepository.findById(id);
        if (!program.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be updated. " +
                         "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        ProgramState nextState = program.get().getState().next();
        program.get().setState(nextState);
        return programRepository.save(program.get());
    }

    /**
     * Delete a existing {@link Program}
     *
     * @param id which is the identifier of the {@link Program}
     * @throws ResourceNotFoundException if {@link Program} for {@code id} doesn't exist
     */
    public void deleteProgram(long id) throws ResourceNotFoundException {
        Optional<Program> existingProgram = programRepository.findById(id);
        if (!existingProgram.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be deleted. " +
                         "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        menteeRepository.deleteByMentorProgramId(id);
        mentorRepository.deleteByProgramId(id);
        programRepository.deleteById(id);
    }

    /**
     * Retrieves all the {@link Mentor} objects filtered from {@link Program} {@code id}
     *
     * @param id     which is the Program id of the filtering {@link Mentor} objects
     * @param states which is the list of states that {@link Mentor} objects should be filtered from
     * @return {@link List} of {@link Mentor} objects
     *
     * @throws ResourceNotFoundException if the requesting {@link Program} to filter
     *                                  {@link Mentor} objects doesn't exist
     */
    public List<Mentor> getAllMentorsByProgramId(long id, List<EnrolmentState> states)
            throws ResourceNotFoundException {
        if (!programRepository.existsById(id)) {
            String msg = "Error, Program by id: " + id + " doesn't exist";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (states == null || states.isEmpty()) {
            return  mentorRepository.findAllByProgramId(id);
        } else {
            return  mentorRepository.findAllByProgramIdAndStateIn(id, states);
        }
    }

    /**
     * Create new {@link Mentor}
     *
     * @param programId which is the program id for the requesting {@link Program}
     * @param profileId which is the profile id of the applying user's {@link Profile}
     * @param mentor    which holds the data to be added
     * @return the created {@link Mentor}
     *
     * @throws ResourceNotFoundException is thrown if the applying {@link Program} doesn't exist
     * @throws ResourceNotFoundException is thrown if the applying user's {@link Profile} doesn't exist
     * @throws BadRequestException is thrown if the applying {@link Program} is
     * not in the applicable {@link ProgramState}
     */
    public Mentor applyAsMentor(long programId, long profileId, Mentor mentor)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Program> optionalProgram = programRepository.findById(programId);
        if (!optionalProgram.isPresent()) {
            String msg = "Error, Unable to apply as a mentor. " +
                         "Program with id: " + programId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!ProgramState.MENTOR_APPLICATION.equals(optionalProgram.get().getState())) {
            String msg = "Error, Unable to apply as a mentor. " +
                         "Program with id: " + programId + " is not in the applicable status.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (!optionalProfile.isPresent()) {
            String msg = "Error, Unable to apply as a mentor. " +
                         "Profile with id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        mentor.setProfile(optionalProfile.get());
        mentor.setProgram(optionalProgram.get());
        mentor.setState(EnrolmentState.PENDING);
        return mentorRepository.save(mentor);
    }

    /**
     * Retrieves the {@link Mentor} of a user if the user is a mentor
     *
     * @param programId which is the id of the {@link Program}
     * @param profileId which is the id of the {@link Profile}
     * @return {@link Mentor}
     *
     * @throws ResourceNotFoundException if the requesting {@link Mentor} doesn't exist
     */
    public Mentor getLoggedInMentor(long programId, long profileId)
            throws ResourceNotFoundException {
        Optional<Mentor> optionalMentor = mentorRepository.findByProfileIdAndProgramId(profileId, programId);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentor by profile id: " + profileId + " and " +
                         "program id: " + programId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return optionalMentor.get();
    }

    /**
     * Update the application and prerequisites of a {@link Mentor}
     *
     * @param profileId which is the Profile id of the {@link Mentor} to be updated
     * @param programId which is the Program id of the {@link Mentor} to be updated
     * @param mentor    with the application and prerequisites of the mentor to be updated
     * @return the updated {@link Mentor}
     *
     * @throws ResourceNotFoundException is thrown if the {@link Mentor} doesn't exist
     * @throws BadRequestException       if the {@link Mentor} is not in the valid state
     */
    public Mentor updateMentorData(long profileId, long programId, Mentor mentor)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentor> optionalMentor = mentorRepository.findByProfileIdAndProgramId(profileId, programId);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentor by profile id: " + profileId + " and " +
                         "program id: " + programId + " cannot be updated. " +
                         "Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        Mentor existingMentor = optionalMentor.get();
        if (!mentor.getApplication().isEmpty()) {
            if (EnrolmentState.PENDING.equals(existingMentor.getState())) {
                existingMentor.setApplication(mentor.getApplication());
            } else {
                String msg = "Error, Application cannot be updated. " +
                             "Mentor is not in a valid state.";
                log.error(msg);
                throw new BadRequestException(msg);
            }
        }
        if (!mentor.getPrerequisites().isEmpty()) {
            if (EnrolmentState.APPROVED.isHigherThanOrEqual(existingMentor.getState())) {
                if (ProgramState.MENTEE_APPLICATION.isHigherThan(existingMentor.getProgram().getState())) {
                    existingMentor.setPrerequisites(mentor.getPrerequisites());
                } else {
                    String msg = "Error, Prerequisites cannot be updated. " +
                                 "Mentor is not in a valid state.";
                    log.error(msg);
                    throw new BadRequestException(msg);
                }
            }
        }
        return mentorRepository.save(existingMentor);
    }

    /**
     * Retrieves the applied {@link Mentor} objects of the {@link Mentee}
     *
     * @param programId    which is the id of the {@link Program}
     * @param profileId    which is the profile id of the {@link Mentee}
     * @param menteeStates which is the list of states that {@link Mentee} objects should be
     *                     filtered from
     * @return {@link List} of {@link Mentor} objects
     *
     * @throws NoContentException if the user hasn't applied for {@link Mentor} objects
     */
    public List<Mentor> getAppliedMentorsOfMentee(long programId, List<EnrolmentState> menteeStates, long profileId)
            throws NoContentException {
        List<Mentee> menteeList;
        if (menteeStates == null || menteeStates.isEmpty()) {
            menteeList = menteeRepository.findAllByProgramIdAndProfileId(programId, profileId);
        } else {
            menteeList = menteeRepository.findAllByProgramIdAndProfileIdAndStateIn(programId, profileId, menteeStates);
        }
        if (menteeList.isEmpty()) {
            String msg = "Error, Mentee by program id: " + programId + " and " +
                         "profile id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new NoContentException(msg);
        }
        List<Mentor> mentorList = new ArrayList<>();
        for (Mentee mentee : menteeList) {
            mentorList.add(mentee.getMentor());
        }
        return mentorList;
    }

    /**
     * Retrieves the selected {@link Mentor} of the {@link Mentee}
     *
     * @param programId which is the id of the {@link Program}
     * @param profileId which is the profile id of the {@link Mentee}
     * @return {@link Mentor} object
     *
     * @throws ResourceNotFoundException if the {@link Program} doesn't exist
     */
    public Mentor getSelectedMentor(long programId, long profileId)
            throws ResourceNotFoundException, NoContentException {
        List<Mentee> menteeList = menteeRepository
                .findAllByProgramIdAndProfileId(programId, profileId);
        if (menteeList.isEmpty()) {
            String msg = "Error, Mentee by program id: " + programId + " and " +
                         "profile id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        for (Mentee mentee : menteeList) {
            if (EnrolmentState.APPROVED.equals(mentee.getState())) {
                return mentee.getMentor();
            }
        }

        String msg = "Error, Mentee is not approved by any mentor yet.";
        log.error(msg);
        throw new NoContentException(msg);
    }
}
