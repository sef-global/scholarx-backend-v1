package org.sefglobal.scholarx.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthLoginController {
    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String prevUrl = request.getHeader("Referer");
        request.getSession().setAttribute("prev_url", prevUrl);
        String[] urlSplitArray = request.getRequestURL().toString().split("/");
        String redirectUrl = urlSplitArray[0] + "//" + urlSplitArray[2] + "/oauth2/authorization/google";
        response.sendRedirect(redirectUrl);
    }
}
