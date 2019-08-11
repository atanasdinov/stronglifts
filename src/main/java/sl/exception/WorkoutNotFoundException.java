package sl.exception;

import sl.model.Workout;

/**
 * Thrown if {@link Workout} does not exist.
 */
public class WorkoutNotFoundException extends RuntimeException {

    static final long serialVersionUID = -3387516993124229948L;

    public WorkoutNotFoundException(String message) {
        super(message);
    }

    public WorkoutNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkoutNotFoundException(Throwable cause) {
        super(cause);
    }
}
