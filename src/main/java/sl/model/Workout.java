package sl.model;


import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * <b>This entity contains {@link Workout} information.</b>
 */
@Entity
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "day_name")
    private String dayName;

    @Column(name = "date")
    private Date date;

    @ManyToMany(targetEntity = WorkoutData.class)
    private List<WorkoutData> workoutData;

    @ManyToOne(targetEntity = User.class)
    private User user;

    @Transient
    private String displayDate;

    public Workout(String dayName, List<WorkoutData> workoutData, Date date, User user) {
        this.dayName = dayName;
        this.workoutData = workoutData;
        this.date = date;
        this.user = user;
    }

    public Workout() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public List<WorkoutData> getWorkoutData() {
        return workoutData;
    }

    public void setWorkoutData(List<WorkoutData> workoutData) {
        this.workoutData = workoutData;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }
}
