package org.sefglobal.scholarx.util;

import jakarta.mail.MessagingException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.model.Profile;
import org.sefglobal.scholarx.model.Program;
import org.sefglobal.scholarx.model.SentEmail;
import org.sefglobal.scholarx.repository.EmailRepository;
import org.sefglobal.scholarx.repository.MenteeRepository;
import org.sefglobal.scholarx.repository.MentorRepository;
import org.sefglobal.scholarx.repository.ProfileRepository;
import org.sefglobal.scholarx.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@Component
public class ProgramUtil {
    private static final Logger log=LoggerFactory.getLogger(ProgramUtil.class);
    private final EmailRepository emailRepository;

    public ProgramUtil(EmailRepository emailRepository){
        this.emailRepository = emailRepository;
    }

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

                message ="Dear" + mentor.getProfile().getName() + ",<br /><br />" +
                        "I hope this email finds you in high spirits! I am delighted to inform you that you have been selected as a mentor for " + program.get().getTitle() + ", and we extend our heartfelt congratulations to you! <br /><br />" +
                        "We received a large number of qualified applicants, and after a thorough review of all candidates, we are thrilled to invite you to accept a place in our program. Your profile stood out amongst the others, and we are confident that you will contribute positively to our program.<br /><br />"+
                        "We understand that your hard work and dedication have brought you to this moment, and we recognize your exceptional talent, experience and potential in your respective fields. We are excited to have you join our community of learners and scholars.<br /><br />" +
                        "We look forward to seeing the unique perspective and insights you will bring to the mentees and to the program. We believe that you will flourish in this year's edition of ScholarX, and we are thrilled to be a part of your academic or professional journey.<br /><br />" +
                        "Once again, congratulations on your selection! We cannot wait to have you on board. We will keep you informed on the next steps, and in the meantime would like to invite you to go through some of the resources that would be useful to thrive as a great mentor in  " + program.get().getTitle() + ". <br /><br />" +
                        " To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

                emailService.sendEmail(mentor.getProfile().getEmail(), StringUtils.capitalize(mentor.getState().name()), message, false);

