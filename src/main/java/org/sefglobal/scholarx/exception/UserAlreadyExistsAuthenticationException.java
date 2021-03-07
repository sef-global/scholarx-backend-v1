package org.sefglobal.scholarx.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserAlreadyExistsAuthenticationException extends AuthenticationException {

    public UserAlreadyExistsAuthenticationException(String msg) {
        super(msg);
    }

    public UserAlreadyExistsAuthenticationException(String msg, Throwable e) {
        super(msg, e);
    }
}
