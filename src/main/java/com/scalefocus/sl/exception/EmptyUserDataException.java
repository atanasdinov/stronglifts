package com.scalefocus.sl.exception;

/**
 * This exception is used to indicate there are empty fields in the input form.
 */
public class EmptyUserDataException extends RuntimeException {
    public EmptyUserDataException(String s) {
        super(s);
    }
}