                SentEmail email = new SentEmail();
                email.setEmail(mentor.getProfile().getEmail());
                email.setMessage(message);
                email.setProgramId(program.get());
                email.setReceiver(mentor.getProfile());
                email.setState(program.get().getState());
                emailRepository.save(email);

            } else if (mentor.getState().name().equals("REJECTED")) {

                message = "Dear " + mentor.getProfile().getName() + ",<br /><br />" +
                        "I hope this email finds you well. I wanted to take a moment to thank you for your interest in joining " + program.get().getTitle() + " as a mentor and for submitting your application. We appreciate the time and effort you put into it.<br /><br />"+
                        "After careful review of your application and considering all of the candidates, we regret to inform you that we are unable to make you part of the mentor base at this time. We received a large number "+
                        "of qualified applicants, and unfortunately, we could only accept a limited number of Mentors.<br /><br />" +
                        "We understand that this news may be disappointing, and we encourage you to not be discouraged by this decision. Please know that this does not "+
                        "reflect on your abilities, potential or value as an individual. As you progress ahead on your academic or professional journey, we would be glad to have you as a mentor for future ScholarX programs.<br /><br />"+
                        "We appreciate your interest in our program and would like to wish you all the best in your future endeavors. We are grateful for the opportunity "+
                        "to consider you for our program and encourage you to keep pursuing your goals and aspirations.<br /><br />" +
                        "Thank you again for considering our program and for the time you invested in your application. We hope you find success and fulfillment in your academic and professional pursuits." +
                        " To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

                emailService.sendEmail(mentor.getProfile().getEmail(), StringUtils.capitalize(mentor.getState().name()), message, false);

                SentEmail email = new SentEmail();
                email.setEmail(mentor.getProfile().getEmail());
                email.setMessage(message);
                email.setProgramId(program.get());
                email.setReceiver(mentor.getProfile());
                email.setState(program.get().getState());
                emailRepository.save(email);

            }
        }
    }

    public void sendMenteeFiltrationEmails(long id, Optional<Program> program) throws MessagingException, IOException {
        List<Mentee> mentees = getMenteesWithoutDuplicatesByProgramId(id);
        // Notify mentees
        for (Mentee mentee : mentees) {
            String message = "Dear " + mentee.getProfile().getName() + ",<br /><br />" +
                    "Thank you very much for applying to the " + program.get().getTitle() + " program. Your application has been received. " +
                    "Mentors will soon review your applications and we will keep you posted on the progress via email. " +
                    "Until then, read more about student experience <a href=\"https://medium.com/search?q=scholarx\">here</a> and reach out to us via " +
                    "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> " +
                    "for any clarifications. " +
                    "To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, false);

            SentEmail email = new SentEmail();
            email.setEmail(mentee.getProfile().getEmail());
            email.setMessage(message);
            email.setProgramId(program.get());
            email.setReceiver(mentee.getProfile());
            email.setState(program.get().getState());
            emailRepository.save(email);
        }
    }

    public void sendMenteeSelectionEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        // Notify mentors
        for (Mentor mentor : approvedMentors) {

            String message = "Dear " + mentor.getProfile().getName() + ",<br /><br />" +

                    "It is with much pleasure we inform you that we have completed the first round of mentor-mentee matching.<br />" +
                    "There are a few points that we would like to share with you with regard to the mentee applications.<br />"+
                    "<ul>"+
                    "<li>We would like to emphasize that some of the content in the mentee applications might be generated by an AI. "+
                    "While we have made efforts to ensure the quality and relevance of the generated content, "+
                    "we recommend reviewing the applications carefully to evaluate the mentees' suitability for the program.</li>"+
                    "<li>The minimum word limit for each question of the application was introduced halfway through the application "+
                    "period in order to enhance competitiveness. Due to this reason, the applications which were received at the earliest"+
                    " stage might contain relatively short answers.</li>"+
                    "<li>Mentor-mentee matching process consists of two rounds.</li>"+
                    "<li>Some mentors have received an excess number of applications and we have selected potential mentees for those mentors "+
                    "after a filtering process. We kindly request you to go through these applications and select the mentees of your choice "+
                    "and decline the mentees who are not commendable enough before 26th May 11.59 p.m (IST), so that we can replace them during "+
                    "the second round of matching.</li>"+
                    "<li>Some mentors have received less than the number of available slots or none, owing to facts such as lack of compatibility "+
                    "of subject areas, more mentors representing the same subject area etc. We will be choosing the potential mentees for those mentors "+
                    "as well during the second round of matching.</li><br />"+
                    "We appreciate your enthusiasm in being a part of this journey and kindly request your cooperation in completing this matching process as well.<br />"+
                    "If you have any further queries please don't hesitate to contact us at "+
                    "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a><br />" +
                    " To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message, true);

            SentEmail email = new SentEmail();
            email.setEmail(mentor.getProfile().getEmail());
            email.setMessage(message);
            email.setProgramId(program.get());
            email.setReceiver(mentor.getProfile());
            email.setState(program.get().getState());
            emailRepository.save(email);
        }
    }

    public void sendOnGoingEmails(long id, Optional<Program> program) throws IOException, MessagingException {
        List<Mentor> approvedMentors = mentorRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        List<Mentee> approvedMentees = menteeRepository.findAllByProgramIdAndState(id, EnrolmentState.APPROVED);
        List<Mentee> discardedMentees = menteeRepository.findAllByProgramIdAndState(id, EnrolmentState.FAILED_FROM_WILDCARD);

        for (Mentor mentor : approvedMentors) {

            String message = "Dear " + mentor.getProfile().getName() + ",<br /><br />" +
                    "<b>Congratulations!</b><br />Your list of students is now finalised. " +
                    "You can check your mentees and their contact details by visiting the <b>ScholarX dashboard.</b> " +
                    "Please make the first contact with them as we have instructed them to wait for your email. " +
                    "To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

            String logMsg = "Email sent to mentor " + mentor.getProfile().getName() + " " +
                            "of " + mentor.getProfile().getEmail();
            log.info(logMsg);

            emailService.sendEmail(mentor.getProfile().getEmail(), program.get().getTitle(), message, true);

            SentEmail email = new SentEmail();
            email.setEmail(mentor.getProfile().getEmail());
            email.setMessage(message);
            email.setProgramId(program.get());
            email.setReceiver(mentor.getProfile());
            email.setState(program.get().getState());
            emailRepository.save(email);
        }

        for (Mentee mentee: approvedMentees) {
            String message = "Dear " + mentee.getProfile().getName() + ",<br /><br />" +
            "We are delighted to inform you that you have been selected for our undergraduate program, and we extend our heartfelt congratulations to you!"+ "<br /><br />"+
            "We received a large number of qualified applicants, and after a thorough review of all candidates, we are thrilled to offer you a place in our program. Your application stood out amongst the others, and we are confident that you will contribute positively to our program." + "<br /><br />"+
            "We believe that you have great potential to succeed in your academic and professional pursuits, and we are excited to have you join our community of learners and scholars." + "<br /><br />"+
            "To emphasize the importance of completing the program, you have received a valuable opportunity. If, for any reason, you are uncertain about completing the program within the 6-month timeline, please inform our admissions team as soon as possible, so we can provide the opportunity to another deserving student."+"<br /><br />"+
            "Once again, congratulations on your selection! We cannot wait to have you on board. " +
            "To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

            String logMsg = "Email sent to mentee " + mentee.getProfile().getName() + " " +
                        "of " + mentee.getProfile().getEmail();

            log.info(logMsg);

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, true);

            SentEmail email = new SentEmail();
            email.setEmail(mentee.getProfile().getEmail());
            email.setMessage(message);
            email.setProgramId(program.get());
            email.setReceiver(mentee.getProfile());
            email.setState(program.get().getState());
            emailRepository.save(email);
        }

        for (Mentee mentee: discardedMentees) {
            String message = "Dear " + mentee.getProfile().getName() + ",<br /><br />" +
            "We wanted to take a moment to thank you for your interest in the ScholarX program and for submitting your application. We appreciate the time and effort you put into it."+ "<br /><br />" +
            "After a careful review of your application and considering all of the candidates, we regret to inform you that we are unable to offer you admission at this time. We received a large number of qualified applicants, and unfortunately, we could only accept a limited number of students." + "<br /><br />" +
            "However, we want to encourage you not to be discouraged by this decision. We recognize that the admissions process can be competitive, and we understand that this news may be disappointing. Please know that this does not reflect on your abilities, potential, or value as an individual." + "<br /><br />" +
            "We do offer the possibility for you to apply again next year if you meet the eligibility criteria. We invite you to stay engaged with us by attending our events, reaching out to our admissions team, and taking advantage of any opportunities to connect with our current students and alumni." + "<br /><br />" +
            "Thank you again for considering our program and for the time you invested in your application. We wish you all the best in your future endeavours." +
            " To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

            String logMsg = "Email sent to mentee " + mentee.getProfile().getName() + " " +
                            "of " + mentee.getProfile().getEmail();
            log.info(logMsg);

            emailService.sendEmail(mentee.getProfile().getEmail(), program.get().getTitle(), message, true);

            SentEmail email = new SentEmail();
            email.setEmail(mentee.getProfile().getEmail());
            email.setMessage(message);
            email.setProgramId(program.get());
            email.setReceiver(mentee.getProfile());
            email.setState(program.get().getState());
            emailRepository.save(email);
        }
    }

    public void sendConfirmationEmails(long profileId, Optional<Program> program) throws MessagingException, IOException {
        Optional<Profile> profile = profileRepository.findById(profileId);
        String message = "Dear " + profile.get().getName() + ",<br /><br />" +
                "Thank you very much for applying to the " + program.get().getTitle() + " program. Your application has been received. " +
                "You can view/edit your application by visiting the <b>ScholarX dashboard.</b> " +
                "Reach out to us via " +
                "<a href=\"mailto:sustainableedufoundation@gmail.com\">sustainableedufoundation@gmail.com</a> " +
                "for any clarifications. " +
                "To ensure that you receive our emails and they do not go to your spam folder, please add sustainableedufoundation@gmail.com to your email whitelist.";

        emailService.sendEmail(profile.get().getEmail(), program.get().getTitle(), message, true);

        SentEmail email = new SentEmail();
        email.setEmail(profile.get().getEmail());
        email.setMessage(message);
        email.setProgramId(program.get());
        email.setReceiver(profile.get());
        email.setState(program.get().getState());
        emailRepository.save(email);
    }

    /**
     * Removes mentee duplicates
     */
    private List<Mentee> getMenteesWithoutDuplicatesByProgramId(long id) {
        List<Mentee> output = new ArrayList<>();
        List<Long> idList = new ArrayList<>();
        for (Mentee mentee: menteeRepository.findAllByProgramId(id)) {
            if (!idList.contains(mentee.getProfile().getId())) {
                idList.add(mentee.getProfile().getId());
                output.add(mentee);
            }
        }
        return output;
    }
}
