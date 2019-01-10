package com.scalefocus.sl.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>This enum contains the exercises' names and their {@link String} value.</b>
 */
public enum ExerciseName {

    SQUAT("Squat"),

    BENCH_PRESS("Bench press"),

    DEADLIFT("Deadlift"),

    OVERHEAD_PRESS("Overhead press"),

    BARBELL_ROW("Barbell row");

    private String value;

    ExerciseName(String value) {
        this.value = value;
    }

    private static final Map<String, ExerciseName> exerciseMap = new HashMap<>();

    static {
        for (ExerciseName e : ExerciseName.values()) {
            exerciseMap.put(e.getValue(), e);
        }
    }

    public String getValue() {
        return value;
    }

    public static ExerciseName get(String value) {
        return exerciseMap.get(value);
    }
}
