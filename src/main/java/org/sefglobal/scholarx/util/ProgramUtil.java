package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class ProgramUtil {
    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    MenteeRepository menteeRepository;

    @Autowired
    private EmailService emailService;

    public void sendMenteeApplicationEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> mentors = mentorRepository.findAllByProgramId(id);

        String message;
        for (Mentor mentor : mentors) {
            message = "You have been " + mentor.getState().name().toLowerCase();
            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message);
        }
    }

    public void sendMenteeSelectionEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);

        String message = "You can approve or reject your mentees by visiting the dashboard";
        for (Mentor mentor : approvedMentors) {
            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message);
        }
    }

    public void sendOnGoingEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);

        String message = "You can check your mentees by visiting the dashboard";
        for (Mentor mentor : approvedMentors) {
            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message);
        }
    }

    public void sendMentorConfirmationEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentee> mentees = menteeRepository.findAllByProgramId(id);

        String message = "You can check your mentor by visiting the dashboard";
        for (Mentee mentee : mentees) {
            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message);
        }
    }
}
