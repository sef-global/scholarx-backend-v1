package org.sefglobal.scholarx.controller.admin;

import org.sefglobal.scholarx.exception.ResourceNotFoundException;
import org.sefglobal.scholarx.service.MenteeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/mentees")
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
}
