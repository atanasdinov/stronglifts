
import com.scalefocus.sl.constant.DayName;
import com.scalefocus.sl.enumeration.ExerciseName;
import com.scalefocus.sl.exception.UserNotFoundException;
import com.scalefocus.sl.model.Exercise;
import com.scalefocus.sl.model.User;
import com.scalefocus.sl.model.Workout;
import com.scalefocus.sl.model.WorkoutData;
import com.scalefocus.sl.repository.ExerciseRepository;
import com.scalefocus.sl.repository.UserRepository;
import com.scalefocus.sl.repository.WorkoutDataRepository;
import com.scalefocus.sl.repository.WorkoutRepository;
import com.scalefocus.sl.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.scalefocus.sl.constant.DayName.DAY_A;
import static com.scalefocus.sl.constant.DayName.DAY_B;
import static com.scalefocus.sl.enumeration.ExerciseName.*;
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

        assertEquals(ExerciseName.SQUAT.getValue(), workout.getWorkoutData().get(0).getExercise().getName());
        assertEquals(ExerciseName.BENCH_PRESS.getValue(), workout.getWorkoutData().get(1).getExercise().getName());
        assertEquals(ExerciseName.BARBELL_ROW.getValue(), workout.getWorkoutData().get(2).getExercise().getName());

    }

    @Test
    void getWorkout_withPrevWorkout_DayA() {
        User user = new User("user", "user");
        user.setId(1L);

        List<WorkoutData> prevWorkoutData = new ArrayList<>();
        prevWorkoutData.add(new WorkoutData(new Exercise(ExerciseName.get(SQUAT.getValue()).toString()), 20.0, null));

        Workout prevWorkout = new Workout();
        prevWorkout.setDayName(DAY_A);
        prevWorkout.setWorkoutData(prevWorkoutData);

        when(userRepository.findByToken("token")).thenReturn(Optional.of(user));
        when(workoutRepository.getLastWorkout(anyLong())).thenReturn(Optional.of(prevWorkout));

        Workout workout = workoutService.getWorkout("token");

        assertEquals(DayName.DAY_B, workout.getDayName());

        assertEquals(workout.getWorkoutData().get(0).getWeight() - 2.5, (double) prevWorkout.getWorkoutData().get(0).getWeight());

        assertEquals(ExerciseName.SQUAT.getValue(), workout.getWorkoutData().get(0).getExercise().getName());
        assertEquals(ExerciseName.OVERHEAD_PRESS.getValue(), workout.getWorkoutData().get(1).getExercise().getName());
        assertEquals(ExerciseName.DEADLIFT.getValue(), workout.getWorkoutData().get(2).getExercise().getName());
    }

    @Test
    void getWorkout_withPrevWorkout_DayB() {
        User user = new User("user", "user");
        user.setId(1L);

        Workout prevWorkout = new Workout();
        prevWorkout.setDayName(DAY_B);

        when(userRepository.findByToken("token")).thenReturn(Optional.of(user));
        when(workoutRepository.getLastWorkout(anyLong())).thenReturn(Optional.of(prevWorkout));

        Workout workout = workoutService.getWorkout("token");

        assertEquals(DayName.DAY_A, workout.getDayName());

        assertEquals(ExerciseName.SQUAT.getValue(), workout.getWorkoutData().get(0).getExercise().getName());
        assertEquals(ExerciseName.BENCH_PRESS.getValue(), workout.getWorkoutData().get(1).getExercise().getName());
        assertEquals(ExerciseName.BARBELL_ROW.getValue(), workout.getWorkoutData().get(2).getExercise().getName());
    }

    @Test
    void getWorkout_nullUser() {
        when(userRepository.findByToken(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> workoutService.getWorkout("token"));
    }
}