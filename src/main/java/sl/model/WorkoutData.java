package sl.model;

import javax.persistence.*;

/**
 * <b>This entity contains {@link Workout}'s data.</b>
 */
@Entity
@Table(name = "workout_data")
public class WorkoutData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(targetEntity = Exercise.class, cascade = CascadeType.PERSIST)
    private Exercise exercise;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "reps")
    private Integer[] reps;

    public WorkoutData() {
    }

    public WorkoutData(Exercise exercise) {
        this.exercise = exercise;
    }

    public WorkoutData(Exercise exercise, Double weight, Integer[] reps) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer[] getReps() {
        return reps;
    }

    public void setReps(Integer[] reps) {
        this.reps = reps;
    }
}
