package org.sefglobal.scholarx.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class NoContentException extends APIException {

    public NoContentException() {
        super();
    }

    public NoContentException(String msg) {
        super(msg);
    }

    public NoContentException(String msg, Throwable e) {
        super(msg, e);
    }
}
