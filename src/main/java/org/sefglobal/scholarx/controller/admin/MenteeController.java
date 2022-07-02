package org.sefglobal.scholarx.controller.admin;

import org.sefglobal.scholarx.exception.BadRequestException;
import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.model.Mentee;
import org.sefglobal.scholarx.service.MenteeService;
import org.sefglobal.scholarx.util.EnrolmentState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController("MenteeAdminController")
@RequestMapping("/api/admin/mentees")
public class MenteeController {

    private final MenteeService menteeService;

    public MenteeController(MenteeService menteeService) {
        this.menteeService = menteeService;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(@PathVariable long id)
            throws ResourceNotFoundException {
        menteeService.deleteMentee(id);
    }
    
    @PutMapping("/{id}/assign")
    @ResponseStatus(HttpStatus.OK)
    public Mentee updateAssignedMentor(@PathVariable long id,
                                       @Valid @RequestBody Map<String, Long> payload)
            throws ResourceNotFoundException, BadRequestException {
        return menteeService.updateAssignedMentor(id, payload.get("mentorId"));
    }

    @PutMapping("/{id}/state")
    public Mentee changeState(@PathVariable long id,
                              @Valid @RequestBody Map<String, EnrolmentState> payload)
            throws ResourceNotFoundException, BadRequestException {
        return menteeService.changeState(id, payload.get("state"));
    }
}
