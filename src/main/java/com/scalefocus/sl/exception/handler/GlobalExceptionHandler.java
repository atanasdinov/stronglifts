package com.scalefocus.sl.exception.handler;

import com.scalefocus.sl.exception.EmptyUserDataException;
import com.scalefocus.sl.exception.IncorrectPasswordException;
import com.scalefocus.sl.exception.UserNotFoundException;
import com.scalefocus.sl.exception.WorkoutNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <b>A dedicated global exception handler class for handling specific exceptions.</b>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmptyUserDataException.class)
    @ResponseBody
    public ResponseEntity handleEmptyUserData(EmptyUserDataException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ResponseEntity handleUserNotFound(UserNotFoundException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseBody
    public ResponseEntity handleIncorrectPassword(IncorrectPasswordException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(WorkoutNotFoundException.class)
    @ResponseBody
    public ResponseEntity handleWorkoutNotFound(WorkoutNotFoundException e) {
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
