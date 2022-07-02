package org.sefglobal.scholarx.service;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.exception.UnauthorizedException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.sefglobal.scholarx.util.ProgramState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenteeService {

    private final static Logger log = LoggerFactory.getLogger(MenteeService.class);
    private final MenteeRepository menteeRepository;
    private final MentorRepository mentorRepository;
    
    public MenteeService(MenteeRepository menteeRepository, MentorRepository mentorRepository) {
        this.menteeRepository = menteeRepository;
        this.mentorRepository = mentorRepository;
    }

    /**
     * Delete a existing {@link Mentee}
     *
     * @param id which is the identifier of the {@link Mentee}
     * @throws ResourceNotFoundException if {@link Mentee} for {@code id} doesn't exist
     */
    public void deleteMentee(long id)
            throws ResourceNotFoundException {
        Optional<Mentee> optionalMentee = menteeRepository.findById(id);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee with id: " + id + " cannot be deleted. " +
                         "Mentee doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        menteeRepository.deleteById(id);
    }

    /**
     * Update a {@link EnrolmentState} of a {@link Mentee} to Approved or Rejected
     *
     * @param menteeId   which is the {@link Mentee} to be updated
     * @param profileId  which is the profile identifier of the requesting user
     * @param isApproved which states whether the {@link Mentee} to be approved or rejected
     * @return the updated {@link Mentee}
     *
     * @throws ResourceNotFoundException is thrown if the {@link Mentee} doesn't exist
     * @throws UnauthorizedException     is thrown if the requesting user is not the assigned mentor
     * @throws BadRequestException       is thrown if the {@link Mentee} is removed
     * @throws BadRequestException       is thrown if the {@link Boolean} is null
     */
    public Mentee approveOrRejectMentee(long menteeId, long profileId, Boolean isApproved)
            throws ResourceNotFoundException, BadRequestException, UnauthorizedException {
        if (null == isApproved){
            String msg = "Error, Value cannot be null.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        long assignedMentorProfileId = optionalMentee.get().getAssignedMentor().getProfile().getId();
        if (assignedMentorProfileId != profileId) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " is not a mentee " +
                         "of mentor with profile id: " + profileId + ".";
            log.error(msg);
            throw new UnauthorizedException(msg);
        }
        if (EnrolmentState.REMOVED.equals(optionalMentee.get().getState())) {
            String msg = "Error, Mentee cannot be approved/rejected. " +
                         "Mentee with id: " + menteeId + " is removed.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        Mentor mentor = optionalMentee.get().getAssignedMentor();
        if (isApproved) {
            mentor.setNoOfAssignedMentees(mentor.getNoOfAssignedMentees() + 1);
        } else if (optionalMentee.get().getState().equals(EnrolmentState.ASSIGNED)) {
            optionalMentee.get().setRejectedBy(mentor);
        } else if (optionalMentee.get().getState().equals(EnrolmentState.APPROVED)) {
            optionalMentee.get().setRejectedBy(mentor);
            mentor.setNoOfAssignedMentees(mentor.getNoOfAssignedMentees() - 1);
        }

        optionalMentee.get().setState(isApproved?EnrolmentState.APPROVED:EnrolmentState.REJECTED);
        return menteeRepository.save(optionalMentee.get());
    }
    
    /**
     * Update a assigned {@link Mentor} of a {@link Mentee}
     *
     * @param menteeId which is the {@link Mentee} to be updated
     * @param mentorId which is the id of assigned {@link Mentor}
     * @return the updated {@link Mentee}
     
     * @throws ResourceNotFoundException is thrown if the {@link Mentee} doesn't exist
     * @throws ResourceNotFoundException is thrown if the {@link Mentor} doesn't exist
     * @throws BadRequestException       is thrown if the {@link Mentor} id is not given
     * @throws BadRequestException       is thrown if the {@link Program} is not in a valid state
     */
    public Mentee updateAssignedMentor(long menteeId, Long mentorId)
            throws ResourceNotFoundException, BadRequestException {
        if (null == mentorId) {
            String msg = "Error, Value cannot be null.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee cannot be updated. " +
                    "Mentee with id: " + menteeId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        
        Optional<Mentor> optionalMentor = mentorRepository.findById(mentorId);
        if (!optionalMentor.isPresent()) {
            String msg = "Error, Mentee cannot be updated. " +
                    "Mentor with id: " + mentorId + " doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }

        ProgramState programState = optionalMentee.get().getProgram().getState();
        if (programState.equals(ProgramState.ADMIN_MENTEE_FILTRATION) || programState.equals(ProgramState.WILDCARD)) {
            optionalMentee.get().setState(EnrolmentState.ASSIGNED);
        } else {
            String msg = "Error, Mentee cannot be updated. " +
                    "Program is not in a valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }

        Mentor previouslyAssignedMentor = optionalMentee.get().getAssignedMentor();
        if (previouslyAssignedMentor != null) {
            previouslyAssignedMentor.setNoOfAssignedMentees(previouslyAssignedMentor.getNoOfAssignedMentees() - 1);
        }

        optionalMentor.get().setNoOfAssignedMentees(optionalMentor.get().getNoOfAssignedMentees() + 1);
        optionalMentee.get().setAssignedMentor(optionalMentor.get());
        return menteeRepository.save(optionalMentee.get());
    }

    public Mentee changeState(long menteeId, EnrolmentState enrolmentState)
            throws ResourceNotFoundException, BadRequestException {
        Optional<Mentee> optionalMentee = menteeRepository.findById(menteeId);
        if (!optionalMentee.isPresent()) {
            String msg = "Error, Mentee cannot be updated. " +
                    "Mentee with id "+ menteeId +" doesn't exist.";
            log.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        ProgramState state = optionalMentee.get().getProgram().getState();
        if (!ProgramState.ADMIN_MENTEE_FILTRATION.equals(state) && !ProgramState.WILDCARD.equals(state)){
            String msg = "Error, Mentee cannot be updated. " +
                    "The program is not in a valid state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }
        if (EnrolmentState.REJECTED.equals(enrolmentState) || EnrolmentState.APPROVED.equals(enrolmentState)
                || EnrolmentState.ASSIGNED.equals(enrolmentState)){
            String msg = "Error, Mentee cannot be updated. " +
                    "EnrolmentState: "+ enrolmentState +" is not an applicable state.";
            log.error(msg);
            throw new BadRequestException(msg);
        }


        if(optionalMentee.get().getState().equals(EnrolmentState.ASSIGNED)){
            Mentor assignedMentor = optionalMentee.get().getAssignedMentor();
            assignedMentor.setNoOfAssignedMentees(assignedMentor.getNoOfAssignedMentees() - 1);
            optionalMentee.get().setAssignedMentor(null);
            mentorRepository.save(assignedMentor);
        }
    
        optionalMentee.get().setState(enrolmentState);
        return menteeRepository.save(optionalMentee.get());
    }
}
