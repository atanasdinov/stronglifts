package sl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sl.constant.DayName;
import sl.model.User;
import sl.model.Workout;
import sl.model.WorkoutData;
import sl.service.WorkoutService;

import java.util.List;

/**
 * <b>Workout controller used to handle all logic that is workout related.</b>
 */
@RestController
@RequestMapping("/workout")
public class WorkoutController {

    @Autowired
    private WorkoutService workoutService;

    /**
     * This method is used to get workout data for the next workout of a given {@link User}.
     *
     * @param token of the authenticated {@link User}
     * @return {@link Workout} containing workout's data
     */
    @GetMapping("/get")
    public Workout getWorkout(@RequestParam String token) {
        return workoutService.getWorkout(token);
    }

    /**
     * This method submits {@link Workout} data upon workout completion.
     *
     * @param workoutData contains workout data
     * @param token       used for authentication
     * @param dayName     contains {@link DayName}
     * @return {@link ResponseStatus} 200 if successfully completed
     */
    @PostMapping("/complete")
    public ResponseEntity completeWorkout(@RequestBody List<WorkoutData> workoutData,
                                          @RequestParam(value = "token") String token,
                                          @RequestParam(value = "dayName") String dayName) {
        return ResponseEntity.ok(workoutService.completeWorkout(workoutData, dayName, token));
    }

    /**
     * This method deletes a {@link Workout}.
     *
     * @param id of the {@link Workout} to be deleted
     * @return {@link ResponseStatus} 200 if successfully completed
     */
    @DeleteMapping("/delete")
    public ResponseEntity deleteWorkout(@RequestParam(value = "workoutId") String id) {
        return ResponseEntity.ok(workoutService.deleteWorkout(id));
    }

    /**
     * This method returns the history of {@link User}'s workouts.
     *
     * @param token used for authentication
     * @return last 3 {@link Workout}s
     */
    @GetMapping("/history")
    public List<Workout> getWorkoutHistory(@RequestParam String token) {
        return workoutService.retrieveHistory(token);
    }
}
