package com.scalefocus.sl.service;

import com.scalefocus.sl.constant.DayName;
import com.scalefocus.sl.enumeration.ExerciseName;
import com.scalefocus.sl.exception.UserNotFoundException;
import com.scalefocus.sl.model.Exercise;
import com.scalefocus.sl.model.User;
import com.scalefocus.sl.model.Workout;
import com.scalefocus.sl.model.WorkoutData;
import com.scalefocus.sl.repository.UserRepository;
import com.scalefocus.sl.repository.WorkoutDataRepository;
import com.scalefocus.sl.repository.WorkoutRepository;
import com.scalefocus.sl.utility.WeightCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static com.scalefocus.sl.constant.DayName.DAY_A;
import static com.scalefocus.sl.constant.DayName.DAY_B;
import static com.scalefocus.sl.constant.InitialWeight.INITIAL_DEADLIFT_WEIGHT_KG;
import static com.scalefocus.sl.constant.InitialWeight.INITIAL_WEIGHT_KG;

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

    private Exercise squat = new Exercise(ExerciseName.SQUAT.getValue());
    private Exercise overheadPress = new Exercise(ExerciseName.OVERHEAD_PRESS.getValue());
    private Exercise deadlift = new Exercise(ExerciseName.DEADLIFT.getValue());
    private Exercise benchPress = new Exercise(ExerciseName.BENCH_PRESS.getValue());
    private Exercise barbellRow = new Exercise(ExerciseName.BARBELL_ROW.getValue());

    @Autowired
    public WorkoutService(UserRepository userRepository, WorkoutRepository workoutRepository,
                          WorkoutDataRepository workoutDataRepository) {
        this.userRepository = userRepository;
        this.workoutRepository = workoutRepository;
        this.workoutDataRepository = workoutDataRepository;
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
        Workout workout = new Workout();

        String dayName = Optional.of(lastWorkout)
                .map(lastWorkoutData -> lastWorkoutData
                        .filter(workOut -> DAY_A.equals(workOut.getDayName()))
                        .map(day -> DAY_B)
                        .orElse(DAY_A))
                .orElse(DAY_A);

        workout.setDayName(dayName);

        List<WorkoutData> workoutData = Optional.of(dayName)
                .filter(day -> day.equals(DAY_A))
                .map(dayA -> getDayAData(user.getId()))
                .orElse(getDayBData(user.getId()));

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
        WorkoutData squatData = new WorkoutData(squat);
        WorkoutData benchPressData = new WorkoutData(benchPress);
        WorkoutData barbellRowData = new WorkoutData(barbellRow);

        return workoutRepository.getPreviousWorkout(userId, DAY_A)
                .map(previousWorkout -> {

                    squatData.setWeight(WeightCalculator.calculate(ExerciseName.SQUAT.toString(), previousWorkout));
                    benchPressData.setWeight(WeightCalculator.calculate(ExerciseName.BENCH_PRESS.toString(), previousWorkout));
                    barbellRowData.setWeight(WeightCalculator.calculate(ExerciseName.BARBELL_ROW.toString(), previousWorkout));

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
        WorkoutData squatData = new WorkoutData(squat);
        WorkoutData overheadPressData = new WorkoutData(overheadPress);
        WorkoutData deadliftData = new WorkoutData(deadlift);

        return workoutRepository.getPreviousWorkout(userId, DAY_B)
                .map(previousWorkout -> {
                    squatData.setWeight(WeightCalculator.calculate(ExerciseName.SQUAT.toString(), previousWorkout));
                    overheadPressData.setWeight(WeightCalculator.calculate(ExerciseName.OVERHEAD_PRESS.toString(), previousWorkout));
                    deadliftData.setWeight(WeightCalculator.calculate(ExerciseName.DEADLIFT.toString(), previousWorkout));

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
            workoutData.getExercise().setName(ExerciseName.get(workoutData.getExercise().getName()).toString());

            workoutDataRepository.save(workoutData);
        });

        Calendar cal = Calendar.getInstance();
        Workout workout = new Workout(dayName, workoutDataList, cal.getTime(), user);

        workoutRepository.save(workout);
        logger.info("Workout completed and saved.");

        return ResponseEntity.ok().build();
    }

    public ResponseEntity deleteWorkout(String id) {
        workoutRepository.findById(Long.valueOf(id))
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
