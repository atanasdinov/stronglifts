import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import sl.service.WorkoutService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    WorkoutRepository workoutRepository;

    @Mock
    WorkoutDataRepository workoutDataRepository;

    @Mock
    ExerciseRepository exerciseRepository;

    @InjectMocks
    WorkoutService workoutService;

    @Test
    void getWorkout_withNullPrevWorkout() {
        User user = new User("user", "user");
        user.setId(1L);

        when(userRepository.findByToken("token")).thenReturn(Optional.of(user));
        when(workoutRepository.getLastWorkout(anyLong())).thenReturn(Optional.empty());

        Workout workout = workoutService.getWorkout("token");

        assertEquals(DayName.DAY_A, workout.getDayName());

        assertEquals(ExerciseName.SQUAT, workout.getWorkoutData().get(0).getExercise().getName());
        assertEquals(ExerciseName.BENCH_PRESS, workout.getWorkoutData().get(1).getExercise().getName());
        assertEquals(ExerciseName.BARBELL_ROW, workout.getWorkoutData().get(2).getExercise().getName());

    }

    @Test
    void getWorkout_withPrevWorkout_DayA() {
        User user = new User("user", "user");
        user.setId(1L);

        List<WorkoutData> prevWorkoutData = new ArrayList<>();
        prevWorkoutData.add(new WorkoutData(new Exercise(ExerciseName.SQUAT), 20.0, null));

        Workout prevWorkout = new Workout();
        prevWorkout.setDayName(DayName.DAY_A);
        prevWorkout.setWorkoutData(prevWorkoutData);

        when(userRepository.findByToken("token")).thenReturn(Optional.of(user));
        when(workoutRepository.getLastWorkout(anyLong())).thenReturn(Optional.of(prevWorkout));

        Workout workout = workoutService.getWorkout("token");

        assertEquals(DayName.DAY_B, workout.getDayName());

        assertEquals(workout.getWorkoutData().get(0).getWeight() - 2.5, (double) prevWorkout.getWorkoutData().get(0).getWeight());

        assertEquals(ExerciseName.SQUAT, workout.getWorkoutData().get(0).getExercise().getName());
        assertEquals(ExerciseName.OVERHEAD_PRESS, workout.getWorkoutData().get(1).getExercise().getName());
        assertEquals(ExerciseName.DEADLIFT, workout.getWorkoutData().get(2).getExercise().getName());
    }

    @Test
    void getWorkout_withPrevWorkout_DayB() {
        User user = new User("user", "user");
        user.setId(1L);

        Workout prevWorkout = new Workout();
        prevWorkout.setDayName(DayName.DAY_B);

        when(userRepository.findByToken("token")).thenReturn(Optional.of(user));
        when(workoutRepository.getLastWorkout(anyLong())).thenReturn(Optional.of(prevWorkout));

        Workout workout = workoutService.getWorkout("token");

        assertEquals(DayName.DAY_A, workout.getDayName());

        assertEquals(ExerciseName.SQUAT, workout.getWorkoutData().get(0).getExercise().getName());
        assertEquals(ExerciseName.BENCH_PRESS, workout.getWorkoutData().get(1).getExercise().getName());
        assertEquals(ExerciseName.BARBELL_ROW, workout.getWorkoutData().get(2).getExercise().getName());
    }

    @Test
    void getWorkout_nullUser() {
        when(userRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> workoutService.getWorkout("token"));
    }
}