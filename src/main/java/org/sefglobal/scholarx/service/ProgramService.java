package org.sefglobal.scholarx.service;

import com.google.common.collect.ImmutableList;
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

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProgramService {
    private final static Logger log = LoggerFactory.getLogger(ProgramService.class);
    private final ProgramRepository programRepository;
    private final ProfileRepository profileRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @Autowired
    private ProgramUtil programUtil;

    @Autowired
    private EmailService emailService;

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
        if (!program.isPresent()) {
            String msg = "Error, Program with id: " + id + " cannot be updated. " +
                    "Program doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        final ProgramState nextState = program.get().getState().next();

        switch (nextState) {
            case MENTEE_APPLICATION:
                List<Mentor> mentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.PENDING);
                for (Mentor mentor : mentors) {
                    mentor.setState(EnrolmentState.REJECTED);
                }
                break;

            case MENTEE_SELECTION:
                List<Mentor> mentorList = mentorRepository.findAllByProgramId(id);
                for (Mentor mentor: mentorList) {
                    mentor.setNoOfAssignedMentees(0);
                }
                break;

            case WILDCARD:
                List<Mentee> mentees = menteeRepository.findAllByProgramIdAndStateIn(
                        id, ImmutableList.of(EnrolmentState.ASSIGNED, EnrolmentState.REJECTED));
                for (Mentee mentee : mentees) {
                    mentee.setState(EnrolmentState.REJECTED);
                    mentee.setRejectedBy(mentee.getAssignedMentor());
                    mentee.setAssignedMentor(null);
                }
                break;

            case ONGOING:
                List<Mentee> approvedMentees = menteeRepository.findAllByProgramIdAndState(id, EnrolmentState.ASSIGNED);
                for (Mentee mentee : approvedMentees) {
                    mentee.setState(EnrolmentState.APPROVED);
                }
                List<Mentee> ignoredMentees = menteeRepository.
                        findAllByProgramIdAndStateIn(id, ImmutableList.of(EnrolmentState.POOL, EnrolmentState.PENDING, EnrolmentState.REJECTED));
                for (Mentee mentee : ignoredMentees) {
                    mentee.setState(EnrolmentState.FAILED_FROM_WILDCARD);
                }
                break;
        }

        Thread thread = new Thread(() -> {
            try {
                switch (nextState) {
                    case MENTEE_APPLICATION:
                        programUtil.sendMenteeApplicationEmails(id, program);
                        break;
                    case ADMIN_MENTEE_FILTRATION:
                        programUtil.sendMenteeFiltrationEmails(id, program);
                        break;
                    case MENTEE_SELECTION:
                        programUtil.sendMenteeSelectionEmails(id, program);
                        break;
                    case ONGOING:
                        programUtil.sendOnGoingEmails(id, program);
                        break;
                }
            } catch (Exception exception) {
                log.error("Email service error: ", exception);
            }
        });
        thread.start();

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
     * Create new {@link Mentor}
     *
     * @param programId which is the program id for the requesting {@link Program}
     * @param profileId which is the profile id of the applying user's {@link Profile}
     * @param mentor which holds the mentor details
     * @return the created {@link Mentor}
     *
     * @throws ResourceNotFoundException is thrown if the applying {@link Program} doesn't exist
     * @throws ResourceNotFoundException is thrown if the applying user's {@link Profile} doesn't exist
     * @throws BadRequestException is thrown if the applying {@link Program} is
     * not in the applicable {@link ProgramState}
     * @throws BadRequestException is thrown if the applying user has already applied for the {@link Program}
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

        Optional<Mentor> duplicateMentor = mentorRepository.findByProfileIdAndProgramId(profileId, programId);
        if (duplicateMentor.isPresent()) {
            String msg = "Error, Unable to apply as a mentor. " +
                         "Profile with id: " + profileId + " has already applied as a mentor for the selected program.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        Mentor savedMentor = new Mentor();
        savedMentor.setProfile(optionalProfile.get());
        savedMentor.setProgram(optionalProgram.get());
        savedMentor.setCategory(mentor.getCategory());
        savedMentor.setBio(mentor.getBio());
        savedMentor.setExpertise(mentor.getExpertise());
        savedMentor.setInstitution(mentor.getInstitution());
        savedMentor.setPosition(mentor.getPosition());
        savedMentor.setSlots(mentor.getSlots());
        savedMentor.setState(EnrolmentState.PENDING);
        Mentor savedMentorEntity = mentorRepository.save(savedMentor);

        Thread thread = new Thread(() -> {
            try {
                programUtil.sendConfirmationEmails(profileId, optionalProgram);
            } catch (Exception exception) {
                log.error("Email service error: ", exception);
            }
        });
        thread.start();

        return savedMentorEntity;
    }

    /**
     * Update a {@link Mentor}
     *
     * @param programId which is the program id for the mentor's {@link Program}
     * @param profileId which is the profile id of the mentor's {@link Profile}
     * @param mentor    which holds the mentor details
     * @return the updated {@link Mentor}
     *
     * @throws ResourceNotFoundException is thrown if the mentor doesn't exist
     * @throws BadRequestException is thrown if the {@link Program} is not in the applicable {@link ProgramState}
     */
    public Mentor updateMentorApplication(long programId, long profileId, Mentor mentor)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentor> optionalMentor = mentorRepository.findByProfileIdAndProgramId(profileId, programId);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Unable to update mentor application. " +
                         "Mentor with profile id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        if (!ProgramState.MENTOR_APPLICATION.equals(optionalMentor.get().getProgram().getState())) {
            String msg = "Error, Unable to update mentor application. " +
                         "Program with id: " + programId + " is not in the valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        optionalMentor.get().setCategory(mentor.getCategory());
        optionalMentor.get().setBio(mentor.getBio());
        optionalMentor.get().setExpertise(mentor.getExpertise());
        optionalMentor.get().setInstitution(mentor.getInstitution());
        optionalMentor.get().setPosition(mentor.getPosition());
        optionalMentor.get().setSlots(mentor.getSlots());
        return mentorRepository.save(optionalMentor.get());
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
     * Retrieves the applied {@link Mentor} objects of the {@link Mentee}
     *
     * @param programId    which is the id of the {@link Program}
     * @param profileId    which is the profile id of the {@link Mentee}
     * @return {@link Mentor} object
     *
     * @throws NoContentException if the user hasn't applied for {@link Mentor} objects
     */
    public Mentor getAppliedMentorOfMentee(long programId, long profileId)
            throws NoContentException {
        Optional<Mentee> mentee = menteeRepository.findByProgramIdAndProfileId(programId, profileId);
        
        if (!mentee.isPresent()) {
            String msg = "Error, Mentee by program id: " + programId + " and " +
                         "profile id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new NoContentException(msg);
        }
        
        return mentee.get().getAppliedMentor();
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
        Optional<Mentee> optionalMentee = menteeRepository
                .findByProgramIdAndProfileId(programId, profileId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee by program id: " + programId + " and " +
                         "profile id: " + profileId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (EnrolmentState.APPROVED.equals(optionalMentee.get().getState())) {
            return optionalMentee.get().getAssignedMentor();
        }

        String msg = "Error, Mentee is not approved by any mentor yet.";
        log.error(msg);
        throw new NoContentException(msg);
    }

    /**
     * Update the application of a {@link Mentee}
     *
     * @param profileId which is the Profile id of the {@link Mentee} to be updated
     * @param programId which is the Program id of the {@link Mentee} to be updated
     * @param mentee    with the application of the mentee to be updated
     * @return the updated {@link Mentee}
     *
     * @throws ResourceNotFoundException is thrown if the {@link Mentee} doesn't exist
     * @throws ResourceNotFoundException is thrown if the {@link Mentor} doesn't exist
     * @throws BadRequestException       if the {@link Mentor} is not in the valid state
     * @throws BadRequestException       if the {@link Mentee} is not in the valid state
     * @throws BadRequestException is thrown if the applying program {@link Program} is not in applicable state {@link ProgramState}
     */
    public Mentee updateMenteeData(long profileId, long programId, Mentee mentee)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentee> optionalMentee = menteeRepository.findByProgramIdAndProfileId(programId, profileId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee by profile id: " + profileId + " and " +
                    "program id: " + programId + " cannot be updated. " +
                    "Mentee doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!ProgramState.MENTEE_APPLICATION.equals(optionalMentee.get().getProgram().getState())) {
            String msg = "Error, Unable to update mentee application. " +
                         "Program with id: " + programId + " is not in the valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        Optional<Mentor> optionalMentor = mentorRepository.findById(mentee.getAppliedMentor().getId());
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentee by profile id: " + profileId + " and " +
                    "program id: " + programId + " cannot be updated. " +
                    "Applied Mentor doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        if (!EnrolmentState.APPROVED.equals(optionalMentor.get().getState())) {
            String msg = "Error, Unable to apply as a mentee. " +
                    "Mentor is not in the applicable state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        Mentee existingMentee = optionalMentee.get();
        if (EnrolmentState.PENDING.equals(existingMentee.getState())) {
            existingMentee.setAppliedMentor(optionalMentor.get());
            existingMentee.setIntent(mentee.getIntent());
            existingMentee.setUniversity(mentee.getUniversity());
            existingMentee.setYear(mentee.getYear());
            existingMentee.setReasonForChoice(mentee.getReasonForChoice());
            existingMentee.setResumeUrl(mentee.getResumeUrl());
            existingMentee.setAchievements(mentee.getAchievements());
        } else {
            String msg = "Error, Application cannot be updated. " +
                    "Mentee is not in a valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        return menteeRepository.save(existingMentee);
    }

    /**
     * Retrieves the {@link Mentee} of a user if the user is a mentee
     *
     * @param programId which is the id of the {@link Program}
     * @param profileId which is the id of the {@link Profile}
     * @return {@link Mentee}
     * @throws NoContentException if the user hasn't applied for {@link Program}
     */
    public Mentee getLoggedInMentee(long programId, long profileId)
            throws NoContentException {
        Optional<Mentee> optionalMentee = menteeRepository.findByProgramIdAndProfileId(programId, profileId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, User by profile id: " + profileId + " hasn't applied for " +
                    "program with id: " + programId + ".";
            log.error(msg);
            throw new NoContentException(msg);
        }
        return optionalMentee.get();
    }

    /**
     * Retrieves all the emails from {@link EnrolledUser}
     *
     * @param programId which is the id of the {@link Program}
     * @return {@link List<String>}
     * @throws ResourceNotFoundException if the {@link Program} doesn't exist
     */
    public List<String> getEmailsAddresses(long programId)
    throws ResourceNotFoundException {
        Optional<Program> program = programRepository.findById(programId);
        if (!program.isPresent()) {
            String msg = "Error, Program by id: " + programId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        List<EnrolledUser> enrolledUsers = program.get().getEnrolledUsers();
        List<String> emails = new ArrayList<String>();

        for(EnrolledUser user: enrolledUsers) {
            emails.add(user.getProfile().getEmail());
        }
        return emails;
    }

    /**
     * Sends bulk emails to the recipients in {@link BulkEmailDto}
     *
     * @param programId    which is the id of the {@link Program}
     * @param bulkEmailDto which contains the recipients and the message
     * @throws ResourceNotFoundException if the program doesn't exist
     */
    public void sendBulkEmails(long programId, BulkEmailDto bulkEmailDto)
            throws ResourceNotFoundException {
        Optional<Program> optionalProgram = programRepository.findById(programId);
        if (!optionalProgram.isPresent()) {
            String msg = "Error, Program with id: " + programId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        Set<String> emails = new HashSet<>();
        for (MailGroup mailGroup : bulkEmailDto.getMailGroups()) {
            if (mailGroup.equals(MailGroup.ALL)){
                optionalProgram.get().getEnrolledUsers()
                        .forEach(user -> emails.add(user.getProfile().getEmail()));
            } else if (mailGroup.equals(MailGroup.ALL_MENTORS)){
                mentorRepository.findAllByProgramId(programId)
                        .forEach(mentor -> emails.add(mentor.getProfile().getEmail()));
            } else if (mailGroup.equals(MailGroup.ALL_MENTEES)){
                menteeRepository.findAllByProgramId(programId)
                        .forEach(mentee -> emails.add(mentee.getProfile().getEmail()));
            } else if (mailGroup.equals(MailGroup.APPROVED_MENTORS)){
                mentorRepository.findAllByProgramIdAndState(programId, EnrolmentState.APPROVED)
                        .forEach(mentor -> emails.add(mentor.getProfile().getEmail()));
            } else if (mailGroup.equals(MailGroup.REJECTED_MENTORS)){
                mentorRepository.findAllByProgramIdAndState(programId, EnrolmentState.REJECTED)
                        .forEach(mentor -> emails.add(mentor.getProfile().getEmail()));
            } else if (mailGroup.equals(MailGroup.APPROVED_MENTEES)) {
                menteeRepository.findAllByProgramIdAndState(programId, EnrolmentState.APPROVED)
                        .forEach(mentee -> emails.add(mentee.getProfile().getEmail()));
            } else if (mailGroup.equals(MailGroup.DISCARDED_MENTEES)) {
                menteeRepository.findAllByProgramIdAndStateIn(programId, ImmutableList.of(EnrolmentState.DISCARDED, EnrolmentState.FAILED_FROM_WILDCARD))
                        .forEach(mentee -> emails.add(mentee.getProfile().getEmail()));
            }
        }

        emails.addAll(bulkEmailDto.getAdditionalEmails());
        Thread thread = new Thread(() -> {
            for (String email : emails) {
                try {
                    emailService.sendEmail(email, bulkEmailDto.getSubject(), bulkEmailDto.getMessage(), true);
                } catch (MessagingException | IOException exception) {
                    log.error("Email service error: ", exception);
                }
            }
        });
        thread.start();
    }
}
