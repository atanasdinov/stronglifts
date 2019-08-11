package sl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sl.model.WorkoutData;

/**
 * <b>This interface declares manipulations over the {@link WorkoutData} data in the database.</b>
 */
@Repository
public interface WorkoutDataRepository extends JpaRepository<WorkoutData, Long> {
}
