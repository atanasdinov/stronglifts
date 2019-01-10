package com.scalefocus.sl.repository;

import com.scalefocus.sl.model.User;
import com.scalefocus.sl.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <b>This interface declares manipulations over the {@link Workout} data in the database.</b>
 */
@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    /**
     * This method is used to get the last {@link Workout} of a given {@link User}.
     *
     * @param userId id of the user
     * @return {@link Workout} if exists
     */
    @Query(value = "select * from workouts where user_id=:userId order by id desc limit 1", nativeQuery = true)
    Optional<Workout> getLastWorkout(@Param("userId") Long userId);

    /**
     * This method is used to get the previous {@link Workout} of a given {@link User} by a given day.
     *
     * @param userId  id of the user
     * @param dayName name of the workout day
     * @return {@link Workout} if exists
     */
    @Query(value = "select * from workouts where user_id=:userId and day_name=:dayName order by id desc limit 1", nativeQuery = true)
    Optional<Workout> getPreviousWorkout(@Param("userId") Long userId, @Param("dayName") String dayName);

    /**
     * This method is used to retrieve the workout history of a {@link User} (last 3 {@link Workout}s).
     *
     * @param userId id of the user
     * @return list of workouts
     */
    @Query(value = "select * from workouts where user_id=:userId order by id desc limit 3", nativeQuery = true)
    List<Workout> getHistory(@Param("userId") Long userId);
}
