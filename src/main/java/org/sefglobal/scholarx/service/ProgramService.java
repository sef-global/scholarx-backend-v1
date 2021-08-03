package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.NoContentException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.*;
import org.sefglobal.scholarx.repository.*;
import org.sefglobal.scholarx.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProgramService {
    private final static Logger log = LoggerFactory.getLogger(ProgramService.class);
    private final ProgramRepository programRepository;
    private final ProfileRepository profileRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final QuestionRepository questionRepository;
    private final MentorResponseRepository mentorResponseRepository;

    @Autowired
    private ProgramUtil programUtil;

    public ProgramService(ProgramRepository programRepository,
                          ProfileRepository profileRepository,
                          MentorRepository mentorRepository,
                          MenteeRepository menteeRepository,
                          QuestionRepository questionRepository,
                          MentorResponseRepository mentorResponseRepository) {
        this.programRepository = programRepository;
        this.profileRepository = profileRepository;
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
        this.questionRepository = questionRepository;
        this.mentorResponseRepository = mentorResponseRepository;
    }

    /**
     * Retrieves all the {@link Program} objects
     *
     * @param states which is the list of states that {@link Program} objects should be filtered from
     * @return {@link List} of {@link Program} objects
     */
    public List<Program> getAllPrograms(List<ProgramState> states) {
        if (states == null || states.isEmpty()) {
            return programRepository.findAll();
        } else {
            return programRepository.findAllByStateIn(states);
        }
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

        Thread thread = new Thread(() -> {
            try {
                switch (program.get().getState().next()) {
                    case MENTEE_APPLICATION:
                        programUtil.sendMenteeApplicationEmails(id, program);
                        break;
                    case MENTEE_SELECTION:
                        programUtil.sendMenteeSelectionEmails(id, program);
                        break;
                    case ONGOING:
                        programUtil.sendOnGoingEmails(id, program);
                        break;
                    case MENTOR_CONFIRMATION:
                        programUtil.sendMentorConfirmationEmails(id, program);
                        break;
                }
            } catch (Exception ignored) {
            }
        });
        thread.start();

        if (!program.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be updated. " +
                    "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        ProgramState nextState = program.get().getState().next();
        if (ProgramState.ONGOING.equals(nextState)) {
            List<Mentee> approvedMenteeList = menteeRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
            for (Mentee mentee : approvedMenteeList) {
                long profileId = mentee.getProfile().getId();
                if (menteeRepository.findAllByProgramIdAndProfileIdAndState(id, profileId, EnrolmentState.APPROVED).size() != 1) {
                    menteeRepository.removeAllByProgramIdAndProfileId(id, profileId);
                }
            }
        }
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
     * Retrieves all the {@link Mentee} objects filtered from {@link Program} {@code id}
     *
     * @param id     which is the Program id of the filtering {@link Mentee} objects
     * @return {@link List} of {@link Mentee} objects
     *
     * @throws ResourceNotFoundException if the requesting {@link Program} to filter
     *                                  {@link Mentee} objects doesn't exist
     */
    public List<Mentee> getAllMenteesByProgramId(long id)
            throws ResourceNotFoundException {
        if (!programRepository.existsById(id)) {
            String msg = "Error, Program by id: " + id + " doesn't exist";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return  menteeRepository.findAllByProgramId(id);

    }

    /**
     * Create new {@link Mentor} and Record new {@link MentorResponse} list
     *
     * @param programId which is the program id for the requesting {@link Program}
     * @param profileId which is the profile id of the applying user's {@link Profile}
     * @param responses which holds the responses to be added
     * @return the created {@link Mentor}
     *
     * @throws ResourceNotFoundException is thrown if the applying {@link Program} doesn't exist
     * @throws ResourceNotFoundException is thrown if the applying user's {@link Profile} doesn't exist
     * @throws BadRequestException is thrown if the applying {@link Program} is
     * not in the applicable {@link ProgramState}
     */
    public Mentor applyAsMentor(long programId, long profileId, List<MentorResponse> responses)
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

        Mentor mentor = new Mentor();
        mentor.setProfile(optionalProfile.get());
        mentor.setProgram(optionalProgram.get());
        mentor.setState(EnrolmentState.PENDING);
        Mentor savedMentor = mentorRepository.save(mentor);
        List<MentorResponse> processedResponses = new ArrayList<>();
        for (MentorResponse r: responses) {
            Question question = questionRepository.getOne(r.getQuestion().getId());
            MentorResponse updatedResponse = new MentorResponse(question, savedMentor, r.getResponse());
            processedResponses.add(updatedResponse);
        }
        try {
            mentorResponseRepository.saveAll(processedResponses);
        } catch (Exception e) {
            mentorRepository.delete(savedMentor);
            throw e;
        }
        return savedMentor;
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
     * Retrieves the {@link MentorResponse} List of a {@link Mentor}
     *
     * @param programId which is the Id of the {@link Program}
     * @param profileId which is the profile Id of the {@link Mentor}
     * @return {@link MentorResponse} list
     * @throws ResourceNotFoundException if a mentor doesn't exist by the given programId and profileId
     */
    public List<MentorResponse> getMentorResponses(long programId, long profileId) throws ResourceNotFoundException {
        Optional<Mentor> mentor = mentorRepository.findByProfileIdAndProgramId(profileId, programId);
        if (!mentor.isPresent()) {
            String msg = "Error, Mentor by profile id: " + profileId + " and " +
                    "program id: " + programId + " cannot be found. " +
                    "Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return mentorResponseRepository.getAllByMentorId(mentor.get().getId());
    }

    /**
     * Retrives the {@link MentorResponse} List of a {@link Mentor}
     *
     * @param mentorId which is the id of the {@link Mentor}
     * @return {@link MentorResponse} list
     * @throws ResourceNotFoundException if a mentor doesn't exist by the given mentorId
     */
    public List<MentorResponse> getMentorResponses(long mentorId) throws ResourceNotFoundException {
        Optional<Mentor> mentor = mentorRepository.findById(mentorId);
        if (!mentor.isPresent()) {
            String msg = "Error, Mentor by mentor id: " + mentorId + " cannot be found. " +
                    "Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return mentorResponseRepository.getAllByMentorId(mentor.get().getId());
    }

    /**
     * Update the application question responses of a {@link Mentor}
     *
     * @param programId which is the id of the program
     * @param profileId which is the profile id of the {@link Mentor}
     * @param mentorResponses list of {@link MentorResponse}s to be updated
     * @return the updated list of {@link MentorResponse}
     * @throws ResourceNotFoundException if a mentor doesn't exist by the given profileId and programId
     * @throws BadRequestException is thrown if the {@link Program} is not in the valid {@link ProgramState}
     */
    public List<MentorResponse> editMentorResponses(long programId,
                                                    long profileId,
                                                    List<MentorResponse> mentorResponses)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentor> mentor = mentorRepository.findByProfileIdAndProgramId(profileId, programId);
        if (!mentor.isPresent()) {
            String msg = "Error, Mentor by profile id: " + profileId + " and " +
                    "program id: " + programId + " cannot be found. " +
                    "Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        if (!ProgramState.MENTOR_APPLICATION.equals(mentor.get().getProgram().getState())) {
            String msg = "Error, Unable to edit mentor application. " +
                    "Program with id: " + programId + " is not in the valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        List<MentorResponse> updatedMentorResponses = new ArrayList<>();
        for (MentorResponse response: mentorResponses) {
            MentorResponse queriedResponse = mentorResponseRepository.getOne(response.getId());
            queriedResponse.setResponse(response.getResponse());
            updatedMentorResponses.add(queriedResponse);
        }
        return mentorResponseRepository.saveAll(updatedMentorResponses);
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

    /**
     * Retrieves the {@link Question} list for a given {@link Program} and a {@link QuestionCategory}
     *
     * @param programId which is the if of the {@link Program}
     * @param category which is the identifier of if the required set of questions are mentor ones or mentee ones
     * @return {@link Question} Object list
     * @throws ResourceNotFoundException if the {@link Program} doesn't exist
     */
    public List<Question> getQuestions(long programId, QuestionCategory category) throws ResourceNotFoundException {
        Optional<Program> program = programRepository.findById(programId);
        if (!program.isPresent()) {
            String msg = "Error, Program by id: " + programId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        return questionRepository.getAllByCategoryAndProgramId(category, program.get().getId());
    }

    /**
     *Adds new {@link Question} objects to the {@link Program} mentor/mentee application forms
     *
     * @param programId which is the id of the {@link Program}
     * @param category which is the identifier of whether the question is a {@link Mentor} one or a {@link Mentee} one
     * @param questions which is the list of questions
     * @return {@link Question} Object list
     * @throws ResourceNotFoundException if the {@link Program} doesn't exist
     * @throws BadRequestException if the {@link Program} is not in valid state
     */
    public List<Question> addQuestions(long programId, QuestionCategory category, List<Question> questions)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Program> program = programRepository.findById(programId);
        if (!program.isPresent()) {
            String msg = "Error, Program by id: " + programId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!ProgramState.CREATED.equals(program.get().getState())) {
            String msg = "Error, Unable to add question. " +
                         "Program with id: " + programId + " is not in the valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        List<Question> processedQuestions = new ArrayList<>();
        for (Question q: questions) {
            q.setProgram(program.get());
            q.setCategory(category);
            processedQuestions.add(q);
        }
        return questionRepository.saveAll(processedQuestions);
    }
}
