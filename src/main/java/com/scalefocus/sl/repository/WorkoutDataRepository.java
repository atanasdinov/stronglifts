package com.scalefocus.sl.repository;

import com.scalefocus.sl.model.WorkoutData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <b>This interface declares manipulations over the {@link WorkoutData} data in the database.</b>
 */
@Repository
public interface WorkoutDataRepository extends JpaRepository<WorkoutData, Long> {
}
