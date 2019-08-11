package sl.utility;

import org.apache.commons.lang3.StringUtils;
import sl.exception.WorkoutNotFoundException;
import sl.model.Exercise;
import sl.model.Workout;
import sl.model.WorkoutData;

import java.util.Optional;

import static sl.constant.ExerciseName.DEADLIFT;
import static sl.constant.ExerciseName.SQUAT;

public final class WeightCalculator {

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
                            String exName = Optional.of(workout)
                                    .map(WorkoutData::getExercise)
                                    .map(Exercise::getName)
                                    .orElse(StringUtils.EMPTY);

                            return exerciseName.equals(exName);
                        })
                        .findFirst())
                .orElseThrow(() -> new WorkoutNotFoundException("Workout data not found!"));

        return Optional.of(exerciseName)
                .filter(exName -> DEADLIFT.equals(exName) || SQUAT.equals(exName))
                .map(exercise -> workoutData.getWeight() + 5)
                .orElse(workoutData.getWeight() + 2.5);
    }

    private WeightCalculator() {
    }
}