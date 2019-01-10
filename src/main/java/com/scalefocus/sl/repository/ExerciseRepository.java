package com.scalefocus.sl.repository;

import com.scalefocus.sl.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <b>This interface declares manipulations over the {@link Exercise} data in the database.</b>
 */
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * This method is used to get {@link Exercise} by its name.
     *
     * @param name {@link Exercise}'s name
     * @return {@link Exercise}
     */
    @Query(value = "select * from exercises where name=:name", nativeQuery = true)
    Exercise findByName(@Param("name") String name);
}
