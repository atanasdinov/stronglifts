package com.scalefocus.sl.exception;

/**
 * <b>This exception is used to indicate that the password from input form is incorrect.</b>
 */
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String s) {
        super(s);
    }
}
