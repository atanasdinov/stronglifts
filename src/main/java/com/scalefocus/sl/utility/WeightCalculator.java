package com.scalefocus.sl.utility;

import com.scalefocus.sl.exception.WorkoutNotFoundException;
import com.scalefocus.sl.model.Exercise;
import com.scalefocus.sl.model.Workout;
import com.scalefocus.sl.model.WorkoutData;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.scalefocus.sl.enumeration.ExerciseName.DEADLIFT;
import static com.scalefocus.sl.enumeration.ExerciseName.SQUAT;

public class WeightCalculator {

    /**
     * This method contains the logic of weight calculation for different {@link Exercise}s.
     *
     * @param exerciseName    name of the exercise
     * @param previousWorkout contains {@link WorkoutData} of a previous {@link Workout}
     * @return weight for a given {@link Exercise}
     */
    public static double calculate(String exerciseName, Workout previousWorkout) {
        WorkoutData workoutData = Optional.of(previousWorkout)
                .map(Workout::getWorkoutData)
                .flatMap(workoutDataList -> workoutDataList.stream()
                        .filter(workout -> {
                            String exercise = Optional.of(workout)
                                    .map(WorkoutData::getExercise)
                                    .map(Exercise::getName)
                                    .orElse(StringUtils.EMPTY);

                            return exerciseName.equals(exercise);
                        })
                        .findFirst())
                .orElseThrow(() -> new WorkoutNotFoundException("Workout data not found!"));

        return Optional.of(exerciseName)
                .filter(exercise -> DEADLIFT.toString().equals(exercise) || SQUAT.toString().equals(exercise))
                .map(exercise -> workoutData.getWeight() + 5)
                .orElse(workoutData.getWeight() + 2.5);

    }
}
