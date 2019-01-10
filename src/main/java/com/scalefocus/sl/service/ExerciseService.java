package com.scalefocus.sl.service;

import com.scalefocus.sl.model.Exercise;
import com.scalefocus.sl.repository.ExerciseRepository;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.scalefocus.sl.enumeration.ExerciseName.*;

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
    @Bean
    public Void createDefaultExercises() {

        Optional.of(exerciseRepository.count())
                .filter(count -> count.equals(NumberUtils.LONG_ZERO))
                .ifPresent(count -> {
                    List<Exercise> exercises = new ArrayList<>();

                    exercises.add(new Exercise(SQUAT.toString()));
                    exercises.add(new Exercise(DEADLIFT.toString()));
                    exercises.add(new Exercise(BENCH_PRESS.toString()));
                    exercises.add(new Exercise(OVERHEAD_PRESS.toString()));
                    exercises.add(new Exercise(BARBELL_ROW.toString()));

                    exerciseRepository.saveAll(exercises);
                    logger.info("Exercises created.");
                });

        return null;
    }
}
