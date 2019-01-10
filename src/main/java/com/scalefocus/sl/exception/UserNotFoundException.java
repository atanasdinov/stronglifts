package com.scalefocus.sl.exception;

import com.scalefocus.sl.model.User;

/**
 * <b>This exception indicates {@link User} is not found with given credentials.</b>
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String s) {
        super(s);
    }
}
