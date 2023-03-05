package org.sefglobal.scholarx.controller.admin;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentor;
import org.sefglobal.scholarx.service.MentorService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("MentorAdminController")
@RequestMapping("/api/admin/mentors")
public class MentorController {

    private final MentorService mentorService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Mentor> getAllMentors() {
        return mentorService.getAllMentors();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mentor getMentorById(@PathVariable long id)
            throws ResourceNotFoundException {
        return mentorService.getMentorById(id);
    }

    @PutMapping("/{id}/state")
    @ResponseStatus(HttpStatus.OK)
    public Mentor updateState(@PathVariable long id,
                              @Valid @RequestBody Map<String, EnrolmentState> payload)
            throws ResourceNotFoundException, BadRequestException {
        return mentorService.updateState(id, payload.get("state"));
    }
}
