package com.scalefocus.sl.service;

import com.scalefocus.sl.enumeration.ExerciseName;
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

    private ExerciseRepository exerciseRepository;

    @Autowired
    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * This method is used to add all {@link Exercise}'s to the database.
     */
    private static final Map<String, Exercise> exercises;

    static {
        exercises = new HashMap<>();
        exercises.put(ExerciseName.SQUAT.toString(), new Exercise(ExerciseName.SQUAT.getValue()));
        exercises.put(ExerciseName.OVERHEAD_PRESS.toString(), new Exercise(ExerciseName.OVERHEAD_PRESS.getValue()));
        exercises.put(ExerciseName.DEADLIFT.toString(), new Exercise(ExerciseName.DEADLIFT.getValue()));
        exercises.put(ExerciseName.BENCH_PRESS.toString(), new Exercise(ExerciseName.BENCH_PRESS.getValue()));
        exercises.put(ExerciseName.BARBELL_ROW.toString(), new Exercise(ExerciseName.BARBELL_ROW.getValue()));
    }


    public static Map<String, Exercise> getExercises() {
        return exercises;
    }

    @Bean
    public Void createDefaultExercises() {

        Optional.of(exerciseRepository.count())
                .filter(count -> count.equals(NumberUtils.LONG_ZERO))
                .ifPresent(count -> {
                    List<Exercise> exerciseList = new ArrayList<>();

                    exerciseList.add(exercises.get(ExerciseName.SQUAT.toString()));
                    exerciseList.add(exercises.get(ExerciseName.OVERHEAD_PRESS.toString()));
                    exerciseList.add(exercises.get(ExerciseName.DEADLIFT.toString()));
                    exerciseList.add(exercises.get(ExerciseName.BENCH_PRESS.toString()));
                    exerciseList.add(exercises.get(ExerciseName.BARBELL_ROW.toString()));

                    exerciseRepository.saveAll(exerciseList);
                    logger.info("Exercises created.");
                });

        return null;
    }
}
