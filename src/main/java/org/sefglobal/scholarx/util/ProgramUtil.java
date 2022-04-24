package org.sefglobal.scholarx.util;

import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Component
public class ProgramUtil {
    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    MenteeRepository menteeRepository;

    @Autowired
    ProfileRepository profileRepository;

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
                        "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> "
                        +
                        "for any clarifications.";

                emailService.sendEmail(mentor.getProfile().getEmail(), StringUtils.capitalize(mentor.getState().name()),
                        message, false);

            } else if (mentor.getState().name().equals("REJECTED")) {

                message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +
                        "Thank you very much for taking your time to apply for the " + program.get().getTitle()
                        + " program. " +
                        "However, due to the competitive nature of the mentor applications, your application " +
                        "did not make it to the final list of mentors for the program. We encourage you to try " +
                        "again next year and follow us on our social media channels for future programs. " +
                        "If you have any clarifications, please reach out to us via " +
                        "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a>";

                emailService.sendEmail(mentor.getProfile().getEmail(), StringUtils.capitalize(mentor.getState().name()),
                        message, false);

            }
        }
    }

    public void sendMenteeFiltrationEmails(long id, Optional<Program> program) throws MessagingException, IOException {
        List<Mentee> mentees = getMenteesWithoutDuplicatesByProgramId(id);
        // Notify mentees
        for (Mentee mentee : mentees) {
            String message = "Dear " + mentee.getProfile().getFirstName() + ",<br /><br />" +
                    "Thank you very much for applying to the " + program.get().getTitle()
                    + " program. Your application has been received. " +
                    "Mentors will soon review your applications and we will keep you posted on the progress via email. "
                    +
                    "Until then, read more about student experience <a href=\"https://medium.com/search?q=scholarx\">here</a> and reach out to us via "
                    +
                    "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> " +
                    "for any clarifications.";

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, false);
        }
    }

    public void sendMenteeSelectionEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        // Notify mentors
        for (Mentor mentor : approvedMentors) {

            String message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +

                    "It is with much pleasure we inform you that we have completed the first round of mentor-mentee matching.<br />" + 
                    "There are a few points that we would like to share with you with regard to the mentee applications.<br />"+
                    "<ul>"+
                    "<li>The minimum word limit for each question of the application was introduced halfway through the application"+
                    "period in order to enhance competitiveness. Due to this reason, the applications which were received at the earliest"+
                    " stage might contain relatively short answers.</li>"+
                    "<li>Mentor-mentee matching process consists of two rounds.</li>"+
                    "<li>Some mentors have received an excess number of applications and we have selected potential mentees for those mentors"+
                    "after a filtering process. We kindly request you to go through these applications and select the mentees of your choice"+
                    "and decline the mentees who are not commendable enough before 28th April 11.59 p.m (IST), so that we can replace them during"+
                    "the second round of matching.</li>"+
                    "<li>Some mentors have received less than the number of available slots or none, owing to facts such as lack of compatibility"+
                    "of subject areas, more mentors representing the same subject area etc. We will be choosing the potential mentees for those mentors"+
                    "as well during the second round of matching.</li><br />"+
                    "We appreciate your enthusiasm in being a part of this journey and kindly request your cooperation in completing this matching process as well.<br />"+
                    "If you have any further queries please don't hesitate to contact us at"+
                    "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a><br /><br />"+
                    "Thank you and Best Regards<br />"+
                    "SEF Team";
        
            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message, true);
        }
    }

    public void sendOnGoingEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        List<Mentee> approvedMentees = menteeRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        List<Mentee> discardedMentees = menteeRepository.findAllByProgramIdAndState(id,
                EnrolmentState.FAILED_FROM_WILDCARD);

        for (Mentor mentor : approvedMentors) {

            String message = "Dear " + mentor.getProfile().getFirstName() + ",<br /><br />" +
                    "<b>Congratulations!</b><br />Your list of students is now finalised. " +
                    "You can check your mentees and their contact details by visiting the <b>ScholarX dashboard.</b> " +
                    "Please make the first contact with them as we have instructed them to wait for your email.";

            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message, true);
        }

        for (Mentee mentee : approvedMentees) {
            Profile assignedMentor = mentee.getAssignedMentor().getProfile();
            String message = "Dear " + mentee.getProfile().getFirstName() + ",<br /><br />" +
                    "<b>Congratulations!</b><br /> You have been accepted as a mentee to be mentored under  " +
                    assignedMentor.getFirstName() + " " + assignedMentor.getLastName() + ". <br />" +
                    "You can check your mentor and their details by visiting the <b>ScholarX dashboard.</b> " +
                    "Please make sure not to contact your mentor until they do as we have instructed them to " +
                    "make the first contact";

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, true);
        }

        for (Mentee mentee : discardedMentees) {
            String message = "Dear " + mentee.getProfile().getFirstName() + ",<br /><br />" +
                    "Thank you very much for taking your time to apply for the " + program.get().getTitle()
                    + " program. " +
                    "However, We regret to inform you that your application couldn't make the cut this time." +
                    "We encourage you to try again next year and follow us on our social media channels for " +
                    "future programs. If you have any clarifications, please reach out to us via " +
                    "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a>" +
                    "You can check your application details by visiting the <b>ScholarX dashboard.</b>";

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, true);
        }
    }

    public void sendConfirmationEmails(long profileId, Optional<Program> program)
            throws MessagingException, IOException {
        Optional<Profile> profile = profileRepository.findById(profileId);
        String message = "Dear " + profile.get().getFirstName() + ",<br /><br />" +
                "Thank you very much for applying to the " + program.get().getTitle()
                + " program. Your application has been received. " +
                "You can view/edit your application by visiting the <b>ScholarX dashboard.</b> " +
                "Reach out to us via " +
                "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> " +
                "for any clarifications.";

        emailService.sendEmail(profile.get().getEmail(), program.get().getTitle(), message, true);
    }

    /**
     * Removes mentee duplicates
     */
    private List<Mentee> getMenteesWithoutDuplicatesByProgramId(long id) {
        List<Mentee> output = new ArrayList<>();
        List<Long> idList = new ArrayList<>();
        for (Mentee mentee : menteeRepository.findAllByProgramId(id)) {
            if (!idList.contains(mentee.getProfile().getId())) {
                idList.add(mentee.getProfile().getId());
                output.add(mentee);
            }
        }
        return output;
    }
}
