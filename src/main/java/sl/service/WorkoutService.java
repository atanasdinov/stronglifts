package sl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import sl.constant.DayName;
import sl.constant.ExerciseName;
import sl.exception.UserNotFoundException;
import sl.model.Exercise;
import sl.model.User;
import sl.model.Workout;
import sl.model.WorkoutData;
import sl.repository.ExerciseRepository;
import sl.repository.UserRepository;
import sl.repository.WorkoutDataRepository;
import sl.repository.WorkoutRepository;
import sl.utility.WeightCalculator;

import javax.transaction.Transactional;
import java.util.*;

import static sl.constant.DayName.DAY_A;
import static sl.constant.DayName.DAY_B;
import static sl.constant.InitialWeight.INITIAL_DEADLIFT_WEIGHT_KG;
import static sl.constant.InitialWeight.INITIAL_WEIGHT_KG;

/**
 * <b>This service declares all manipulations that can be done on a {@link Workout}.</b>
 */
@Service
@Transactional
public class WorkoutService {

    private static final Logger logger = LoggerFactory.getLogger(WorkoutService.class);

    private UserRepository userRepository;
    private WorkoutRepository workoutRepository;
    private WorkoutDataRepository workoutDataRepository;
    private ExerciseRepository exerciseRepository;

    private Map<String, Exercise> exercises;

    @Autowired
    public WorkoutService(UserRepository userRepository, WorkoutRepository workoutRepository,
                          WorkoutDataRepository workoutDataRepository, ExerciseRepository exerciseRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutDataRepository = workoutDataRepository;
        this.exerciseRepository = exerciseRepository;
        this.exercises = ExerciseService.getExercises();
    }

    /**
     * This method is used to get {@link Workout} data for the next workout.
     *
     * @param token used for authentication of a given {@link User}
     * @return {@link Workout} containing the data for the next workout
     */
    public Workout getWorkout(String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("User not found by token."));

        Optional<Workout> lastWorkout = workoutRepository.getLastWorkout(user.getId());

        String dayName = Optional.of(lastWorkout)
                .map(lastWorkoutData -> lastWorkoutData
                        .filter(workOut -> DayName.DAY_A.equals(workOut.getDayName()))
                        .map(day -> DayName.DAY_B)
                        .orElse(DayName.DAY_A))
                .orElse(DayName.DAY_A);

        List<WorkoutData> workoutData = Optional.of(dayName)
                .filter(day -> day.equals(DayName.DAY_A))
                .map(dayA -> getDayAData(user.getId()))
                .orElse(getDayBData(user.getId()));

        Workout workout = new Workout();

        workout.setDayName(dayName);
        workout.setWorkoutData(workoutData);

        logger.info("Workout created.");
        return workout;
    }

    /**
     * This method is used to retrieve weights for different {@link Exercise}s in a {@link Workout} for {@link DayName} 'A'.
     *
     * @param userId of the user
     * @return list of {@link WorkoutData}
     */
    private List<WorkoutData> getDayAData(Long userId) {
        WorkoutData squatData = new WorkoutData(exercises.get(ExerciseName.SQUAT));
        WorkoutData benchPressData = new WorkoutData(exercises.get(ExerciseName.BENCH_PRESS));
        WorkoutData barbellRowData = new WorkoutData(exercises.get(ExerciseName.BARBELL_ROW));

        return workoutRepository.getPreviousWorkout(userId, DAY_A)
                .map(previousWorkout -> {
                    squatData.setWeight(WeightCalculator.calculate(ExerciseName.SQUAT, previousWorkout));
                    benchPressData.setWeight(WeightCalculator.calculate(ExerciseName.BENCH_PRESS, previousWorkout));
                    barbellRowData.setWeight(WeightCalculator.calculate(ExerciseName.BARBELL_ROW, previousWorkout));

                    return Arrays.asList(squatData, benchPressData, barbellRowData);
                })
                .orElseGet(() -> {
                    squatData.setWeight(INITIAL_WEIGHT_KG);
                    benchPressData.setWeight(INITIAL_WEIGHT_KG);
                    barbellRowData.setWeight(INITIAL_WEIGHT_KG);

                    return Arrays.asList(squatData, benchPressData, barbellRowData);

                });
    }

    /**
     * This method is used to retrieve weights for different {@link Exercise}s in a {@link Workout} for {@link DayName} 'B'.
     *
     * @param userId of the user
     * @return list of {@link WorkoutData}
     */
    private List<WorkoutData> getDayBData(Long userId) {
        WorkoutData squatData = new WorkoutData(exercises.get(ExerciseName.SQUAT));
        WorkoutData overheadPressData = new WorkoutData(exercises.get(ExerciseName.OVERHEAD_PRESS));
        WorkoutData deadliftData = new WorkoutData(exercises.get(ExerciseName.DEADLIFT));

        return workoutRepository.getPreviousWorkout(userId, DAY_B)
                .map(previousWorkout -> {
                    squatData.setWeight(WeightCalculator.calculate(ExerciseName.SQUAT, previousWorkout));
                    overheadPressData.setWeight(WeightCalculator.calculate(ExerciseName.OVERHEAD_PRESS, previousWorkout));
                    deadliftData.setWeight(WeightCalculator.calculate(ExerciseName.DEADLIFT, previousWorkout));

                    return Arrays.asList(squatData, overheadPressData, deadliftData);
                })
                .orElseGet(() -> {
                    squatData.setWeight(INITIAL_WEIGHT_KG + 2.5);
                    overheadPressData.setWeight(INITIAL_WEIGHT_KG);
                    deadliftData.setWeight(INITIAL_DEADLIFT_WEIGHT_KG);

                    return Arrays.asList(squatData, overheadPressData, deadliftData);
                });
    }

    /**
     * This method is used to save {@link Workout} data in the database.
     *
     * @param workoutDataList contains {@link WorkoutData} for the different exercises
     * @param dayName         name of the workout day
     * @param token           used for authentication
     * @return {@link ResponseStatus} 200 if data is saved successfully
     */
    public ResponseEntity completeWorkout(List<WorkoutData> workoutDataList, String dayName, String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("User not found by token."));

        workoutDataList.forEach(workoutData -> {
            Exercise exercise = exerciseRepository.findByName(workoutData.getExercise().getName());
            workoutData.setExercise(exercise);

            workoutDataRepository.save(workoutData);
        });

        Calendar cal = Calendar.getInstance();
        Workout workout = new Workout(dayName, workoutDataList, cal.getTime(), user);

        workoutRepository.save(workout);
        logger.info("Workout completed and saved.");

        return ResponseEntity.ok().build();
    }

    /**
     * This method is used to delete a {@link Workout}.
     *
     * @param workoutId of the {@link Workout}
     * @return {@link ResponseStatus} 200 if workout is deleted successfully
     */
    public ResponseEntity deleteWorkout(String workoutId) {
        workoutRepository.findById(Long.valueOf(workoutId))
                .ifPresent(workout -> workoutRepository.delete(workout));

        return ResponseEntity.ok().build();
    }

    /**
     * This method is used to retrieve data from last 3 {@link Workout}s of a {@link User}.
     *
     * @param token used for authentication
     * @return list of {@link Workout}
     */
    public List<Workout> retrieveHistory(String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("User not found by token."));

        List<Workout> history = workoutRepository.getHistory(user.getId());
        history.forEach(workout -> workout.setDisplayDate(workout.getDate().toLocaleString()));

        logger.info("Workout history retrieved");

        return history;
    }
}
