package org.sefglobal.scholarx.util;

import org.apache.commons.lang.StringUtils;
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

            if (mentor.getState().name().equals("APPROVED")) {

                message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +
                        "<b>Congratulations!</b><br />You have been selected by the " +
                        "ScholarX committee to be a mentor of the " + program.get().getTitle() +
                        " program. We will soon open up the program for students to " +
                        "apply and keep you posted on the progress via email. Until " +
                        "then, read more about student experience " +
                        "<a href=\"https://medium.com/search?q=scholarx\">here</a> and reach out to us via " +
                        "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> " +
                        "for any clarifications.";

                emailService.sendEmail(mentor.getProfile().getEmail(), StringUtils.capitalize(mentor.getState().name()), message, false);

            } else if (mentor.getState().name().equals("REJECTED")) {

                message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +
                        "Thank you very much for taking your time to apply for the " + program.get().getTitle() + " program. " +
                        "However, due to the competitive nature of the mentor applications, your application " +
                        "did not make it to the final list of mentors for the program. We encourage you to try " +
                        "again next year and follow us on our social media channels for future programs. " +
                        "If you have any clarifications, please reach out to us via " +
                        "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a>";

                emailService.sendEmail(mentor.getProfile().getEmail(), StringUtils.capitalize(mentor.getState().name()), message, false);

            }
        }
    }

    public void sendMenteeSelectionEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        List<Mentee> mentees = menteeRepository.findAllByProgramId(id);

        // Notify mentors
        for (Mentor mentor : approvedMentors) {

            String message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +
                    "You have student applications waiting to be reviewed. You can approve or reject your mentees " +
                    "by visiting the <b>ScholarX dashboard.</b>";

            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message, true);
        }

        // Notify mentees
        for (Mentee mentee : mentees) {
            String message = "Dear " + mentee.getProfile().getFirstName() + ",<br /><br />" +
                    "Thank you very much for applying to the " + program.get().getTitle() + " program. Your application has been received. " +
                    "Mentors will soon review your applications and we will keep you posted on the progress via email. " +
                    "Until then, read more about student experience <a href=\"https://medium.com/search?q=scholarx\">here</a> and reach out to us via " +
                    "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> " +
                    "for any clarifications.";

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, false);
        }
    }

    public void sendOnGoingEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);

        for (Mentor mentor : approvedMentors) {

            String message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +
                    "<b>Congratulations!</b><br />Students have accepted you as their mentor. " +
                    "You can check your mentees and their contact details by visiting the <b>ScholarX dashboard.</b> " +
                    "Please make the first contact with them as we have instructed them to wait for your email.";

            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message, true);
        }
    }

    public void sendMentorConfirmationEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentee> mentees = menteeRepository.findAllByProgramId(id);

        String message = "You can check your mentor by visiting the dashboard";

        for (Mentee mentee : mentees) {
            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, true);
        }
    }
}
