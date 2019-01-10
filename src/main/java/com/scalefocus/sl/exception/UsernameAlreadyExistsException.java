package com.scalefocus.sl.exception;

import com.scalefocus.sl.model.User;

/**
 * <b>This exception is used to indicate that a {@link User} with given username already exists.</b>
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String s) {
        super(s);
    }
}
