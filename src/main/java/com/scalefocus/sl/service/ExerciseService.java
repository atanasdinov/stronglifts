package com.scalefocus.sl.service;

import com.scalefocus.sl.constant.ExerciseName;
import com.scalefocus.sl.model.Exercise;
import com.scalefocus.sl.repository.ExerciseRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 * <b>This service declares all manipulations that can be done on a {@link Exercise}.</b>
 */
@Service
@Transactional
public class ExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    private static final Map<String, Exercise> exercises;

    static {
        exercises = new HashMap<>();
        exercises.put(ExerciseName.SQUAT, new Exercise(ExerciseName.SQUAT));
        exercises.put(ExerciseName.OVERHEAD_PRESS, new Exercise(ExerciseName.OVERHEAD_PRESS));
        exercises.put(ExerciseName.DEADLIFT, new Exercise(ExerciseName.DEADLIFT));
        exercises.put(ExerciseName.BENCH_PRESS, new Exercise(ExerciseName.BENCH_PRESS));
        exercises.put(ExerciseName.BARBELL_ROW, new Exercise(ExerciseName.BARBELL_ROW));
    }

    @Autowired
    private ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public static Map<String, Exercise> getExercises() {
        return exercises;
    }

    /**
     * This method is used to add all {@link Exercise}'s to the database.
     */
    @Bean
    public Void createDefaultExercises() {

        Optional.of(exerciseRepository.count())
                .filter(count -> count.equals(NumberUtils.LONG_ZERO))
                .ifPresent(count -> {
                    List<Exercise> exerciseList = new ArrayList<>();

                    exerciseList.add(exercises.get(ExerciseName.SQUAT));
                    exerciseList.add(exercises.get(ExerciseName.OVERHEAD_PRESS));
                    exerciseList.add(exercises.get(ExerciseName.DEADLIFT));
                    exerciseList.add(exercises.get(ExerciseName.BENCH_PRESS));
                    exerciseList.add(exercises.get(ExerciseName.BARBELL_ROW));

                    exerciseRepository.saveAll(exerciseList);
                    logger.info("Exercises created.");
                });

        return null;
    }
}
